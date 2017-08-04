package com.bsmwireless.data.network;

import java.io.IOException;

import retrofit2.Response;

public class RetrofitException extends RuntimeException {
    public static final int NETWORK_ERROR_CODE = 1;
    public static final int UNEXPECTED_ERROR_CODE = 0;

    public enum Type {
        NETWORK,
        HTTP,
        UNEXPECTED
    }

    private Type mType;
    private int mCode;

    public static RetrofitException httpError(Response response) {
        String message = response.code() + " " + response.message();
        return new RetrofitException(Type.HTTP, response.code(), message, null);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(Type.NETWORK, NETWORK_ERROR_CODE, exception.getMessage(), exception);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(Type.UNEXPECTED, UNEXPECTED_ERROR_CODE, exception.getMessage(), exception);
    }

    private RetrofitException(Type type, int code, String message, Throwable exception) {
        super(message, exception);
        mType = type;
        mCode = code;
    }

    public Type getType() {
        return mType;
    }

    public int getCode() {
        return mCode;
    }
}


