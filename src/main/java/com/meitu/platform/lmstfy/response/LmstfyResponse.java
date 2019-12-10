package com.meitu.platform.lmstfy.response;

import com.google.gson.Gson;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-06
 */
public class LmstfyResponse {

    private static final String HEADER_REQUEST_ID = "X-Request-ID";

    private Gson gson = new Gson();


    private String body;
    private int code;
    private String requestID;

    public LmstfyResponse(Response response) throws IOException {
        this.code = response.code();
        this.requestID = response.header(HEADER_REQUEST_ID);
        this.body = response.body().string();
    }

    public LmstfyResponse() {
    }

    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public String getRequestID() {
        return requestID;
    }

    public <T> T unmarshalBody(Class<T> tClass) {
        return gson.fromJson(this.body, tClass);
    }
}
