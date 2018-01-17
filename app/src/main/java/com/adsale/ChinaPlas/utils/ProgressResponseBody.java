package com.adsale.ChinaPlas.utils;


import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.helper.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Carrie on 2017/10/31.
 */

public class ProgressResponseBody<T> extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressCallback mCallback;
    private BufferedSource bufferedSource;
    private T entity;

    public ProgressResponseBody(ResponseBody responseBody, ProgressCallback callback, T t) {
        this.responseBody = responseBody;
        this.mCallback = callback;
        this.entity = t;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                mCallback.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1,entity);
                return bytesRead;
            }
        };
    }
}
