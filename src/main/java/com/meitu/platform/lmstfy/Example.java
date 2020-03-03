package com.meitu.platform.lmstfy;

import com.meitu.platform.lmstfy.client.LmstfyClient;
import com.meitu.platform.lmstfy.exception.LmstfyException;
import com.meitu.platform.lmstfy.exception.LmstfyNotJobException;
import com.meitu.platform.lmstfy.response.DeadLetterResponse;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-06
 */
public class Example {

    private static String token = "01E2FQ865WA27WWP9NEWCQ08R3";
    private static String host = "localhost";
    private static int port = 7777;
    private static String namespace = "sdk-test";
    private static String queue = "sdk-test-queue";
    private static LmstfyClient client = new LmstfyClient(host, port, namespace, token);

    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            publish();
        }
        consume();
        deleteAndAck();
        batchConsume();
        queueSize();
        peekQueue();
        peekJob();
        peekDeadLetter();
        respawnDeadLetter();
    }

    private static void publish() {
        try {
            String jobID = client.publish(queue, "test data".getBytes(), 5, (short) 2, 0);
            System.out.println(jobID);
        } catch (LmstfyException e) {
            e.printStackTrace();
        }
    }

    private static void consume() {
        try {
            Job job = client.consume(3, 2, queue);
            System.out.println(job.getData());
        } catch (LmstfyNotJobException e) {
            System.out.println("There has no available job");
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void batchConsume() {
        try {
            Job[] jobs = client.batchConsume(3, 3, 2, queue);
            System.out.println(jobs.length);
        } catch (LmstfyNotJobException e) {
            System.out.println("There has no available job");
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void deleteAndAck() {
        try {
            String jobID1 = client.publish(queue, "test data".getBytes(), 5, (short) 2, 0);
            client.delete(queue, jobID1);
            String jobID2 = client.publish(queue, "test data".getBytes(), 5, (short) 2, 0);
            client.ack(queue, jobID2);
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void queueSize() {
        try {
            int size1 = client.queueSize(queue);
            client.publish(queue, "test data".getBytes(), 5, (short) 2, 0);
            int size2 = client.queueSize(queue);
            System.out.printf("%d %d\n", size1, size2);
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void peekQueue() {
        try {
            Job job = client.peekQueue(queue);
            if (job == null) {
                System.out.println("There has no available job to peek");
                return;
            }
            if (job.getData() == null) {
                System.out.printf("The job %s is expired\n", job.getJobID());
            } else {
                System.out.println(job.getData());
            }
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void peekJob() {
        try {
            String jobID = client.publish(queue, "test data".getBytes(), 5, (short) 2, 0);
            Job job = client.peekJob(queue, jobID);
            if (job == null) {
                System.out.println("There has no available job");
            } else {
                System.out.printf("%s\n", job.getData());
            }
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void peekDeadLetter() {
        try {
            DeadLetterResponse deadLetterResponse = client.peekDeadLetter(queue);
            System.out.println(deadLetterResponse.getDeadLetterSize());
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }

    private static void respawnDeadLetter() {
        try {
            int count = client.respawnDeadLetter(queue, 1, 60);
            System.out.println("Respawn deadletter count: " + count);
        } catch (LmstfyException e) {
            System.out.println("Request Lmstfy error, " + e.getMessage());
        }
    }


}
