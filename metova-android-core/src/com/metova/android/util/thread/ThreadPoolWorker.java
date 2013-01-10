package com.metova.android.util.thread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.metova.android.util.concurrent.ThreadPool;

/**
 * <br>
 * An asyncronous task managed by a <code>ThreadPool</code>. This class has been designed to mimic
 * <code>AsyncTask</code> as closely as possible. For guides and useage, see {@link AsyncTask} (though
 * note that this class does <b>not</b> inherit <code>AsyncTask</code>).
 * @author Jason
 *
 * @see {@link AsyncTask}
 * @see {@link ThreadPool}
 */
public abstract class ThreadPoolWorker<Params, Progress, Result> {

    private static final int MESSAGE_POST_RESULT = 1;
    private static final int MESSAGE_POST_PROGRESS = 2;

    private static final InternalHandler handler;
    private static final ThreadPool defaultThreadPool;

    private Runnable runnable;
    private Status status = Status.NOT_STARTED;
    private boolean canceled;
    private ThreadPool threadPool;

    static {
        handler = new InternalHandler();
        defaultThreadPool = new ThreadPool( 20 );
    }

    /**
     * Creates a <code>ThreadPoolWorker</code> that will execute in the default worker thread pool.
     */
    public ThreadPoolWorker() {

        setThreadPool( defaultThreadPool );
    }

    /**
     * Creates a <code>ThreadPoolWorker</code> that will execute in the specified thread pool.
     * @param threadPool the thread pool to execute in
     */
    public ThreadPoolWorker(ThreadPool threadPool) {

        setThreadPool( threadPool );
    }

    private void run( Params... params ) {

        setStatus( Status.RUNNING );
        postResult( doInBackground( params ) );
    }

    private void finish( Result result ) {

        if ( isCanceled() ) {
            onCanceled( result );
        }
        else {
            onPostExecute( result );
        }
        setStatus( Status.FINISHED );
    }

    /**
     * Cancels the task. If the task is not yet running due to never being started or the background process not executing yet,
     * {@link #onCanceled(Object)} will be called immediately. If the task is running, the caller should gracefully finish
     * execution in {@link #doInBackground(Object...)} as soon as possible, in which case {@link #onCanceled(Object)} will
     * then be called.
     */
    public void cancel() {

        setCanceled( true );

        switch (getStatus()) {
            case NOT_STARTED:
                getThreadPool().getQueue().remove( getRunnable() );
                postResult( null );
                break;
            case PENDING:
                getThreadPool().remove( getRunnable() );
                postResult( null );
                break;
            default:
                break;
        }
    }

    /**
     * Initializes this task for thread pool execution. This task will be executed at some time in the future.
     * {@link #onPreExecute()} will be called immediately on the UI thread.
     * @param params parameters for background operation
     * @throws IllegalStateException if called more than once
     */
    public void execute( Params... params ) {

        if ( getStatus() != Status.NOT_STARTED ) {
            switch (getStatus()) {
                case RUNNING:
                    throw new IllegalStateException( "Cannot execute task: the task is already running." );
                case PENDING:
                    throw new IllegalStateException( "Cannot execute task: the task is already pending execution." );
                case FINISHED:
                    throw new IllegalStateException( "Cannot execute task: the task has already finished. A task can only be executed once." );
                default:
                    break;
            }
        }

        setStatus( Status.PENDING );
        handler.post( new Runnable() {

            @Override
            public void run() {

                onPreExecute();
            }
        } );

        Runnable runnable = new ManagedAsyncTaskRunnable( params );
        setRunnable( runnable );
        getThreadPool().execute( runnable );
    }

    /**
     * Publishes progress to the UI thread. This can safely be called at any point in the lifecycle of this task.
     * {@link #onProgressUpdate(Progress...)} will not be called if the task is canceled.
     * @param progress the progress to publish
     * @see #onProgressUpdate(Progress...)
     */
    protected final void publishProgress( Progress... progress ) {

        if ( !isCanceled() ) {
            handler.obtainMessage( MESSAGE_POST_PROGRESS, new ManagedAsyncTaskResult<Progress>( this, progress ) ).sendToTarget();
        }
    }

    @SuppressWarnings( "unchecked" )
    private void postResult( Result result ) {

        handler.obtainMessage( MESSAGE_POST_RESULT, new ManagedAsyncTaskResult<Result>( this, result ) ).sendToTarget();
    }

