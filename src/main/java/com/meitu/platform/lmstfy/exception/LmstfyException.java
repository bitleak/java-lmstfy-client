package com.meitu.platform.lmstfy.exception;

import com.meitu.platform.lmstfy.client.LmstfyClient;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class LmstfyException extends Exception {

    public LmstfyException() {
    }

    public LmstfyException(String msg) {
        super(msg);
    }

    public LmstfyException(Throwable throwable) {
        super(throwable);
    }

    public LmstfyException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
