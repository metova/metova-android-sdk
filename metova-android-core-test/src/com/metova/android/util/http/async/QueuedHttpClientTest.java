package com.metova.android.util.http.async;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.InOrder;

import com.metova.android.util.http.response.Response;

public class QueuedHttpClientTest extends TestCase {

    private QueuedHttpClient queuedHttpClient;
    private HttpClient mockHttpClient;
    private ExecutorService executorService;
    private BlockingQueue<AsyncHttpRequestBase> blockingQueue;

    @Override
    public void setUp() {

        mockHttpClient = mock( HttpClient.class );
        blockingQueue = new LinkedBlockingQueue<AsyncHttpRequestBase>();
        //single thread needed to ensure serial dispatch order
        executorService = Executors.newSingleThreadExecutor();
    }

    public void testNullHttpClientNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( null, executorService, blockingQueue, null );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testNullExecutorServiceNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( mockHttpClient, null, blockingQueue, null );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testNullQueueNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, null, null );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testStoppedByDefault() throws InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, null );

        queuedHttpClient.submit( new HttpPost() );

        assertEquals( 1, blockingQueue.size() );

        //if it were going to dispatch, allow some time for it to do that
        Thread.sleep( 1500 );

        verifyNoMoreInteractions( mockHttpClient );
    }

    public void testRequestsDispatchedInOrder() throws Exception {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, null );

        HttpResponse mockHttpResponse = mock( HttpResponse.class );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( mockHttpResponse );

        HttpRequestBase request1 = new HttpPost();
        HttpRequestBase request2 = new HttpGet();
        HttpRequestBase request3 = new HttpDelete();
        queuedHttpClient.submit( request1 );
        queuedHttpClient.submit( request2 );
        queuedHttpClient.submit( request3 );

        assertEquals( 3, blockingQueue.size() );

        queuedHttpClient.start();

        //wait for dispatch
        int retries = 3;
        while (blockingQueue.size() > 0 && retries > 0) {
            Thread.sleep( 100 );
            retries--;
        }

        InOrder inOrder = inOrder( mockHttpClient );

        inOrder.verify( mockHttpClient ).execute( eq( request1 ) );
        inOrder.verify( mockHttpClient ).execute( eq( request2 ) );
        inOrder.verify( mockHttpClient ).execute( eq( request3 ) );
    }

    public void testCallback() throws ClientProtocolException, IOException, InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, RetryOnceStrategy.class );

        HttpResponse mockHttpResponse = mock( HttpResponse.class, RETURNS_DEEP_STUBS );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( mockHttpResponse );

        when( mockHttpResponse.getStatusLine().getStatusCode() ).thenReturn( 200 );
        HttpRequestBase request1 = new HttpPost();
        queuedHttpClient.submit( request1, WakingCallback.class );

        WakingCallback.lock = this;
        synchronized (this) {
            queuedHttpClient.start();
            wait( 5000 );
        }
        assertTrue( WakingCallback.calledBack );
    }

    public void testRetryOnFailure() throws ClientProtocolException, IOException, InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, RetryOnceStrategy.class );

        HttpResponse mockHttpResponse1 = mock( HttpResponse.class, RETURNS_DEEP_STUBS );
        HttpResponse mockHttpResponse2 = mock( HttpResponse.class, RETURNS_DEEP_STUBS );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( mockHttpResponse1, mockHttpResponse2 );

        when( mockHttpResponse1.getStatusLine().getStatusCode() ).thenReturn( 401 );
        when( mockHttpResponse2.getStatusLine().getStatusCode() ).thenReturn( 200 );

        HttpRequestBase request1 = new HttpPost();
        queuedHttpClient.submit( request1, WakingCallback.class );

        WakingCallback.lock = this;
        synchronized (this) {

            queuedHttpClient.start();
            wait( 5000 );
        }
        verify( mockHttpClient, times( 2 ) ).execute( request1 );
    }

    public void testRetryOnException() throws ClientProtocolException, IOException, InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, RetryOnceStrategy.class );

        HttpResponse mockHttpResponse1 = mock( HttpResponse.class, RETURNS_DEEP_STUBS );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenThrow( new RuntimeException() );

        when( mockHttpResponse1.getStatusLine().getStatusCode() ).thenReturn( 401 );

        HttpRequestBase request1 = new HttpPost();
        queuedHttpClient.submit( request1, WakingCallback.class );

        WakingCallback.lock = this;
        synchronized (this) {

            queuedHttpClient.start();
            wait( 5000 );
        }
        verify( mockHttpClient, times( 2 ) ).execute( request1 );
    }

    public void testNoRetryOnSuccess() throws ClientProtocolException, IOException, InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue, NeverCalledRetryStrategy.class );

        HttpResponse mockHttpResponse = mock( HttpResponse.class, RETURNS_DEEP_STUBS );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( mockHttpResponse );

        when( mockHttpResponse.getStatusLine().getStatusCode() ).thenReturn( 200 );

        HttpRequestBase request1 = new HttpPost();
        queuedHttpClient.submit( request1, WakingCallback.class );

        WakingCallback.lock = this;
        synchronized (this) {

            queuedHttpClient.start();
            wait( 5000 );
        }
        verify( mockHttpClient, times( 1 ) ).execute( request1 );
        assertTrue( NeverCalledRetryStrategy.neverCalled );
    }

    static class NeverCalledRetryStrategy implements RetryStrategy {

        static boolean neverCalled = true;

        @Override
        public boolean onRetry( Response response, Throwable throwable ) {

            neverCalled = false;
            return false;
        }

    }

    private static class RetryOnceStrategy implements RetryStrategy {

        //invoked via reflection, so appears unused
        @SuppressWarnings( "unused" )
        public RetryOnceStrategy() {

        }

        int ctr = 0;

        @Override
        public boolean onRetry( Response response, Throwable throwable ) {

            return ( ctr++ < 1 );
        }
    }

    static class WakingCallback implements AsyncHttpResponseCallback {

        static boolean calledBack = false;
        static Object lock;

        public WakingCallback() {

        }

        @Override
        public void onResponseReceived( Response response ) {

            synchronized (lock) {
                calledBack = true;
                lock.notifyAll();
            }
        }
    }
}
