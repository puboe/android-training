package com.mercadolibre.puboe.meli.photomanager;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by puboe on 07/07/14.
 */
public class PhotoTask {

    private URL url;
    private PhotoManager photoManager;
    private WeakReference<PhotoView> imageWeakRef;
    private Bitmap decodedImage;
    private Runnable worker;

    public PhotoTask() {
        this.worker = new PhotoTaskRunnable(this);
    }

    public void initializeTask(URL url, PhotoView photoView, PhotoManager photoManager) {
        this.url = photoView.getLocation();
//        Log.w("initializeTask", url.toString());
        if(photoView == null) {
            Log.e("initializeTask", "photoView == NULL");
        }
        this.photoManager = photoManager;
        this.imageWeakRef = new WeakReference<PhotoView>(photoView);
    }

    void recycle() {

        // Deletes the weak reference to the imageView
        if ( null != imageWeakRef ) {
            imageWeakRef.clear();
            imageWeakRef = null;
        }

        // Releases references to the byte buffer and the BitMap
        decodedImage = null;
    }

    public URL getUrl() {
        return url;
    }

    public PhotoView getImageView() {
        if ( null != imageWeakRef ) {
            return imageWeakRef.get();
        }
        return null;
    }

    public Bitmap getImage() {
        return decodedImage;
    }

    public void setImage(Bitmap decodedImage) {
        this.decodedImage = decodedImage;
    }

    public Runnable getRunnable() {
        return worker;
    }

    public PhotoManager getPhotoManager() {
        return photoManager;
    }
}