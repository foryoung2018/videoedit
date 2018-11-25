package com.wmlive.networklib.retrofit;


import com.wmlive.networklib.util.NetLog;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by lsq on 11/4/2016.
 */

public class HttpLoggerInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {

        NONE,

        BASIC,

        HEADERS,

        BODY
    }


    public HttpLoggerInterceptor() {
    }

    public HttpLoggerInterceptor(Level level) {
        this.level = level;
    }


    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor NetLogs.
     */
    public HttpLoggerInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        printRequest(request);

        boolean NetLogBody = level == Level.BODY;
        boolean NetLogHeaders = NetLogBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!NetLogHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        NetLog.e(requestStartMessage);
        if (NetLogHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    NetLog.e("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    NetLog.e("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly NetLogged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    NetLog.e(name + ": " + headers.value(i));
                }
            }

            if (!NetLogBody || !hasRequestBody) {
                NetLog.e("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                NetLog.e("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                NetLog.e("");
                if (isPlaintext(buffer)) {
                    NetLog.e(buffer.readString(charset));
                    NetLog.e("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    NetLog.e("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            NetLog.e("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        NetLog.e("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!NetLogHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (NetLogHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                NetLog.e(headers.name(i) + ": " + headers.value(i));
            }

            if (!NetLogBody || !HttpHeaders.hasBody(response)) {
                NetLog.e("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                NetLog.e("<-- END HTTP (encoded body omitted)");
            } else {
                return printBody(response);
            }
        }
        return printBody(response);
    }

    private Response printBody(Response response) {
        if (response == null) {
            return null;
        }
        BufferedSource source = response.body().source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = response.body().contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                NetLog.e("");
                NetLog.e("Couldn't decode the response body; charset is likely malformed.");
                NetLog.e("<-- END HTTP");

                return response;
            }
        }

        if (!isPlaintext(buffer)) {
            NetLog.e("");
            NetLog.e("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
            return response;
        }

        if (response.body().contentLength() != 0) {
            NetLog.e(buffer.clone().readString(charset));
        }

        NetLog.e("<-- END HTTP (" + buffer.size() + "-byte body)");
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private void printRequest(Request request) throws UnsupportedEncodingException {
        if (request != null) {
            NetLog.e("request method:" + request.method());
            if (request.url() != null) {
                //打印GET请求参数
                Set<String> set = request.url().queryParameterNames();
                for (String s : set) {
                    NetLog.e("request key:" + s
                            + " --> value:" + request.url().queryParameterValues(s).toString());
                }
            }

            if (request.body() != null && request.body() instanceof FormBody) {
                //打印FormBody类型参数
                FormBody oldFormBody = (FormBody) request.body();
                for (int i = 0; i < oldFormBody.size(); i++) {
                    NetLog.e("request key:" + URLDecoder.decode(oldFormBody.encodedName(i), "utf-8")
                            + " --> value:" + URLDecoder.decode(oldFormBody.encodedValue(i), "utf-8"));
                }
            }
        }
    }
}