    /**
     * Performs computation on a background thread. The specified parameters are the parameters passed to
     * {@link #execute(Params...)} by the caller of this task. Progress can be published to the UI thread
     *  using {@link #publishProgress(Progress...)}.
     * @param params the parameters of the task
     * @return the result of the computation
     * @see #onPreExecute()
     * @see #onPostExecute(Result)
     * @see #publishProgress(Progress...)
     */
    protected abstract Result doInBackground( Params... params );

    /**
     * Override this method if you need to update the UI prior to {@link #doInBackground(Params...)} executing. Runs on the
     * UI thread.
     * @see #doInBackground(Params...)
     * @see #onPostExecute(Result)
     */
    protected void onPreExecute() {

    }

    /**
     * Override this method if you need to update the UI after {@link #doInBackground(Params...)} has finished execution.
     * The specified result is the value returned by {@link #doInBackground(Params...)}. This method won't be invoked if
     * the task was canceled ({@link #onCanceled(Result)} will instead). Runs on the UI thread.
     * @param result the result of the operation computed by {@link #doInBackground(Params...)}
     * @see #doInBackground(Params...)
     * @see #onPreExecute()
     */
    protected void onPostExecute( Result result ) {

    }

    /**
     * Override this method if you need to update the UI during exeuction of {@link #doInBackground(Params...)}. The specified
     * parameters are the values passed with {@link #publishProgress(Progress...)}. Runs on the UI thread.
     * @param progress the values indicating progress
     * @see #doInBackground(Progress...)
     */
    protected void onProgressUpdate( Progress... progress ) {

    }

    /**
     * Override this method if you need to perform some action after {@link #doInBackground(Params...)} has finished
     * execution and the task has been canceled. Runs on the UI thread.
     * @param result the result of the operation computed by {@link #doInBackground(Params...)}
     * @see #cancel()
     * @see #isCanceled()
     * @see #doInBackground(Params...)
     */
    protected void onCanceled( Result result ) {

    }

    /**
     * Status codes indicating the stage in the lifecycle the task is currently at.
     * @author Jason
     *
     */
    public enum Status {

        /**
         * Indicates this task has finished execution.
         */
        FINISHED,
        /**
         * Indicates this task has been added to the thread pool queue but has not begun execution.
         */
        PENDING,
        /**
         * Indicates this task is currently executing.
         */
        RUNNING,
        /**
         * Indicates that {@link ManagedAsyncTask#execute(Params...)} has not been called.
         */
        NOT_STARTED
    }

    private class ManagedAsyncTaskRunnable implements Runnable {

        private Params[] params;

        public ManagedAsyncTaskRunnable(Params... params) {

            this.params = params;
        }

        @Override
        public void run() {

            ThreadPoolWorker.this.run( params );
        }
    }

    private static class InternalHandler extends Handler {

        public InternalHandler() {

            super( Looper.getMainLooper() );
        }

        @SuppressWarnings( { "unchecked", "rawtypes" } )
        @Override
        public void handleMessage( Message msg ) {

            ManagedAsyncTaskResult result = (ManagedAsyncTaskResult) msg.obj;

            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    result.task.finish( result.data[0] );
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.task.onProgressUpdate( result.data );
            }
        }
    }

    @SuppressWarnings( "rawtypes" )
    private static class ManagedAsyncTaskResult<Data> {

        final ThreadPoolWorker task;
        final Data[] data;

        ManagedAsyncTaskResult(ThreadPoolWorker task, Data... data) {

            this.task = task;
            this.data = data;
        }
    }

    /**
     * Indicates whether {@link #cancel()} has been called.
     * @return <code>true</code> if this task has been canceled
     */
    public boolean isCanceled() {

        return canceled;
    }

    private void setCanceled( boolean canceled ) {

        this.canceled = canceled;
    }

    /**
     * Get the current lifecycle stage this task is in.
     * @return the lifecycle stage
     * @see Status
     */
    public Status getStatus() {

        return status;
    }

    private void setStatus( Status status ) {

        this.status = status;
    }

    private Runnable getRunnable() {

        return runnable;
    }

    private void setRunnable( Runnable runnable ) {

        this.runnable = runnable;
    }

    public void setThreadPool( ThreadPool threadPool ) {

        this.threadPool = threadPool;
    }

    private ThreadPool getThreadPool() {

        return threadPool;
    }
}
