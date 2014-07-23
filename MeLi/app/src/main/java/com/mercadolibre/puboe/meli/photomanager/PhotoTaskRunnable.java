package com.mercadolibre.puboe.meli.photomanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.EOFException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by puboe on 14/07/14.
 */
public class PhotoTaskRunnable implements Runnable {

    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 1000;
    private static final int READ_SIZE = 1024 * 2;
    private static final int NUMBER_OF_DECODE_TRIES = 2;
    private static final long SLEEP_TIME_MILLISECONDS = 250;
    private static final String LOG_TAG = "PhotoDecodeRunnable";
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android "
            + android.os.Build.VERSION.RELEASE + ";"
            + Locale.getDefault().toString() + "; " + android.os.Build.DEVICE
            + "/" + android.os.Build.ID + ")";

    private PhotoTask photoTask;
    private byte[] imageBuffer;

    public PhotoTaskRunnable(PhotoTask photoTask) {
        this.photoTask = photoTask;
    }

    @Override
    public void run() {
        Log.i("initializeTask", "Running PhotoTask runnable: " + hashCode());
        // Moves the current Thread into the background
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        imageBuffer = downloadImage(photoTask.getUrl());
        if(imageBuffer == null) {
            Log.e("imageNotDownloaded", "imageBuffer Null");
            photoTask.getPhotoManager().onCompleteTask(photoTask, PhotoManager.DOWNLOAD_FAILED);
        } else {
            photoTask.setImage(decodeImage(imageBuffer));
            if(photoTask.getImage() == null) {
                Log.e("imageNotDecoded", "decodedImage Null");
            }
            photoTask.getPhotoManager().onCompleteTask(photoTask, PhotoManager.TASK_COMPLETE);
        }
    }

