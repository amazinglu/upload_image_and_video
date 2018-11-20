package com.amazing.upload_image_and_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaUploadManager {

    public static final String MEDIA_STORE = "image_store";

    private Uri localUri;
    private Uri localVideoUri;
    private Context context;

    private static MediaUploadManager instance;

    private MediaUploadManager(Context context) {
        this.context = context;
    }

    public static MediaUploadManager getInstance(Context context) {
        if (instance == null) {
            instance = new MediaUploadManager(context);
        }
        return instance;
    }

    /**
     * load the image base on "uri" and set the image on image view
     * @param uri the origin url of the image file
     * @return local url of the file
     * */
    public Uri loadImage(Uri uri) throws IOException {
        return getImageContextWithAuthority(uri);

//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), localUri);
//        imageView.setImageBitmap(bitmap);
    }

    public Uri loadVideo(Uri uri) throws IOException {
        return getVideoContextWithAuthority(uri);
    }

    /**
     * down load and store the image file from other apps (mostly gallery)
     *
     * run in worker thread
     * @param uri the origin url of the image file
     * @return the local url of the image file
     * */
    private Uri getImageContextWithAuthority(Uri uri) throws IOException {
        if (uri.getAuthority() != null) {
            /**
             * use content provider to get the bitmap file of the media from other app
             * */
            File curImageFile = new File(uri.getPath());

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap mediaBitmap = BitmapFactory.decodeStream(inputStream);

            if (mediaBitmap == null) {
                Log.e(MEDIA_STORE, "bitmap is null");
                return null;
            }

            return writeImageBitMapToExternalStorage(mediaBitmap, "my_pictures",
                    curImageFile.getName() + "_edit");
        }
        return null;
    }

    /**
     * down load and store the video file from other apps
     * better to run this in worker thread since the video file can be large
     * */
    private Uri getVideoContextWithAuthority(Uri uri) throws IOException {
        if (uri.getAuthority() != null) {
            /**
             * use content provider to get the bitmap file of the media from other app
             * */

            File currentFile = new File(uri.getPath());
            String curName = currentFile.getName();

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File outputDir = getPublicVideoStorageDir("my_videos");
            File file = new File(outputDir, curName + "_edit");

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];

            /**
             * get the total length of the file
             * so that we can calculate the process of the download
             * */
            long totalLen = currentFile.length();

            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer);
            }

            inputStream.close();
            outputStream.close();

            localVideoUri = Uri.parse(file.getAbsolutePath());

            return localVideoUri;
        }
        return null;
    }

    /**
     * compress the bitmap file and save it to external storage
     * @param mediaBitmap bitmap file of the image
     * @param fileName the directory name of the image file
     * @return the local url of the image file
     *
     * comment code is to use MediaStore to store image file, the file will be store in externalStorageRoot/Pictures
     * in this way, the bitmap file can be read using
     * "MediaStore.Images.Media.getBitmap(context.getContentResolver(), localUri)"
     * also in this way the folder will be show in phone's gallery
     * */
    private Uri writeImageBitMapToExternalStorage(Bitmap mediaBitmap, String fileDirName, String fileName) throws IOException {
        File imageDir = getPublicImageStorageDir(fileDirName);
        File imageFile = new File(imageDir, fileName);
        /**
         * compress the bitmap file
         * */
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        mediaBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
        mediaBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

//        String path = MediaStore.Images.Media.insertImage(
//                context.getContentResolver(), mediaBitmap, "myImage", null);
//
//        return Uri.parse(path);
        fileOutputStream.flush();
        fileOutputStream.close();

        return Uri.parse(imageFile.getAbsolutePath());
    }

    public File getPublicImageStorageDir(String fileName) {
        if (!isExternalStorageReadable()) {
            Log.e(MEDIA_STORE, "can not read from external storage");
            return null;
        }
        if (!isExternalStorageWritable()) {
            Log.e(MEDIA_STORE, "can not write to external storage");
            return null;
        }

        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), fileName);
        if (!file.mkdirs()) {
            Log.e(MEDIA_STORE, "Directory not created");
        }
        return file;
    }

    public File getPublicVideoStorageDir(String fileName) {
        if (!isExternalStorageReadable()) {
            Log.e(MEDIA_STORE, "can not read from external storage");
            return null;
        }
        if (!isExternalStorageWritable()) {
            Log.e(MEDIA_STORE, "can not write to external storage");
            return null;
        }

        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), fileName);
        if (!file.mkdirs()) {
            Log.e(MEDIA_STORE, "Directory not created");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
