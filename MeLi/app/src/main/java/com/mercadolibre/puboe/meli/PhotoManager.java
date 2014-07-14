package com.mercadolibre.puboe.meli;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
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
    private final LruCache<URL, Bitmap> mPhotoCache;
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
    private final Queue<PhotoTask> mPhotoTaskWorkQueue;
    private final BlockingQueue<Runnable> mTaskWorkQueue;
    private final ThreadPoolExecutor mThreadPool;

    public static PhotoManager instance;
    private Handler mHandler;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
//    private Map<PhotoView, URL> map = new HashMap<PhotoView, URL>();


    public static PhotoManager getInstance() {
        if(instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    private PhotoManager() {


        mPhotoTaskWorkQueue = new LinkedBlockingQueue<PhotoTask>();
        mTaskWorkQueue = new LinkedBlockingQueue<Runnable>();

        mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mTaskWorkQueue);

        mPhotoCache = new LruCache<URL, Bitmap>(IMAGE_CACHE_SIZE) {
            @Override
            protected int sizeOf(URL key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

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
                PhotoView localView = photoTask.getImageView();

                // If this input view isn't null
                if (localView != null) {

                    if(photoTask.getUrl().equals(localView.getLocation())) {
                        switch (inputMessage.what) {

                            case TASK_COMPLETE:
                                Log.w("handleMessage", "TaskComplete");

                                localView.setImageBitmap(photoTask.getImage());
                                mPhotoCache.put(photoTask.getUrl(), photoTask.getImage());
                                recycleTask(photoTask);
                                break;

                            case DOWNLOAD_FAILED:
                                Log.w("handleMessage", "DownloadFailed");
                                localView.setImageResource(R.drawable.imagedownloadfailed);
                                recycleTask(photoTask);
                                break;

                            default:
                                recycleTask(photoTask);
                                Log.w("handleMessage", "default");
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
                    }
                } else {
                    recycleTask(photoTask);
                    Log.w("handleMessage", "localView NULL");
                }

            }
        };
    }

    public void onCompleteTask(PhotoTask photoTask, int state) {
        switch (state) {

            // The task finished downloading and decoding the image
            case TASK_COMPLETE:
                Log.i("onCompleteTask", "taskComplete");
                Message completeMessage = mHandler.obtainMessage(state, photoTask);
                completeMessage.sendToTarget();
                break;

            // In all other cases, pass along the message without any other action.
            default:
                Log.i("onCompleteTask", "default");
                mHandler.obtainMessage(state, photoTask).sendToTarget();
                break;
        }
    }

    public void startDownload(URL mUrl, PhotoView imageView) {
        Bitmap bm = mPhotoCache.get(mUrl);

        if(bm == null) {
        /*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
            PhotoTask photoTask = (PhotoTask)getInstance().mPhotoTaskWorkQueue.poll();

            // If the queue was empty, create a new task instead.
            if (null == photoTask) {
                photoTask = new PhotoTask();
            }

            // Initializes the task
            photoTask.initializeTask(mUrl, imageView, PhotoManager.getInstance());

            // Sets the display to show that the image is queued for downloading and decoding.
            imageView.setImageResource(R.drawable.imagequeued);
            Log.i("PhotoManager", "executing photoTask");
            getInstance().mThreadPool.execute(photoTask.getRunnable());
        } else {
            imageView.setImageBitmap(bm);
        }
    }

    void recycleTask(PhotoTask photoTask) {

        // Frees up memory in the task
        photoTask.recycle();

        // Puts the task object back into the queue for re-use.
        mPhotoTaskWorkQueue.offer(photoTask);
    }

    public void addBitmapToMemoryCache(URL key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mPhotoCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(URL key) {
        return mPhotoCache.get(key);
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



}
