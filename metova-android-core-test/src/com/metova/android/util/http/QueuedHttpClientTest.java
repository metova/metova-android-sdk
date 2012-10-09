package com.metova.android.util.http;

import static org.mockito.Mockito.mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

public class QueuedHttpClientTest extends TestCase {

    private QueuedHttpClient queuedHttpClient;
    private HttpClient mockHttpClient;
    private ExecutorService executorService;
    private BlockingQueue<HttpUriRequest> blockingQueue;

    @Override
    public void setUp() {

        mockHttpClient = mock( HttpClient.class );
        blockingQueue = new LinkedBlockingQueue<HttpUriRequest>();
        executorService = Executors.newSingleThreadExecutor();

        queuedHttpClient = new QueuedHttpClient( mockHttpClient, executorService, blockingQueue );

    }

    //TODO: ASDK-116
    public void testSomething() {

        assertNotNull( queuedHttpClient );
    }

}
