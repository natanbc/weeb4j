package com.github.natanbc.weeb4j;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * This class represents a request which has not yet been scheduled to execute.
 *
 * <br>The request is only executed when {@link #execute() execute}, {@link #async(Consumer, Consumer) async} or
 * {@link #submit() submit} are called.
 *
 * <br>This request may be executed more than once.
 *
 * This class was inspired by <a href="https://github.com/DV8FromTheWorld/JDA">JDA</a>'s
 * <a href="https://github.com/DV8FromTheWorld/JDA/blob/907f766537a18b610ed8a2cedf95cf6754cf50ee/src/main/java/net/dv8tion/jda/core/requests/RestAction.java">RestAction</a> class.
 *
 * @param <T> The type of object returned by this request.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class PendingRequest<T> {
    private final Weeb4J api;
    private final Request httpRequest;

    public PendingRequest(Weeb4J api, Request.Builder httpRequestBuilder) {
        this.api = api;
        this.httpRequest = httpRequestBuilder.build();
    }

    @Nullable
    protected abstract T mapData(@Nonnull ResponseBody response) throws IOException;

    /**
     * Execute this request asynchronously. Cancelling the returned future has no effect.
     *
     * @return A future representing this request.
     */
    @Nonnull
    public Future<T> submit() {
        CompletableFuture<T> future = new CompletableFuture<>();
        async(future::complete, future::completeExceptionally);
        return future;
    }

    /**
     * Execute this request synchronously. The current thread is blocked until it completes.
     *
     * @return The response received from the API.
     */
    @Nullable
    public T execute() {
        try {
            return submit().get();
        } catch(ExecutionException e) {
            throw new RequestException(e.getCause());
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RequestException(e);
        }
    }

    /**
     * Execute this request asynchronously, calling the appropriate callback when it's done.
     *
     * @param onSuccess Called when this request completes successfully.
     * @param onError Called when there's an error executing the request or parsing the response.
     */
    public void async(Consumer<T> onSuccess, Consumer<RequestException> onError) {
        StackTraceElement[] callSite = api.isTrackingCallSites() ? Thread.currentThread().getStackTrace() : null;
        if(onSuccess == null) onSuccess = v->{};
        if(onError == null) onError = Throwable::printStackTrace;

        Consumer<T> finalOnSuccess = onSuccess;
        Consumer<RequestException> finalOnError = onError;

        api.getClient().newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                finalOnError.accept(new RequestException(e, callSite));
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) {
                try {
                    ResponseBody body = response.body();
                    if(response.code() != 200) {
                        String s = "Server returned unexpected status code " + response.code() + (body == null ? "" : " Body: " + body.string());
                        response.close();
                        finalOnError.accept(new RequestException(s, callSite));
                        return;
                    }
                    if(body == null) {
                        throw new AssertionError("body is null(???)");
                    }
                    try {
                        finalOnSuccess.accept(mapData(body));
                    } finally {
                        response.close();
                    }
                } catch(RequestException e) {
                    finalOnError.accept(e);
                } catch(Exception e) {
                    finalOnError.accept(new RequestException(e, callSite));
                }
            }
        });
    }

    /**
     * Execute this request asynchronously, calling the appropriate callback when it's done.
     *
     * @param onSuccess Called when this request completes successfully.
     */
    public void async(Consumer<T> onSuccess) {
        async(onSuccess, null);
    }

    /**
     * Execute this request asynchronously.
     */
    public void async() {
        async(null, null);
    }
}
