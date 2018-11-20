package com.amazing.upload_image_and_video;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;
import com.amazing.upload_image_and_video.util.PermissionUtil;

import java.io.File;
import java.io.IOException;

public class PickMediaFragment extends Fragment {

    private static final int REQ_CODE_PICK_MEDIA = 10;
    private static final int REQ_CODE_PLAY_VIDEO = 11;

    private ImageView imageHolder;
    private Button pickMedia;
    private VideoView videoHolder;

    private Uri videoLocalUri;

    private MediaUploadManager mediaUploadManager;

    public static PickMediaFragment newInstance() {
        return new PickMediaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_media, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageHolder = view.findViewById(R.id.media_holder);
        videoHolder = view.findViewById(R.id.video_holder);
        pickMedia = view.findViewById(R.id.pick_media_button);

        pickMedia.setOnClickListener(v -> handlePickMediaClick());

        mediaUploadManager = MediaUploadManager.getInstance(getContext());

        videoHolder.setOnClickListener(view1 -> {
            if (videoLocalUri != null) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(videoLocalUri, "video/*");
                startActivityForResult(intent.createChooser(intent, "Select player"), REQ_CODE_PLAY_VIDEO);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoLocalUri != null) {
            videoHolder.setVideoURI(videoLocalUri);
            videoHolder.requestFocus();
            videoHolder.seekTo(1);
//            videoHolder.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == PermissionUtil.REQ_CODE_READ_EXTERNAL_STORAGE ||
                requestCode == PermissionUtil.REQ_CODE_WRITE_EXTERNAL_STORAGE)
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickMediaFromResource();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK_MEDIA && resultCode == Activity.RESULT_OK) {
            Uri mediaUri = data.getData();

            if (mediaUri.toString().toLowerCase().contains("video")) {
                loadVideo(mediaUri);
            } else {
                loadImage(mediaUri);
            }
        }
        if (requestCode == REQ_CODE_PLAY_VIDEO) {
        }
    }

    private void handlePickMediaClick() {
        if (checkPermission()) {
            pickMediaFromResource();
        }
    }

    private void loadVideo(Uri mediaUri) {
        LoadVideoTask loadVideoTask = new LoadVideoTask(mediaUri);
        loadVideoTask.execute();
    }

    private void loadImage(Uri mediaUri) {
        LoadImageTask loadImageTask = new LoadImageTask(mediaUri);
        loadImageTask.execute();
    }

    private void pickMediaFromResource() {

        Log.d(MediaUploadManager.MEDIA_STORE, "begin pickMediaFromResource: ");

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent.createChooser(intent, "Select media"), REQ_CODE_PICK_MEDIA);

        Log.d(MediaUploadManager.MEDIA_STORE, "send intent pickMediaFromResource: ");
    }

    private boolean checkPermission() {

        Log.d(MediaUploadManager.MEDIA_STORE, "begin checkPermission: ");

        boolean needReadExternalPermission = false;
        boolean needWriteExternalPermission = false;
        boolean needCameraPermission = false;
        if (!PermissionUtil.checkPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            needReadExternalPermission = true;
        }
        if (!PermissionUtil.checkPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            needWriteExternalPermission = true;
        }
        if (!PermissionUtil.checkPermission(getContext(), Manifest.permission.CAMERA)) {
            needCameraPermission = true;
        }

        if (needReadExternalPermission) {
            PermissionUtil.requestReadExternalStoragePermission(this);
        }
        if (needWriteExternalPermission) {
            PermissionUtil.requestWriteExternalStoragePermission(this);
        }
        if (needCameraPermission) {
            PermissionUtil.requestCameraPermission(this);
        }

        return !needReadExternalPermission && !needWriteExternalPermission;
    }

    /**
     * load image and video async
     * todo: add the progress bar to show the download progress
     * */
    class LoadImageTask extends AsyncTask<Void, Void, Uri> {

        private Uri originUri;

        public LoadImageTask(Uri originUri) {
            this.originUri = originUri;
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            try {
                return mediaUploadManager.loadImage(originUri);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri == null) {
                return;
            }
            imageHolder.setImageURI(uri);
        }
    }

    class LoadVideoTask extends AsyncTask<Void, Void, Uri> {

        private Uri originUri;

        public LoadVideoTask(Uri originUri) {
            this.originUri = originUri;
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            try {
                return mediaUploadManager.loadVideo(originUri);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                videoHolder.setVideoURI(uri);
                videoHolder.requestFocus();
                videoHolder.seekTo(1);
                videoLocalUri = uri;
            }
        }
    }
}
