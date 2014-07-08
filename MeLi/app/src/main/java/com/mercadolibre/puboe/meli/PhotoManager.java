package com.mercadolibre.puboe.meli;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by puboe on 07/07/14.
 */
public class PhotoManager {

    // Status indicators
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;

    /*
     * Creates a cache of byte arrays indexed by image URLs. As new items are added to the
     * cache, the oldest items are ejected and subject to garbage collection.
     */
//    private final LruCache<URL, byte[]> mPhotoCache;
    // Sets the size of the storage that's used to cache images
    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;;
    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;
    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;
    // A queue of PhotoManager tasks. Tasks are handed to a ThreadPool.
    private final BlockingQueue<Runnable> mPhotoTaskWorkQueue;
    private final ThreadPoolExecutor mThreadPool;

    public static PhotoManager instance;
    private Handler mHandler;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private Map<ImageView, URL> map = new HashMap<ImageView, URL>();


    public static PhotoManager getInstance() {
        if(instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    private PhotoManager() {

        mPhotoTaskWorkQueue = new LinkedBlockingQueue<Runnable>();

        mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mPhotoTaskWorkQueue);

//        mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
//                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);


        mHandler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {

                // Gets the image task from the incoming Message object.
                PhotoTask photoTask = (PhotoTask) inputMessage.obj;

                // Sets an PhotoView that's a weak reference to the
                // input ImageView
                ImageView localView = photoTask.getImageView();

                // If this input view isn't null
                if (localView != null) {

                    /*
                     * Gets the URL of the *weak reference* to the input
                     * ImageView. The weak reference won't have changed, even if
                     * the input ImageView has.
                     */
//                    URL localURL = localView.getLocation();

                    /*
                     * Compares the URL of the input ImageView to the URL of the
                     * weak reference. Only updates the bitmap in the ImageView
                     * if this particular Thread is supposed to be serving the
                     * ImageView.
                     */

//                    if(map.get(localView) == null || map.get(localView).equals(photoTask.getUrl())) {
//                    if (photoTask.getImageURL() == localURL)
//                        map.remove(localView);
                        /*
                         * Chooses the action to take, based on the incoming message
                         */
                        switch (inputMessage.what) {

//                            // If the download has started, sets background color to dark green
//                            case DOWNLOAD_STARTED:
//                                localView.setStatusResource(R.drawable.imagedownloading);
//                                break;
//
//                            /*
//                             * If the download is complete, but the decode is waiting, sets the
//                             * background color to golden yellow
//                             */
//                            case DOWNLOAD_COMPLETE:
//                                // Sets background color to golden yellow
//                                localView.setStatusResource(R.drawable.decodequeued);
//                                break;
//                            // If the decode has started, sets background color to orange
//                            case DECODE_STARTED:
//                                localView.setStatusResource(R.drawable.decodedecoding);
//                                break;
                            /*
                             * The decoding is done, so this sets the
                             * ImageView's bitmap to the bitmap in the
                             * incoming message
                             */
                            case TASK_COMPLETE:
                                localView.setImageBitmap(photoTask.getImage());
//                                recycleTask(photoTask);
                                break;
                            // The download failed, sets the background color to dark red
                            case DOWNLOAD_FAILED:
                                localView.setImageResource(R.drawable.imagedownloadfailed);

                                // Attempts to re-use the Task object
//                                recycleTask(photoTask);
                                break;
                            default:
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
//                    }
                }
            }
        };
    }


    public void onCompleteTask(PhotoTask photoTask, int state) {
        switch (state) {

            // The task finished downloading and decoding the image
            case TASK_COMPLETE:
                Message completeMessage = mHandler.obtainMessage(state, photoTask);
                completeMessage.sendToTarget();
                break;

            // In all other cases, pass along the message without any other action.
            default:
                mHandler.obtainMessage(state, photoTask).sendToTarget();
                break;
        }
    }

//    /**
//     * Handles state messages for a particular task object
//     * @param photoTask A task object
//     * @param state The state of the task
//     */
//    @SuppressLint("HandlerLeak")
//    public void handleState(PhotoTask photoTask, int state) {
//        switch (state) {
//
//            // The task finished downloading and decoding the image
//            case TASK_COMPLETE:
//
//                // Puts the image into cache
//                if (photoTask.isCacheEnabled()) {
//                    // If the task is set to cache the results, put the buffer
//                    // that was
//                    // successfully decoded into the cache
//                    mPhotoCache.put(photoTask.getImageURL(), photoTask.getByteBuffer());
//                }
//
//                // Gets a Message object, stores the state in it, and sends it to the Handler
//                Message completeMessage = mHandler.obtainMessage(state, photoTask);
//                completeMessage.sendToTarget();
//                break;
//
//            // The task finished downloading the image
//            case DOWNLOAD_COMPLETE:
//                /*
//                 * Decodes the image, by queuing the decoder object to run in the decoder
//                 * thread pool
//                 */
//                mDecodeThreadPool.execute(photoTask.getPhotoDecodeRunnable());
//
//                // In all other cases, pass along the message without any other action.
//            default:
//                mHandler.obtainMessage(state, photoTask).sendToTarget();
//                break;
//        }
//
//    }
//
//    /**
//     * Cancels all Threads in the ThreadPool
//     */
//    public static void cancelAll() {
//
//        /*
//         * Creates an array of tasks that's the same size as the task work queue
//         */
//        PhotoTask[] taskArray = new PhotoTask[instance.mDownloadWorkQueue.size()];
//
//        // Populates the array with the task objects in the queue
//        instance.mDownloadWorkQueue.toArray(taskArray);
//
//        // Stores the array length in order to iterate over the array
//        int taskArraylen = taskArray.length;
//
//        /*
//         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
//         * iterates over the array of tasks and interrupts the task's current Thread.
//         */
//        synchronized (instance) {
//
//            // Iterates over the array of tasks
//            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {
//
//                // Gets the task's current thread
//                Thread thread = taskArray[taskArrayIndex].mThreadThis;
//
//                // if the Thread exists, post an interrupt to it
//                if (null != thread) {
//                    thread.interrupt();
//                }
//            }
//        }
//    }
//
//    /**
//     * Stops a download Thread and removes it from the threadpool
//     *
//     * @param downloaderTask The download task associated with the Thread
//     * @param pictureURL The URL being downloaded
//     */
//    static public void removeDownload(PhotoTask downloaderTask, URL pictureURL) {
//
//        // If the Thread object still exists and the download matches the specified URL
//        if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureURL)) {
//
//            /*
//             * Locks on this class to ensure that other processes aren't mutating Threads.
//             */
//            synchronized (instance) {
//
//                // Gets the Thread that the downloader task is running on
//                Thread thread = downloaderTask.getCurrentThread();
//
//                // If the Thread exists, posts an interrupt to it
//                if (null != thread)
//                    thread.interrupt();
//            }
//            /*
//             * Removes the download Runnable from the ThreadPool. This opens a Thread in the
//             * ThreadPool's work queue, allowing a task in the queue to start.
//             */
//            instance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
//        }
//    }


    public PhotoTask startDownload(String url, ImageView imageView) {

        /*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
        PhotoTask photoTask = (PhotoTask)getInstance().mPhotoTaskWorkQueue.poll();


        // If the queue was empty, create a new task instead.
        if (null == photoTask) {
            photoTask = new PhotoTask();
        }

        URL mUrl = null;
        try {
            mUrl = new URL(url);
            map.put(imageView, mUrl);
            // Initializes the task
            photoTask.initializeTask(mUrl, imageView, PhotoManager.getInstance());

            getInstance().mThreadPool.execute(photoTask);

            // Sets the display to show that the image is queued for downloading and decoding.
            imageView.setImageResource(R.drawable.imagequeued);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.imagedownloadfailed);
        }

        // Returns a task object, either newly-created or one from the task pool
        return photoTask;
    }

    void recycleTask(PhotoTask photoTask) {

        // Frees up memory in the task
        photoTask.recycle();

        // Puts the task object back into the queue for re-use.
        mPhotoTaskWorkQueue.offer(photoTask);
    }

}
