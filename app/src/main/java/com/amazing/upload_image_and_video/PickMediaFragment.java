package com.amazing.upload_image_and_video;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.amazing.upload_image_and_video.util.PermissionUtil;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PickMediaFragment extends Fragment {

    private static final int REQ_CODE_PICK_MEDIA = 10;

    private ImageView imageHolder;
    private Button pickMedia;
    private VideoView videoHolder;

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

                try {
                    loadVideo(mediaUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    loadImage(mediaUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePickMediaClick() {
        if (checkPermission()) {
            pickMediaFromResource();
        }
    }

    private Uri loadVideo(Uri mediaUri) throws IOException {
        Uri localVideoUri = mediaUploadManager.loadVideo(mediaUri);

        videoHolder.setVideoURI(localVideoUri);
        videoHolder.requestFocus();
        videoHolder.seekTo(1);

        return localVideoUri;
    }

    private void loadImage(Uri mediaUri) throws IOException {
        Uri localImageUri = mediaUploadManager.loadImage(mediaUri);
        imageHolder.setImageURI(localImageUri);
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
}
