package com.metova.android.util.http.async;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.InOrder;

public class QueuedHttpClientTest extends TestCase {

    private QueuedHttpClient queuedHttpClient;
    private HttpClient mockHttpClient;
    private ExecutorService executorService;
    private BlockingQueue<AsyncHttpUriRequest> blockingQueue;

    @Override
    public void setUp() {

        mockHttpClient = mock( HttpClient.class );
        blockingQueue = new LinkedBlockingQueue<AsyncHttpUriRequest>();
        //single thread needed to ensure serial dispatch order
        executorService = Executors.newSingleThreadExecutor();
    }

    public void testNullHttpClientNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( null, executorService, blockingQueue );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testNullExecutorServiceNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( mockHttpClient, null, blockingQueue );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testNullQueueNotAllowed() {

        try {

            queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, null );
            fail( "Expected a NullPointerException." );
        }
        catch (AssertionError ae) {
            //success
        }
    }

    public void testStoppedByDefault() throws InterruptedException {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue );

        queuedHttpClient.submit( new HttpPost() );

        assertEquals( 1, blockingQueue.size() );

        //if it were going to dispatch, allow some time for it to do that
        Thread.sleep( 1500 );

        verifyNoMoreInteractions( mockHttpClient );
    }

    public void testRequestsDispatchedInOrder() throws Exception {

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue );

        HttpResponse mockHttpResponse = mock( HttpResponse.class );
        when( mockHttpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( mockHttpResponse );

        HttpUriRequest request1 = new HttpPost();
        HttpUriRequest request2 = new HttpGet();
        HttpUriRequest request3 = new HttpDelete();
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
}
