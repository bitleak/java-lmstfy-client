package com.meitu.platform.lmstfy.exception;

import com.google.gson.Gson;
import com.meitu.platform.lmstfy.response.ErrorResponse;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class LmstfyIllegalRequestException extends LmstfyException {

    private int code;

    private String requestID;

    private static Gson gson = new Gson();


    public LmstfyIllegalRequestException(Response response) {
        response.body();

    }

    public LmstfyIllegalRequestException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("illegal lmstfy request, status: %d, error: %s", code, this.getMessage());
    }
}
