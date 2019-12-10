package com.meitu.platform.lmstfy.client;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description: Retry when receiving not 200 ~ 499 response
 *
 * @author Yesphet
 * @date 12/05/2019
 */
public class OkHttpRetryInterceptor implements Interceptor {

    private int retryTimes = 0;
    private long retryIntervalMilliSecond = 0;

    public OkHttpRetryInterceptor(int retryTimes, long retryIntervalMilliSecond) {
        this.retryTimes = retryTimes;
        this.retryIntervalMilliSecond = retryIntervalMilliSecond;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;

        for (int i = 0; i <= retryTimes; i++) {
            response = chain.proceed(chain.request());
            if (response == null || (response.code() >= 200 && response.code() < 500)) {
                return response;
            }
            if (i < retryTimes && retryIntervalMilliSecond > 0) {
                try {
                    Thread.sleep(retryIntervalMilliSecond);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        return response;
    }
}
