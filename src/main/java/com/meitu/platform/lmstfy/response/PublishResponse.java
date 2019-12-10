package com.meitu.platform.lmstfy.response;

import com.google.gson.annotations.SerializedName;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class PublishResponse extends LmstfyResponse {

    private String msg;

    @SerializedName("job_id")
    private String jobID;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
}
