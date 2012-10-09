package com.metova.android.util.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import android.os.Handler;
import android.os.Looper;

/**
 * As a general rule, this thread pool should be used for all situations 
 * a separate thread is needed to do work.
 */
public class GlobalThreadPool {

    private static final ThreadPool threadPool;
    private static final Handler handler;

    private GlobalThreadPool() {

    }

    static {
        handler = new Handler( Looper.getMainLooper() );
        threadPool = new ThreadPool();
    }

    /**
     * Executes the given <code>Runnable</code> at some time in the future on the UI thread.
     * @param task the action to be executed
     */
    public static boolean invokeOnUiThread( Runnable task ) {

        return handler.post( task );
    }

    /**
     * Executes the given <code>Runnable</code> after the specified delay on the UI thread.
     * @param task the action to be executed
     */
    public static boolean invokeOnUiThread( Runnable task, long delayMillis ) {

        return handler.postDelayed( task, delayMillis );
    }

    /**
     * Submits a given <code>Runnable</code> for execution and returns a <code>Future</code> object representing
     * that task.
     * @param task the action to be executed
     * @param result the result to return once the task has finished execution
     * @see {@link ThreadPoolExecutor#submit(Runnable, Object)}
     * @see {@link Future}
     */
    public static <T> Future<T> submit( Runnable task, T result ) {

        return threadPool.submit( task, result );
    }

    /**
     * Removes the task from the worker queue if it's present. If successful, this task will not be executed.
     * This will fail if the task doesn't exist in the worker queue or if the task is already executing.
     * @param task the task to remove
     * @return true if the task was removed
     * @see #invoke(Runnable)
     * @see ThreadPoolExecutor#remove(Runnable)
     */
    public static boolean remove( Runnable task ) {

        return threadPool.remove( task );
    }

    /**
     * Executes the given <code>Runnable</code> at some time in the future on another thread.
     * @param task the action to be executed
     * @see #remove(Runnable)
     * @see {@link ThreadPoolExecutor#execute(Runnable)}
     */
    public static void invoke( Runnable task ) {

        threadPool.execute( task );
    }

    /**
     * Sets the amount of threads managed by the thread pool. This can be set at any time, as the thread pool
     * will shrink or grow to accomodate the new size. It is recommended that if you need to grow or shrink
     * the thread pool for a subset of operations to set the pool size back to its default value once that subset has
     * been executed.
     * <br><br>
     * Default is 10.
     * @param poolSize the new size
     * @see ThreadPoolExecutor#setCorePoolSize(int)
     */
    public static void setPoolSize( int poolSize ) {

        threadPool.setCorePoolSize( poolSize );
        threadPool.setMaximumPoolSize( poolSize );
    }
}
