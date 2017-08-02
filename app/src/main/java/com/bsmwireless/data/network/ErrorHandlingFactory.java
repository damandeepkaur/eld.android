package com.bsmwireless.data.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ErrorHandlingFactory extends CallAdapter.Factory {
    private final RxJava2CallAdapterFactory mOriginalFactory;

    private ErrorHandlingFactory() {
        mOriginalFactory = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new ErrorHandlingFactory();
    }

    @Override
    public CallAdapter<?, ?> get(@NonNull final Type returnType, @NonNull final Annotation[] annotations, @NonNull final Retrofit retrofit) {
        return new RxCallAdapterWrapper<>(mOriginalFactory.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Observable<R>> {
        private final CallAdapter<R, ?> mWrappedCallAdapter;

        public RxCallAdapterWrapper(final CallAdapter<R, ?> wrapped) {
            mWrappedCallAdapter = wrapped;
        }

        @Override
        public Type responseType() {
            return mWrappedCallAdapter.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Observable<R> adapt(@NonNull final Call<R> call) {
            return ((Observable) mWrappedCallAdapter.adapt(call)).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                @Override
                public Observable apply(final Throwable throwable) {
                    return Observable.error(getRetrofitException(throwable));
                }
            });
        }

        private RetrofitException getRetrofitException(final Throwable throwable) {
            // Non-200 http error
            if (throwable instanceof HttpException) {
                final HttpException httpException = (HttpException) throwable;
                final Response response = httpException.response();
                return RetrofitException.httpError(response);
            }

            // A network error
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            // An unknown error
            return RetrofitException.unexpectedError(throwable);
        }
    }
}

