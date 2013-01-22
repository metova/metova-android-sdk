package com.metova.android.test.util.thread;

import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.metova.android.util.concurrent.ThreadPool;
import com.metova.android.util.thread.ThreadPoolWorker;

public class ThreadPoolWorkerTest extends AndroidTestCase {

    private boolean workerRan = false;

    @Override
    public void setUp() {

        workerRan = false;
    }

    public void testThreadPoolWorkerFinishes() throws Throwable {

        ThreadPool threadPool = new ThreadPool();
        TestWorker testWorker = new TestWorker( threadPool );
        testWorker.execute();
        threadPool.shutdown();
        threadPool.awaitTermination( 1, TimeUnit.SECONDS );
        assertTrue( workerRan );
    }

    private class TestWorker extends ThreadPoolWorker<Void, Integer, Integer> {

        public TestWorker(ThreadPool threadPool) {

            super( threadPool );
        }

        @Override
        protected Integer doInBackground( Void... params ) {

            workerRan = true;
            return null;
        }
    }
}
