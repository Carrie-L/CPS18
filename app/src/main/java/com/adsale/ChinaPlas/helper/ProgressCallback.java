package com.adsale.ChinaPlas.helper;

/**
 * Created by Carrie on 2017/10/31.
 */

public interface ProgressCallback {
    /**
     * @param progress     已经下载或上传字节数
     * @param total        总字节数
     * @param done         是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
