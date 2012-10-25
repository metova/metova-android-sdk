package com.metova.android.util.http.async;

import com.metova.android.util.http.response.Response;

/**
 * Strategy pattern to allow defining custom retry behavior when requests fail to be 
 * dispatched from a {@link QueuedHttpClient}.
 * <p/>
 * A custom strategy may be defined that, for example, tracks a counter going to 10 to attempt 10 retries,
 * or print a specific log message indicating something has failed. Alternatively, a strategy may be
 * defined that retries 503 status but not 404s.
 * <p/>
 * An object of this type will be instantiated when a request is first sent, and the same object
 * will be used to make the choice to retry until it returns false.
 * <p/>
 * Since implementors of this type will be instantiated via reflection, they must have an explicit
 * null constructor.
 */
public interface RetryStrategy {

    /**
     * 
     * Called when an outbound request fails.
     * 
     * @param response the Response from the server, if any. Will be null in cases of an Exception being thrown that prevented
     * the outgoing request.
     * @param throwable the exception that occurred that prevented the attempt from being sent, if any. Will be null if successful
     * server communication occurred.
     * 
     * @return whether to attempt the request again
     */
    public boolean onRetry( Response response, Throwable throwable );
}
