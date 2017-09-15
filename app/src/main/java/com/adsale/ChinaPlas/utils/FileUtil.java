package com.adsale.ChinaPlas.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.adsale.ChinaPlas.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.R.attr.path;

/**
 * Created by Carrie on 2017/8/10.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static boolean saveToMemory(String dir, String fileName, byte[] bytes) {
        File file = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String createFile(String absPath) {
        LogUtil.i(TAG, "absPath0=" + absPath);
        if (!absPath.contains("/")) {
            absPath += "/";
        }
        LogUtil.i(TAG, "absPath1=" + absPath);
        File file = new File(absPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return absPath;
    }

    /**
     * <font color="#f97798">解压zip</font>
     *
     * @param zipname eg:temp_20160905144037995.zip
     * @param zipPath zip包的绝对文件夹（不包含.zip，.zip的上一级）eg: /data/user/0/com.adsale.ChinaPlas/app_cps18/WebContent/MI00000046/
     * @return boolean
     * @version 创建时间：2016年6月3日 上午9:33:06
     */
    public static boolean unpackZip(String zipname, InputStream is, String zipPath) {
        long startTime = System.currentTimeMillis();
        if (!zipPath.contains("/")) {
            zipPath += "/";
        }
        ZipInputStream zis;
        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            if (!TextUtils.isEmpty(zipname)) {

                if (zipname.contains(".zip")) {
                    zipname = zipname.replace(".zip", "");
                }

                if (!new File(zipPath).exists()) {
                    new File(zipPath).mkdir();
                }
                boolean unpack = false;
                while ((ze = zis.getNextEntry()) != null) {
                    filename = ze.getName();
                    if (ze.isDirectory()) {
                        File fmd = new File(zipPath + filename);
                        fmd.mkdirs();
                        continue;
                    }
                    FileOutputStream fout = new FileOutputStream(zipPath + filename);
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                    fout.close();
                    zis.closeEntry();
                    unpack = true;
                }
                zis.close();
                return unpack;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "解压" + zipname + "的时间为" + (endTime - startTime) + "ms");
        return false;
    }


    /**
     * 从内存或asset目录下读取文件
     *!!! Attention !!!  为 App.rootDir 目录下
     * @param filePath 如："Txt/advertisement.txt"
     */
    public static String readRootDirFile(String filePath) {
        if (new File(App.rootDir + filePath).exists()) {
            return readMemoryFile(App.filesDir + filePath);
        } else {
            return readAssetFile(filePath);
        }
    }

    /**
     * !!! Attention !!!  文件位于 data/data/com.adsale.ChinaPlas/files/ 目录下 或 assets的 files/ 目录下
     * @param fileName "xxx.txt"
     */
    public static String readFilesDirFile(String fileName){
        if (new File(App.filesDir + fileName).exists()) {
            return readMemoryFile(App.filesDir + fileName);
        } else {
            return readAssetFile("files/"+fileName);
        }
    }

    /**
     * @param path 完整的绝对路径
     * @return str||""
     */
    public static String readMemoryFile(String path) {
        LogUtil.e(TAG, "-----readMemoryFile----" + path);
        try {
            return bufferReadFile(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取Asset下的文件
     *
     * @param filePath 如："Txt/advertisement.txt"
     * @return str||""
     * @version 创建时间：2016年4月6日 下午5:28:44
     */
    public static String readAssetFile(String filePath) {
        LogUtil.e(TAG, "-----readAssetFile----" + filePath);
        try {
            return bufferReadFile(getAssetInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 读取Asset文件
     *
     * @param fileName eg:"xx.txt"
     * @return InputStream||null
     * @version 创建时间：2016年4月12日 下午7:24:56
     */
    public static InputStream getAssetInputStream(String fileName) throws IOException {
        return App.mAssetManager.open(fileName);
    }

    /**
     * @return str||""
     */
    public static String bufferReadFile(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder("");
            String line;
            // 循环读取文件内容
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
