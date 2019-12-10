package com.meitu.platform.lmstfy;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class Job {

    private String namespace;
    private String queue;
    @SerializedName("job_id")
    private String jobID;
    @SerializedName("data")
    private String base64Data;
    private int ttl;
    @SerializedName("elapsed_ms")
    private int elapsedMS;
    @SerializedName("remain_tries")
    private int remainTries;
    private transient String data;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String data) {
        this.base64Data = data;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getElapsedMS() {
        return elapsedMS;
    }

    public void setElapsedMS(int elapsedMS) {
        this.elapsedMS = elapsedMS;
    }

    public int getRemainTries() {
        return remainTries;
    }

    public void setRemainTries(int remainTries) {
        this.remainTries = remainTries;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
