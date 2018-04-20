package com.github.natanbc.weeb4j.util;

import com.github.natanbc.reliqua.request.RequestContext;
import com.github.natanbc.reliqua.request.RequestException;
import com.github.natanbc.weeb4j.MissingScopeException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

@SuppressWarnings("WeakerAccess")
public class RequestUtils {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    public static RequestBody toBody(JSONObject json) {
        return RequestBody.create(MEDIA_TYPE_JSON, json.toString());
    }

    public static JSONObject toJSONObject(Response response) {
        return new JSONObject(new JSONTokener(getInputStream(response)));
    }

    public static InputStream getInputStream(Response response) {
        ResponseBody body = response.body();
        if(body == null) throw new IllegalStateException("Body should never be null");
        String encoding = response.header("Content-Encoding");
        if (encoding != null) {
            switch(encoding.toLowerCase()) {
                case "gzip":
                    try {
                        return new GZIPInputStream(body.byteStream());
                    } catch(IOException e) {
                        throw new IllegalStateException("Received Content-Encoding header of gzip, but data is not valid gzip", e);
                    }
                case "deflate":
                    return new InflaterInputStream(body.byteStream());
            }
        }
        return body.byteStream();
    }

    public static <T> void handleError(RequestContext<T> context) {
        Response response = context.getResponse();
        ResponseBody body = response.body();
        if(body == null) {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + " (No body)", context.getCallStack()));
            return;
        }
        JSONObject json = null;
        try {
            json = toJSONObject(response);
        } catch(JSONException ignored) {}
        handleErrorCode(json, context);
    }

    public static <T> void handleErrorCode(JSONObject json, RequestContext<T> context) {
        Response response = context.getResponse();
        switch(response.code()) {
            case 403:
                context.getErrorConsumer().accept(new MissingScopeException(json == null ? null : json.getString("message"), context.getCallStack()));
                break;
            case 404:
                context.getSuccessConsumer().accept(null);
                break;
            default:
                if(json != null) {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + ": " + json.getString("message"), context.getCallStack()));
                } else {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code(), context.getCallStack()));
                }
        }
    }
}
