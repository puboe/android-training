package com.mercadolibre.puboe.meli;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
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
    private URL mImageURL;

    /*
     * Creates a weak reference to the ImageView in this object. The weak
     * reference prevents memory leaks and crashes, because it automatically tracks the "state" of
     * the variable it backs. If the reference becomes invalid, the weak reference is garbage-
     * collected.
     * This technique is important for referring to objects that are part of a component lifecycle.
     * Using a hard reference may cause memory leaks as the value continues to change; even worse,
     * it can cause crashes if the underlying component is destroyed. Using a weak reference to
     * a View ensures that the reference is more transitory in nature.
     */
    private WeakReference<View> mThisView;

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
        setImageDrawable(null);
        mImageURL = null;

//        mIsDrawn = false;

        // Gets the current Drawable, or null if no Drawable is attached
        Drawable localDrawable = getDrawable();

        // if the Drawable is null, unbind it from this VIew
        if (localDrawable != null)
            localDrawable.setCallback(null);

        // If this View still exists, clears the weak reference, then sets the reference to null
        if (mThisView != null) {
            mThisView.clear();
            mThisView = null;
        }

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

    /**
     * Sets the visibility of the PhotoView
     * @param visState The visibility state (see View.setVisibility)
     */
    private void showView(int visState) {
        // If the View contains something
        if (mThisView != null) {

            // Gets a local hard reference to the View
            View localView = mThisView.get();

            // If the weak reference actually contains something, set the visibility
            if (localView != null)
                localView.setVisibility(visState);
        }
    }

    /**
     * Sets the image in this ImageView to null, and makes the View visible
     */
    public void clearImage() {
        setImageDrawable(null);
        showView(View.VISIBLE);
    }

    /**
     * Returns the URL of the picture associated with this ImageView
     * @return a URL
     */
    final URL getLocation() {
        return mImageURL;
    }

    /*
     * This callback is invoked when the system attaches the ImageView to a Window. The callback
     * is invoked before onDraw(), but may be invoked after onMeasure()
     */
    @Override
    protected void onAttachedToWindow() {
        // Always call the supermethod first
        super.onAttachedToWindow();

        // If the sibling View is set and the parent of the ImageView is itself a View
        if ((this.mHideShowResId != -1) && ((getParent() instanceof View))) {

            // Gets a handle to the sibling View
            View localView = ((View) getParent()).findViewById(this.mHideShowResId);

            // If the sibling View contains something, make it the weak reference for this View
            if (localView != null) {
                this.mThisView = new WeakReference<View>(localView);
            }
        }
    }

    /**
     * Sets the Drawable for this ImageView
     * @param drawable A Drawable to use for the ImageView
     */
    public void setStatusDrawable(Drawable drawable) {

        // If the View is empty, sets a Drawable as its content
        if (mThisView == null) {
            setImageDrawable(drawable);
        }
    }

    /**
     * Sets the content of this ImageView to be a Drawable resource
     * @param resId
     */
    public void setStatusResource(int resId) {

        // If the View is empty, provides it with a Drawable resource as its content
        if (mThisView == null) {
            setImageResource(resId);
        }
    }

    /**
     * Sets the current View weak reference to be the incoming View. See the definition of
     * mThisView
     * @param view the View to use as the new WeakReference
     */
    public void setHideView(View view) {
        this.mThisView = new WeakReference<View>(view);
    }

    @Override
    public void setImageBitmap(Bitmap paramBitmap) {
        super.setImageBitmap(paramBitmap);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // The visibility of the View
        int viewState;

        /*
         * Sets the View state to visible if the method is called with a null argument (the
         * image is being cleared). Otherwise, sets the View state to invisible before refreshing
         * it.
         */
        if (drawable == null) {

            viewState = View.VISIBLE;
        } else {

            viewState = View.INVISIBLE;
        }
        // Either hides or shows the View, depending on the view state
        showView(viewState);

        // Invokes the supermethod with the provided drawable
        super.setImageDrawable(drawable);
    }

    /*
     * Displays a drawable in the View
     */
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    /*
     * Sets the URI for the Image
     */
    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    public URL getImageURL() {
        return mImageURL;
    }

    public void setImageURL(URL mImageURL) {

        if (mImageURL != null && !mImageURL.equals(this.mImageURL)){
            this.mImageURL = mImageURL;

            if (!mIsDrawn) {
                PhotoManager.getInstance().startDownload(mImageURL, this);
                mIsDrawn = true;
            } else
                mIsDrawn = false;
        }



        // Sets the Drawable for this ImageView
//

        // Stores the picture URL for this ImageView
        mImageURL = mImageURL;

        // If the draw operation for this ImageVIew has completed, and the picture URL isn't empty
//        if ((mIsDrawn) && (mImageURL != null)) {

            /*
             * Starts a download of the picture file. Notice that if caching is on, the picture
             * file's contents may be taken from the cache.
             */

//        }
    }
}
