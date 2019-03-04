package com.adsale.ChinaPlas.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

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
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Carrie on 2017/8/10.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static String getSDRootPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory() + "/";
        } else {
            return Environment.getDataDirectory() + "/";

        }
    }

    public static boolean saveToMemory(String dir, String fileName, byte[] bytes) {
        createFile(dir);
        File file = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveToMemory(String absPath, byte[] bytes) {
        File file = new File(absPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String createFile(String absPath) {
        if (!absPath.endsWith("/")) {
            absPath += "/";
        }
        LogUtil.i(TAG, "absPath1=" + absPath);

//       if(createDir(absPath)) {
//           new File(absPath).mkdir();
//       }

        File file = new File(absPath);
        if (!file.exists()) {
            boolean isCreateSuccess = file.mkdir();
            LogUtil.i(TAG, "createFile：isCreateSuccess=" + isCreateSuccess);
        }
        return absPath;
    }

    public static boolean createDir(String path) {
        LogUtil.i(TAG, "old path=" + path);
        if (!new File(path).exists()) {

            if (new File(path).mkdir()) {
                return false;
            } else {
                boolean mkdirs = new File(path).mkdirs();
                LogUtil.i(TAG, "mkdirs=" + mkdirs);
                return false;
            }

//            String path1 = AppUtil.subStringFront(path, "/");
//            LogUtil.i(TAG, "path1=" + path1);
//
//            String newPath = AppUtil.subStringFront(path1, "/");
//            LogUtil.i(TAG, "newPath=" + newPath);
//
//            // 看它的上一个文件夹是否存在
//            if (new File(newPath).exists()) {
//                (new File(path)).mkdir();
//                return false;
//            } else {
//                createDir(newPath);
//                return true;
//            }
        }
        return false;
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
        LogUtil.i(TAG, "> zipPath=" + zipPath + ",zipname=" + zipname);

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

                    if (ze.isDirectory()) {  // 例： Transportation/
                        FileUtil.createDir(zipPath);
                        File fmd = new File(zipPath + filename);
                        fmd.mkdirs();
                        continue;
                    } else {   // e.g: 100/EN.html
                        File file = new File(AppUtil.subStringFront1(zipPath + filename, '/'));
                        if (!file.exists()) {
                            file.mkdir();
                        }
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

    public static boolean writeZipToMemory(Response<ResponseBody> response, String zipName, String zipPath) {
        ResponseBody body = response.body();
        if (body != null) {
            LogUtil.i(TAG, "body != null");
            FileUtil.unpackZip(zipName, body.byteStream(), zipPath);
            body.close();
            return true;
        }
        LogUtil.i(TAG, "body == null");
        return false;
    }


    /**
     * 从内存或asset目录下读取文件
     * !!! Attention !!!  为 App.rootDir 目录下
     *
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
     *
     * @param fileName "xxx.txt"
     */
    public static String readFilesDirFile(String fileName) {
        if (new File(App.filesDir + fileName).exists()) {
            return readMemoryFile(App.filesDir + fileName);
        } else {
            return readAssetFile("files/" + fileName);
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

    public static File getPDFFile() {
        return mPdfFile;
    }

    private static File mPdfFile;

    public static boolean writeFile(ResponseBody body, String fileName) {
        String sPath = Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/DocumentsPDF/" + fileName;
        Log.d(TAG, "path:>>>>" + sPath);
        mPdfFile = new File(sPath);

        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[8192];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(mPdfFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
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

    public static boolean writeFile(InputStream is, String absolutePath) {
        LogUtil.i(TAG, "writeFile path:>>>>" + absolutePath);
        if (is == null) {
            LogUtil.i(TAG, "writeFile is==null");
        }


        try {
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[8192];
                outputStream = new FileOutputStream(new File(absolutePath));
                while (true) {
                    int read = is.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (is != null) {
                    is.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean writeFile(String content, String fileName) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(App.filesDir + fileName);
            byte[] buf = content.getBytes();
            stream.write(buf);
            stream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 复制到指定文件夹下
     *
     * @param in
     * @param dirName 文件夹名称 如 ConcurrentEvent/
     * @return
     */
    public static boolean copyFile(InputStream in, String dirName) {
        boolean result = false;
        System.out.println("copy file");
        File toFile;
        toFile = new File(App.rootDir + dirName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            while ((in.read(buffer)) != -1) {
                out.write(buffer);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = true;

        return result;
    }


}
