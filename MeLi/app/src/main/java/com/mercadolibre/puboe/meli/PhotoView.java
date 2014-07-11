package com.mercadolibre.puboe.meli;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.net.URL;

/**
 * Created by puboe on 11/07/14.
 */
public class PhotoView extends ImageView {

    // Status flag that indicates if onDraw has completed
    private boolean mIsDrawn;

    // Contains the ID of the internal View
    private int mHideShowResId = -1;

    // The URL that points to the source of the image for this ImageView
    private String mImageURL;

    /**
     * Creates an ImageDownloadView with no settings
     * @param context A context for the View
     */
    public PhotoView(Context context) {
        super(context);
    }

    /**
     * Creates an ImageDownloadView and gets attribute values
     * @param context A Context to use with the View
     * @param attributeSet The entire set of attributes for the View
     */
    public PhotoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Gets attributes associated with the attribute set
        getAttributes(attributeSet);
    }

    /**
     * Creates an ImageDownloadView, gets attribute values, and applies a default style
     * @param context A context for the View
     * @param attributeSet The entire set of attributes for the View
     * @param defaultStyle The default style to use with the View
     */
    public PhotoView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);

        // Gets attributes associated with the attribute set
        getAttributes(attributeSet);
    }

    /**
     * Gets the resource ID for the hideShowSibling resource
     * @param attributeSet The entire set of attributes for the View
     */
    private void getAttributes(AttributeSet attributeSet) {

        // Gets an array of attributes for the View
        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeSet, R.styleable.ImageDownloaderView);

        // Gets the resource Id of the View to hide or show
        mHideShowResId =
                attributes.getResourceId(R.styleable.ImageDownloaderView_hideShowSibling, -1);

        // Returns the array for re-use
        attributes.recycle();
    }

    /*
     * This callback is invoked when the ImageView is removed from a Window. It "unsets" variables
     * to prevent memory leaks.
     */
    @Override
    protected void onDetachedFromWindow() {

        // Clears out the image drawable, turns off the cache, disconnects the view from a URL
//        setImageURL(null, false, null);
        mImageURL = null;


        // Gets the current Drawable, or null if no Drawable is attached
        Drawable localDrawable = getDrawable();

        // if the Drawable is null, unbind it from this VIew
        if (localDrawable != null)
            localDrawable.setCallback(null);

        // If this View still exists, clears the weak reference, then sets the reference to null
//        if (mThisView != null) {
//            mThisView.clear();
//            mThisView = null;
//        }

//        // Sets the downloader thread to null
//        this.mDownloadThread = null;

        // Always call the super method last
        super.onDetachedFromWindow();
    }

    /*
     * This callback is invoked when the system tells the View to draw itself. If the View isn't
     * already drawn, and its URL isn't null, it invokes a Thread to download the image. Otherwise,
     * it simply passes the existing Canvas to the super method
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // If the image isn't already drawn, and the URL is set
        if ((!mIsDrawn) && (mImageURL != null)) {

            // Starts downloading this View, using the current cache setting
            PhotoManager.getInstance().startDownload(mImageURL, this);

            // After successfully downloading the image, this marks that it's available.
            mIsDrawn = true;
        }
        // Always call the super method last
        super.onDraw(canvas);
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }
}