    private byte[] downloadImage(URL url) {
        Log.i("downloadImage", url.toString());
        byte[] byteBuffer;

        // Defines a handle for the byte download stream
        InputStream byteStream = null;

        // Downloads the image and catches IO errors
        try {

//            Log.e("downloadImage", "Before opening connection");

            // Opens an HTTP connection to the image's URL
            HttpURLConnection httpConn =
                    (HttpURLConnection) url.openConnection();

//            Log.e("downloadImage", "Connection openned");

            // Sets the user agent to report to the server
            httpConn.setRequestProperty("User-Agent", USER_AGENT);

//            Log.e("downloadImage", "Before executing connection");

            // Gets the input stream containing the image
            byteStream = httpConn.getInputStream();

//            Log.e("downloadImage", "Connection executed");

            /*
            * Gets the size of the file being downloaded. This
                     * may or may not be returned.
                     */
            int contentSize = httpConn.getContentLength();

//            Log.e("downloadImage", "Reading contentLength: " + contentSize );

            /*
                     * If the size of the image isn't available
                     */
            if (-1 == contentSize) {
//                Log.e("downloadImage", "ContentLength not present");

                // Allocates a temporary buffer
                byte[] tempBuffer = new byte[READ_SIZE];

                // Records the initial amount of available space
                int bufferLeft = tempBuffer.length;

                        /*
                         * Defines the initial offset of the next available
                         * byte in the buffer, and the initial result of
                         * reading the binary
                         */
                int bufferOffset = 0;
                int readResult = 0;

                        /*
                         * The "outer" loop continues until all the bytes
                         * have been downloaded. The inner loop continues
                         * until the temporary buffer is full, and then
                         * allocates more buffer space.
                         */
                outer: do {
//                    Log.e("downloadImage", "Looping");
                    while (bufferLeft > 0) {
                                /*
                                 * Reads from the URL location into
                                 * the temporary buffer, starting at the
                                 * next available free byte and reading as
                                 * many bytes as are available in the
                                 * buffer.
                                 */
                        readResult = byteStream.read(tempBuffer, bufferOffset,
                                bufferLeft);

                                /*
                                 * InputStream.read() returns zero when the
                                 * file has been completely read.
                                 */
                        if (readResult < 0) {
                            // The read is finished, so this breaks
                            // the to "outer" loop
                            break outer;
                        }
                                /*
                                 * The read isn't finished. This sets the
                                 * next available open position in the
                                 * buffer (the buffer index is 0-based).
                                 */
                        bufferOffset += readResult;

                        // Subtracts the number of bytes read from
                        // the amount of buffer left
                        bufferLeft -= readResult;

                    }
                            /*
                             * The temporary buffer is full, so the
                             * following code creates a new buffer that can
                             * contain the existing contents plus the next
                             * read cycle.
                             */

                    // Resets the amount of buffer left to be the
                    // max buffer size
                    bufferLeft = READ_SIZE;

                            /*
                             * Sets a new size that can contain the existing
                             * buffer's contents plus space for the next
                             * read cycle.
                             */
                    int newSize = tempBuffer.length + READ_SIZE;

                            /*
                             * Creates a new temporary buffer, moves the
                             * contents of the old temporary buffer into it,
                             * and then points the temporary buffer variable
                             * to the new buffer.
                             */
                    byte[] expandedBuffer = new byte[newSize];
                    System.arraycopy(tempBuffer, 0, expandedBuffer, 0,
                            tempBuffer.length);
                    tempBuffer = expandedBuffer;
                } while (true);

                        /*
                         * When the entire image has been read, this creates
                         * a permanent byte buffer with the same size as
                         * the number of used bytes in the temporary buffer
                         * (equal to the next open byte, because tempBuffer
                         * is 0=based).
                         */
                byteBuffer = new byte[bufferOffset];

                // Copies the temporary buffer to the image buffer
                System.arraycopy(tempBuffer, 0, byteBuffer, 0, bufferOffset);

                        /*
                         * The download size is available, so this creates a
                         * permanent buffer of that length.
                         */
            } else {
//                Log.e("downloadImage", "ContentLength present");
                byteBuffer = new byte[contentSize];

                // How much of the buffer still remains empty
                int remainingLength = contentSize;

                // The next open space in the buffer
                int bufferOffset = 0;

                        /*
                         * Reads into the buffer until the number of bytes
                         * equal to the length of the buffer (the size of
                         * the image) have been read.
                         */
                while (remainingLength > 0) {
//                    Log.e("downloadImage", "Looping contentLength present");
                    int readResult = byteStream.read(
                            byteBuffer,
                            bufferOffset,
                            remainingLength);
                            /*
                             * EOF should not occur, because the loop should
                             * read the exact # of bytes in the image
                             */
                    if (readResult < 0) {

                        // Throws an EOF Exception
                        throw new EOFException();
                    }

                    // Moves the buffer offset to the next open byte
                    bufferOffset += readResult;

                    // Subtracts the # of bytes read from the
                    // remaining length
                    remainingLength -= readResult;
                }
            }
        } catch(Exception e) {
            Log.e("downloadImage", "catch exception: " + e);
            e.printStackTrace();
            return null;
        } finally {
            //If the input stream is still open, close it
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (Exception e) {

                }
            }

//            Log.e("downloadImage", "finally block");
        }

//        Log.e("downloadImage", "returning byteBuffer");
        return byteBuffer;
    }

    private Bitmap decodeImage(byte[] byteBuffer) {
        Log.i("decodeImage", "decoding");
        // Defines the Bitmap object that this thread will create
        Bitmap returnBitmap = null;

        /*
         * A try block that decodes a downloaded image.
         *
         */
        try {
            // Sets up options for creating a Bitmap object from the
            // downloaded image.
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            /*
             * Sets the desired image height and width based on the
             * ImageView being created.
             */
            int targetWidth = DEFAULT_WIDTH;
            int targetHeight = DEFAULT_HEIGHT;

            // Before continuing, checks to see that the Thread hasn't
            // been interrupted
            if (Thread.interrupted()) {
                return null;
            }

            /*
             * Even if the decoder doesn't set a Bitmap, this flag tells
             * the decoder to return the calculated bounds.
             */
            bitmapOptions.inJustDecodeBounds = true;

            /*
             * First pass of decoding to get scaling and sampling
             * parameters from the image
             */
            BitmapFactory
                    .decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);

            /*
             * Sets horizontal and vertical scaling factors so that the
             * image is expanded or compressed from its actual size to
             * the size of the target ImageView
             */
            int hScale = bitmapOptions.outHeight / targetHeight;
            int wScale = bitmapOptions.outWidth / targetWidth;

            /*
             * Sets the sample size to be larger of the horizontal or
             * vertical scale factor
             */
            //
            int sampleSize = Math.max(hScale, wScale);

            /*
             * If either of the scaling factors is > 1, the image's
             * actual dimension is larger that the available dimension.
             * This means that the BitmapFactory must compress the image
             * by the larger of the scaling factors. Setting
             * inSampleSize accomplishes this.
             */
            if (sampleSize > 1) {
                bitmapOptions.inSampleSize = sampleSize;
            }

            if (Thread.interrupted()) {
                return null;
            }

            // Second pass of decoding. If no bitmap is created, nothing
            // is set in the object.
            bitmapOptions.inJustDecodeBounds = false;

            /*
             * This does the actual decoding of the buffer. If the
             * decode encounters an an out-of-memory error, it may throw
             * an Exception or an Error, both of which need to be
             * handled. Once the problem is handled, the decode is
             * re-tried.
             */
            for (int i = 0; i < NUMBER_OF_DECODE_TRIES; i++) {
                try {
                    // Tries to decode the image buffer
                    returnBitmap = BitmapFactory.decodeByteArray(
                            imageBuffer,
                            0,
                            imageBuffer.length,
                            bitmapOptions
                    );
                    /*
                     * If the decode works, no Exception or Error has occurred.
                    break;

                    /*
                     * If the decode fails, this block tries to get more memory.
                     */
                } catch (Throwable e) {
                    // Logs an error
                    Log.e(LOG_TAG, "Out of memory in decode stage. Throttling.");
                    /*
                     * Tells the system that garbage collection is
                     * necessary. Notice that collection may or may not
                     * occur.
                     */
                    java.lang.System.gc();

                    if (Thread.interrupted()) {
                        return null;
                    }
                    /*
                     * Tries to pause the thread for 250 milliseconds,
                     * and catches an Exception if something tries to
                     * activate the thread before it wakes up.
                     */
                    try {
                        Thread.sleep(SLEEP_TIME_MILLISECONDS);
                    } catch (java.lang.InterruptedException interruptException) {
                        return null;
                    }
                }
            }
            // Catches exceptions if something tries to activate the
            // Thread incorrectly.
        } finally {
            // If the decode failed, there's no bitmap.
            if (null == returnBitmap) {
                // Logs the error
                Log.e(LOG_TAG, "Download failed in PhotoDecodeRunnable");

            }
            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
        return returnBitmap;
    }
}
