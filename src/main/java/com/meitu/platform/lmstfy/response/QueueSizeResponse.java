package com.meitu.platform.lmstfy.response;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-09
 */
public class QueueSizeResponse {

    private String namespace;
    private String queue;
    private int size;

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
