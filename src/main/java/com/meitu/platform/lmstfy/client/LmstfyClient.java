package com.meitu.platform.lmstfy.client;

import com.meitu.platform.lmstfy.exception.LmstfyException;
import com.meitu.platform.lmstfy.exception.LmstfyIllegalRequestException;
import com.meitu.platform.lmstfy.exception.LmstfyUnexpectedException;
import com.meitu.platform.lmstfy.response.ErrorResponse;
import com.meitu.platform.lmstfy.Job;
import com.meitu.platform.lmstfy.response.LmstfyResponse;
import com.meitu.platform.lmstfy.response.PublishResponse;
import com.meitu.platform.lmstfy.response.QueueSizeResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static java.net.HttpURLConnection.*;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class LmstfyClient {

    private static final String HEADER_TOKEN = "X-Token";
    private static final String PATH_API = "api";
    private static final String QUERY_DELAY = "delay";
    private static final String QUERY_TTL = "ttl";
    private static final String QUERY_TRIES = "tries";
    private static final String QUERY_TIMEOUT = "timeout";
    private static final String QUERY_TTR = "ttr";

    private String namespace;
    private String token;

    private OkHttpClient http;
    private HttpUrl serviceAddress;


    public LmstfyClient(String host, int port, String namespace, String token) {
        this.namespace = namespace;
        this.token = token;
        this.serviceAddress = new HttpUrl.Builder()
                .host(host)
                .port(port)
                .scheme("http")
                .build();

        this.http = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(new OkHttpRetryInterceptor(3, 100))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
                .build();
    }

    private LmstfyResponse doRequest(String method, HttpUrl url, RequestBody body) throws LmstfyException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader(HEADER_TOKEN, this.token)
                .method(method, body)
                .build();
        try (Response response = this.http.newCall(request).execute()) {
            return new LmstfyResponse(response);
        } catch (IOException e) {
            throw new LmstfyException("request lmstfy failed", e);
        }
    }

    /**
     * Publish a new job to the queue.
     *
     * @param queue
     * @param data
     * @param ttlSecond   The time-to-live of the job. If it's zero, job won't expire.
     * @param tries       The maximum times the job can be fetched.
     * @param delaySecond The duration before the job is released for consuming. When it's zero, no delay is applied.
     * @return Job ID
     * @throws LmstfyException
     */
    public String publish(String queue, byte[] data, int ttlSecond, short tries, int delaySecond) throws LmstfyException {
        HttpUrl url = genServiceUrlBuilder(PATH_API, this.namespace, queue)
                .addQueryParameter(QUERY_DELAY, String.valueOf(delaySecond))
                .addQueryParameter(QUERY_TTL, String.valueOf(ttlSecond))
                .addQueryParameter(QUERY_TRIES, String.valueOf(tries))
                .build();

        LmstfyResponse response = this.doRequest("PUT", url, RequestBody.create(MediaType.parse("text/binary"), data));

        switch (response.getCode()) {
            case HTTP_CREATED:
                PublishResponse publishResponse = response.unmarshalBody(PublishResponse.class);
                return publishResponse.getJobID();
            case HTTP_BAD_REQUEST:
            case HTTP_ENTITY_TOO_LARGE:
                ErrorResponse errorResponse = response.unmarshalBody(ErrorResponse.class);
                throw new LmstfyIllegalRequestException(response.getCode(), errorResponse.getError());
            default:
                throw new LmstfyUnexpectedException(response.getCode());
        }

    }

    /**
     * Consume a job. Consuming will decrease the job's tries by 1 first.
     *
     * @param ttrSecond     The time-to-run of the job. If the job is not finished before the TTR expires,
     *                      the job will be released for consuming again if the `(tries - 1) > 0`.
     * @param timeoutSecond The long-polling wait time. If it's zero, this method will return immediately with or without
     *                      a job; if it's positive, this method would polling for new job until timeout.
     * @param queues        You can consume multiple queues of the same namespace at once. The order of the queues in
     *                      the params implies the priority.
     * @return Return the job, if there is no job available, it will return null.
     * @throws LmstfyException
     */
    public Job consume(int ttrSecond, int timeoutSecond, String... queues) throws LmstfyException {
        HttpUrl url = this.genServiceUrlBuilder(PATH_API, this.namespace, String.join(",", queues))
                .addQueryParameter(QUERY_TIMEOUT, String.valueOf(timeoutSecond))
                .addQueryParameter(QUERY_TTR, String.valueOf(ttrSecond))
                .build();

        LmstfyResponse response = this.doRequest("GET", url, null);
        switch (response.getCode()) {
            case HTTP_OK:
                Job job = response.unmarshalBody(Job.class);
                job.setData(new String(Base64.getDecoder().decode(job.getBase64Data())));
                return job;
            case HTTP_BAD_REQUEST:
                ErrorResponse errorResponse = response.unmarshalBody(ErrorResponse.class);
                throw new LmstfyIllegalRequestException(response.getCode(), errorResponse.getError());
            case HTTP_NOT_FOUND:
                return null;
            default:
                throw new LmstfyUnexpectedException(response.getCode());
        }
    }

    /**
     * Mark a job as finished, so it won't be retried by others.
     * It's same as ack(queue,jobID)
     *
     * @param queue
     * @param jobID
     * @throws LmstfyException
     */
    public void ack(String queue, String jobID) throws LmstfyException {
        this.delete(queue, jobID);
    }

    /**
     * Delete a job from the queue.
     *
     * @param queue
     * @param jobID
     * @throws LmstfyException
     */
    public void delete(String queue, String jobID) throws LmstfyException {
        HttpUrl url = genServiceUrlBuilder(PATH_API, this.namespace, queue, "job", jobID)
                .build();

        LmstfyResponse response = doRequest("DELETE", url, null);
        switch (response.getCode()) {
            case HTTP_NO_CONTENT:
                return;
            case HTTP_BAD_REQUEST:
                ErrorResponse errorResponse = response.unmarshalBody(ErrorResponse.class);
                throw new LmstfyIllegalRequestException(response.getCode(), errorResponse.getError());
            default:
                throw new LmstfyUnexpectedException(response.getCode());
        }
    }


    /**
     * Get queue size. how many jobs are ready for consuming
     *
     * @param queue
     * @return
     * @throws LmstfyException
     */
    public int queueSize(String queue) throws LmstfyException {
        HttpUrl url = genServiceUrlBuilder(PATH_API, this.namespace, queue, "size")
                .build();

        LmstfyResponse response = doRequest("GET", url, null);
        switch (response.getCode()) {
            case HTTP_OK:
                QueueSizeResponse queueSizeResponse = response.unmarshalBody(QueueSizeResponse.class);
                return queueSizeResponse.getSize();
            default:
                throw new LmstfyUnexpectedException(response.getCode());
        }
    }

    /**
     * Peek the job in the head of the queue
     *
     * @param queue
     * @return
     * @throws LmstfyException
     */
    public Job peekQueue(String queue) throws LmstfyException {
        HttpUrl url = genServiceUrlBuilder(PATH_API, this.namespace, queue, "peek")
                .build();
        return this.peek(url);
    }

    /**
     * Peek a specific job data
     *
     * @param queue
     * @param jobID
     * @return
     * @throws LmstfyException
     */
    public Job peekJob(String queue, String jobID) throws LmstfyException {
        HttpUrl url = genServiceUrlBuilder(PATH_API, this.namespace, queue, "job", jobID)
                .build();
        return this.peek(url);
    }

    private Job peek(HttpUrl url) throws LmstfyException {
        LmstfyResponse response = doRequest("GET", url, null);
        switch (response.getCode()) {
            case HTTP_OK:
                Job job = response.unmarshalBody(Job.class);
                job.setData(new String(Base64.getDecoder().decode(job.getBase64Data())));
                return job;
            case HTTP_NOT_FOUND:
                return null;
            default:
                throw new LmstfyUnexpectedException(response.getCode());
        }
    }

    private HttpUrl.Builder genServiceUrlBuilder(String... pathSegments) {
        HttpUrl.Builder builder = serviceAddress.newBuilder();
        for (String pathSegment : pathSegments) {
            builder.addPathSegment(pathSegment);
        }
        return builder;
    }
}