package com.meitu.platform.lmstfy.response;

import com.google.gson.Gson;
import com.meitu.platform.lmstfy.Job;
import okhttp3.Response;

import java.io.IOException;
import java.util.Base64;

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

    public Job unmarshalToJob() {
        Job job = gson.fromJson(this.body, Job.class);
        if (job.getBase64Data() != null) {
            job.setData(new String(Base64.getDecoder().decode(job.getBase64Data())));
        }
        return job;
    }

    public Job[] unmarshalToJobs() {
        Job[] jobs = gson.fromJson(this.body, Job[].class);
        for (Job job : jobs) {
            if (job.getBase64Data() != null) {
                job.setData(new String(Base64.getDecoder().decode(job.getBase64Data())));
            }
        }
        return jobs;
    }
}
