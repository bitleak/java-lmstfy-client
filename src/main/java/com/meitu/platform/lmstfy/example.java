package com.meitu.platform.lmstfy;

import com.meitu.platform.lmstfy.client.LmstfyClient;
import com.meitu.platform.lmstfy.exception.LmstfyException;
import com.meitu.platform.lmstfy.exception.LmstfyNotJobException;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-06
 */
public class example {

    private static String token = "01DVCJBB8XZTB73SSDCZ4Q6VZN";
    private static String host = "localhost";
    private static int port = 7777;
    private static String namespace = "sdk-test";

    public static void main(String[] args) {
        publish();
        consume();
    }

    private static void publish() {
        LmstfyClient client = new LmstfyClient(host, port, namespace, token);
        try {
            String jobID = client.publish("sdk-test-queue", "test data".getBytes(), 5, (short) 2, 0);
            System.out.println(jobID);
        } catch (LmstfyException e) {
            e.printStackTrace();
        }
    }

    private static void consume() {
        LmstfyClient client = new LmstfyClient(host, port, namespace, token);
        try {
            Job job = client.consume(3, 2, "sdk-test-queue");
            System.out.println(job.getData());
        } catch (LmstfyNotJobException e) {
            System.out.println("There has no available job");
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

}
