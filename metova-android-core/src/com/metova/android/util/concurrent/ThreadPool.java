package com.metova.android.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A blocking unbounded thread pool. Tasks are run on a thread from the thread 
 * pool with a default maximum size of 10 concurrently running threads. New 
 * tasks that are added when all threads are busy will be added to a queue.
 * 
 * @see ThreadPoolExecutor
 */
public class ThreadPool extends ThreadPoolExecutor {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int KEEP_ALIVE_TIME = 1000;

    /**
     * Creates a thread pool with a default size of 10 concurrently running threads.
     */
    public ThreadPool() {

        super( CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, new LinkedBlockingQueue<Runnable>() );
    }

    /**
     * Creates a thread pool with the given size.
     * @param poolSize the size of the pool
     */
    public ThreadPool(int poolSize) {

        this( poolSize, false );
    }

    /**
     * Creates a thread pool with the given size.
     * @param poolSize the size of the pool
     * @param whether or not to limit the queue to 20 tasks
     */
    @SuppressWarnings( "serial" )
    public ThreadPool(int poolSize, final boolean limitQueue) {

        super( poolSize, poolSize, KEEP_ALIVE_TIME, TIME_UNIT, new LinkedBlockingQueue<Runnable>() {

            @Override
            public boolean offer( Runnable e ) {

                if ( limitQueue ) {

                    boolean value = super.offer( e );
                    if ( size() > 20 ) {
                        poll();
                    }

                    return value;
                }
                else {
                    return super.offer( e );
                }
            }
        } );
    }
}
