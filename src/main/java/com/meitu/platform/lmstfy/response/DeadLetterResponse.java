package com.meitu.platform.lmstfy.response;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-10
 */
public class DeadLetterResponse {

    private String namespace;
    private String queue;
    @SerializedName("deadletter_size")
    private int deadLetterSize;
    @SerializedName("deadletter_head")
    private String deadLetterHead;

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

    public int getDeadLetterSize() {
        return deadLetterSize;
    }

    public void setDeadLetterSize(int deadLetterSize) {
        this.deadLetterSize = deadLetterSize;
    }

    public String getDeadLetterHead() {
        return deadLetterHead;
    }

    public void setDeadLetterHead(String deadLetterHead) {
        this.deadLetterHead = deadLetterHead;
    }
}
