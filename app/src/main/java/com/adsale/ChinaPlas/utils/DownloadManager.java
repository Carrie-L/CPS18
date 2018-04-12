package com.adsale.ChinaPlas.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

/**
 * Created by Carrie on 2018/4/11.
 */

public class DownloadManager {

    private static final String TAG = "DownLoadManager";

    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    private static String MP4_CONTENTTYPE = "video/mp4";

    private static String PDF_CONTENTTYPE = "video/mp4";

    private static String FLV_CONTENTTYPE = "video/x-flv";

    private static File sFutureStudioIconFile;
    private static long sFileSize;
    private static String fileName = "";

    public static String getFileName() {
        return fileName;
    }

    public static File getFutureStudioIconFile() {
        return sFutureStudioIconFile;
    }

    public static boolean writeFile(ResponseBody body, String fileName) {
        String sPath = Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/DocumentsPDF/" + fileName;

        Log.d(TAG, "path:>>>>" + sPath);

        try {
            sFutureStudioIconFile = new File(sPath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[8192];

                //需要下载的文件总长度
                if (sFileSize == 0) {
                    sFileSize = body.contentLength();
                }
                //已经下载的文件长度
                long fileSizeDownloaded = sFutureStudioIconFile.length();

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(sFutureStudioIconFile, true);

//                progressBar.setMax((int) sFileSize);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

//                    progressBar.setProgress((int) fileSizeDownloaded);

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + sFileSize);
                }

                outputStream.flush();


                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    public static boolean save(ResponseBody body, String fileName, long startsPoint) {
        InputStream in = body.byteStream();
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        String sPath = Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/DocumentsPDF/" + fileName;
        Log.d(TAG, "path:>>>>" + sPath);

        try {
            sFutureStudioIconFile = new File(sPath);
            randomAccessFile = new RandomAccessFile(sFutureStudioIconFile, "rwd");
            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            channelOut = randomAccessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                in.close();
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
