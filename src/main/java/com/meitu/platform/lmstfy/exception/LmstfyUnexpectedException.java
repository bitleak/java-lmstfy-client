package com.meitu.platform.lmstfy.exception;

/**
 * Description:
 *
 * @author Yesphet
 * @date 2019-12-05
 */
public class LmstfyUnexpectedException extends LmstfyException {

    private int code;

    public LmstfyUnexpectedException(int code) {
        super(String.format("unexpected lmstfy response status %d", code));
        this.code = code;
    }

}
