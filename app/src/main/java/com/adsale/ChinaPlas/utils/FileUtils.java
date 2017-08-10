package com.adsale.ChinaPlas.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

public class FileUtils {
    public static final String TAG = "FileUtils";

    public static String SD_ROOT_DIR = "";
    private static String mFileRootDir = "";

    public static String getFileRootDir(Context context) {
        if (TextUtils.isEmpty(SD_ROOT_DIR)) {
            SD_ROOT_DIR = context.getApplicationInfo().processName + "/";
        }

        if (TextUtils.isEmpty(mFileRootDir))
            mFileRootDir = getRootPath() + SD_ROOT_DIR;

        File dirFile = new File(mFileRootDir);
        if (!dirFile.exists() || !dirFile.isDirectory())
            dirFile.mkdirs();

        File nomediafFile = new File(mFileRootDir + ".nomedia");
        try {
            if (!nomediafFile.exists())
                nomediafFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getFileRootDir:" + mFileRootDir);
        return mFileRootDir;
    }

    private static String FILE_PATH;
    private static final int FILE_SIZE = 1024;

    static {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            FILE_PATH = Environment.getExternalStorageDirectory() + "/";
        } else {
            FILE_PATH = Environment.getDataDirectory() + "/";
            // FILE_PATH = "/data" +
            // Environment.getDataDirectory().getAbsolutePath() +
            // "/"+SD_ROOT_DIR+"file/";
        }
    }

    /**
     * 在SD卡或手机内存上创建文件
     */
    public static File createFile(String fileName) {
        File file = null;
        if (fileName.indexOf(FILE_PATH) != -1)
            fileName = fileName.replace(FILE_PATH, "");
        try {
            file = new File(FILE_PATH);
            String[] fileDer = fileName.split("/");
            if (fileName.length() >= 1) {
                for (int i = 0; i < fileDer.length - 1; i++) {
                    file = new File(file, fileDer[i]);
                    if (!file.exists()) {
//						Log.d(TAG, "create FileDir :" + file.getName());
                        file.mkdirs();
                    }
                }
            }
            if (fileName.lastIndexOf("/") != fileName.length() - 1) {
                file = new File(FILE_PATH + fileName);
                if (!file.exists()) {
//					Log.d(TAG, "create file :" + fileName);
                    file.createNewFile();
                }
            } else {
                file = new File(FILE_PATH + fileName);
                if (!file.exists()) {
//					Log.d(TAG, "create FileDir:" + file.getName());
                    file.mkdirs();
                }
            }
//			Log.d(TAG, "create file succeed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 在SD卡或手机内存上创建文件
     */
    public static File createFileByFullPath(String fileName) throws IOException {

        File file = null;
        String[] fileDer = fileName.split("/");
        if (fileName.length() >= 1) {
            for (int i = 0; i < fileDer.length - 1; i++) {
                file = new File(file, fileDer[i]);
//				Log.d(TAG, "create file :" + file.getName());
                if (!file.exists())
                    file.mkdirs();
            }
        }

//		Log.d(TAG, "create file length:" + fileName.length() + "\n"+ fileDer.length);
        if (fileName.lastIndexOf("/") != fileName.length() - 1) {
            Log.d(TAG, "create file :" + fileName);
            file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } else {
            file = new File(fileName);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
//		Log.d(TAG, "create file succeed");
        return file;
    }

    /**
     * 在SD卡或手机内存上删除文件
     */
    public static boolean deleteFile(String fileName) {
        Boolean blnResult = true;
        if (fileName.indexOf(FILE_PATH) == -1)
            fileName = FILE_PATH + fileName;
        if (isFileExist(fileName)) {
            File file = new File(fileName);
            try {
                blnResult = file.delete();
            } catch (Exception e) {
                blnResult = false;
            }
        }
        return blnResult;
    }

    /**
     * 在SD卡或手机内存上删除文件
     */
    public static boolean deleteFileByFullPath(String fileName) {
        Boolean blnResult = false;
        try {
            if (isFileExistByFullPath(fileName)) {
                File file = new File(fileName);
                blnResult = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnResult;
    }

    /**
     * 在SD卡或手机内存上删除目录
     */
    public static void deleteDir(String pDirPath) {
        if (pDirPath.indexOf(FILE_PATH) == -1)
            pDirPath = FILE_PATH + pDirPath;

        File f = new File(pDirPath);
        if (!f.exists()) {
//			System.out.println("not exists");
            return;
        }
        try {
            if (f.delete()) {
//				System.out.println("delete directory : " + f.getAbsolutePath());
            } else {
                File[] fs = f.listFiles();
                for (int i = 0; i < fs.length; i++) {
                    if (fs[i].isDirectory()) {
                        if (!fs[i].delete())
                            deleteDir(fs[i].getAbsolutePath());
                        else
                            System.out.println("delete directory : "
                                    + fs[i].getAbsolutePath());
                    } else {
                        fs[i].delete();
                        System.out.println("delete file : "
                                + fs[i].getAbsolutePath());
                    }
                }
                f.delete();
//				System.out.println("delete directory : " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在SD卡或手机内存上创建目录，如存在就删除原目录，再创建
     */
    public static void createDir(String dirName) {
        try {
            if (dirName.indexOf(FILE_PATH) == -1)
                dirName = FILE_PATH + dirName;
            File dir = new File(dirName);
            if (dir.exists())
                dir.delete();
            dir.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断SD卡或手机内存上的文件夹是否存在
     */
    public static boolean isFileExist(String fileName) {
        if (fileName.indexOf(FILE_PATH) == -1)
            fileName = FILE_PATH + fileName;
        File file = new File(fileName);
        return file.exists();
    }


    /**
     * 判断SD卡或手机内存上的文件是否存在【路径包含："file:///"】
     */
    public static boolean isFileExistByFullPath(String fullpath) {
        if (fullpath.indexOf("file:///") != -1)
            fullpath = fullpath.replace("file:///", "");
        File file = new File(fullpath);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡或手机内存中
     */
    public static boolean writeFileFromInput(String path, String fileName,
                                             InputStream input) {
        Log.d(TAG, "file path :" + path + fileName);
        boolean result = false;
        File file = null;
        OutputStream output = null;
        try {
            createDir(path);
            file = createFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILE_SIZE];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 重命名文件，输入原文件名及新文件名，输出操作成功与否
     */
    public static boolean renameFromTo(String oldFileName, String newFileName) {
        File file = new File(FILE_PATH + oldFileName);
        return file.renameTo(new File(FILE_PATH + newFileName));
    }

    /**
     * 重命名文件，输入原文件名及新文件名，输出操作成功与否
     */
    public static boolean renameFromToByFullName(String oldFileName,
                                                 String newFileName) {
        File file = new File(oldFileName);
        return file.renameTo(new File(newFileName));
    }

    /**
     * 获取封面图片的名称
     */
    public static List<String> getCoversFromSDCard(String pFilePath) {

        List<String> coverList = new ArrayList<String>();

        File f = new File(pFilePath);
        File[] files = f.listFiles();
        if (files != null) {
            int count = files.length;
            for (int i = 0; i < count; i++) {
                File file = files[i];
                String strFILE_PATH = file.getAbsolutePath();// 获得文件绝对路径
                String strFileName = file.getName();
                String strPath = file.getPath();
//				Log.i(TAG, "FileName:" + strFileName + "   FILE_PATH:"+ strFILE_PATH);
                // 系统自动生成的dcim文件夹除外
                if (strFILE_PATH.endsWith("DCIM"))
                    continue;

                if (strFILE_PATH.endsWith("jpg")
                        || strFILE_PATH.endsWith("gif")
                        || strFILE_PATH.endsWith("bmp")
                        || strFILE_PATH.endsWith("png")) {
                    // 图片文件
                    coverList.add(strFileName);
                } else {
                    // 非图片文件
                    // 目标为文件夹，进入该文件夹继续遍历
                    if (file.isDirectory()) {
                        getCoversFromSDCard(strPath);
                    }
                }
            }
        } else {
            coverList = null;
        }
        return coverList;
    }

    /**
     * 获取存储设备根目录
     */
    public static String getRootPath() {
        return FILE_PATH;
    }

    public static boolean copyFile(String pFromFileName, String pToFileName)
            throws IOException {
        boolean result = false;
        System.out.println("copy file---------from:" + pFromFileName + ";to:"
                + pToFileName);
        File fromFile = new File(pFromFileName);
        if (fromFile.exists()) {
            File toFile;
            if (!isFileExist(pToFileName)) {
                toFile = createFile(pToFileName);
            } else {
                toFile = new File(getRootPath() + pToFileName);
            }
            FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            while ((in.read(buffer)) != -1) {
                out.write(buffer);
            }
            out.flush();
            out.close();
            in.close();
            result = true;
        }
        return result;
    }

    public static boolean copyFile(InputStream in, String pToFileName)
            throws IOException {
        boolean result = false;
        System.out.println("copy file");
        File toFile;
        if (!isFileExist(pToFileName)) {
            toFile = createFile(pToFileName);
        } else {
            toFile = new File(getRootPath() + pToFileName);
        }
        FileOutputStream out = new FileOutputStream(toFile);
        byte[] buffer = new byte[1024];
        while ((in.read(buffer)) != -1) {
            out.write(buffer);
        }
        out.flush();
        out.close();
        in.close();
        result = true;

        return result;
    }

    /**
     * 根据文件路径获取文件的Bytes
     */
    public static byte[] getBytesFromFile(String filePath) {
        byte[] bytes = null;
        try {
            if (filePath.indexOf(FILE_PATH) == -1)
                filePath = FILE_PATH + filePath;

            File file = new File(filePath);

            // 获取文件大小
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                // 文件太大，无法读取
                throw new IOException("File is to large " + file.getName());
            }
            // 创建一个数据来保存文件数据
            InputStream is = new FileInputStream(file);
            bytes = new byte[(int) length];
            // 读取数据到byte数组中
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset < bytes.length) {
                is.close();
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            // Close the input stream and return bytes
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 获取Bitmap的Bytes
     */
    public static byte[] getBytesFromBitmap(Bitmap bm) {
        byte[] bytes = null;
        try {
            if (bm != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                bytes = baos.toByteArray();
                baos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Bitmap LoadImage(String pFilePath, int pWidth, int pHeight) {
        Bitmap oBitmap = null, outBitmap = null;
        if (!new File(pFilePath).exists())
            return null;
        try {
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            if (pHeight != 0 && pWidth != 0) {
                bitmapFactoryOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pFilePath, bitmapFactoryOptions);
                int yRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight
                        / pHeight);
                int xRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth
                        / pWidth);

                if (yRatio > 1 || xRatio > 1) {
                    if (yRatio > xRatio) {
                        bitmapFactoryOptions.inSampleSize = yRatio;
                    } else {
                        bitmapFactoryOptions.inSampleSize = xRatio;
                    }
                } else {
                    bitmapFactoryOptions.inSampleSize = 1;
                }
            } else {
                bitmapFactoryOptions.inSampleSize = 1;
            }
            bitmapFactoryOptions.inPreferredConfig = Config.ARGB_8888;
            bitmapFactoryOptions.inJustDecodeBounds = false;
            oBitmap = BitmapFactory.decodeFile(pFilePath, bitmapFactoryOptions);
            // BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inPreferredConfig = Config.ARGB_8888;
            // options.inPurgeable = true;
            // options.inInputShareable = true;
            // oBitmap= BitmapFactory.decodeFile(pFilePath,options);'
            outBitmap = getResizedBitmap(oBitmap, pWidth, pHeight);
            if (!outBitmap.equals(oBitmap)) {
                oBitmap.recycle();
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return outBitmap;
    }

    public static Bitmap getBitmapFromAsset(Context contex, String fileName,
                                            int pWidth, int pHeight) {
        Bitmap outBitmap = null;
        AssetManager assetManager = contex.getAssets();
        try {
            InputStream istr = assetManager.open(fileName);
            outBitmap = BitmapFactory.decodeStream(istr);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Matrix matrix = new Matrix();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // RESIZE THE BIT MAP
        if (scaleWidth < scaleHeight) {
            newHeight = (int) Math.rint(height * scaleWidth);
            scaleHeight = ((float) newHeight) / height;
        } else {
            newWidth = (int) Math.rint(width * scaleHeight);
            scaleWidth = ((float) newWidth) / width;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, true);
        return resizedBitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int ovalLen = Math.min(width, height);

        float Scaling = (float) (500.00 / ovalLen);
        Matrix matrix = new Matrix();
        matrix.postScale(Scaling, Scaling);
        image = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

        width = image.getWidth();
        height = image.getHeight();
        ovalLen = Math.min(width, height);

        Rect src = new Rect((width - ovalLen) / 2, (height - ovalLen) / 2,
                (width - ovalLen) / 2 + ovalLen, (height - ovalLen) / 2
                + ovalLen);
        Rect dst = new Rect(0, 0, ovalLen, ovalLen);
        Bitmap output = Bitmap.createBitmap(ovalLen, ovalLen, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawOval(new RectF(0, 0, ovalLen, ovalLen), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, src, dst, paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setColor(Color.parseColor("#908fc743"));
        paint2.setStyle(Style.STROKE);
        paint2.setStrokeWidth(width * 0.05f);
        float lt = width * 0.025f;
        float rb = ovalLen - lt;
        canvas.drawOval(new RectF(lt, lt, rb, rb), paint2);
        // canvas.drawCircle(width/2, height/2, ovalLen/2, paint2);
        return output;

    }

    public static String readFromSD(String fileName) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileInputStream fis = new FileInputStream(fileName);
                // 将指定输入流包装成BufferReader
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        fis));
                StringBuilder sb = new StringBuilder("");
                String line = null;
                // 循环读取文件内容
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFileToSD(String text, String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String path=fileName.substring(0, fileName.lastIndexOf("/")+1);
            File filePath = new File(path);
            File file = new File(fileName);

            if (!filePath.exists()) {
                filePath.mkdir();
            }
            if (!file.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(file);
            byte[] buf = text.getBytes();
            stream.write(buf);
            stream.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }
    /**
     * Bitmap保存成Jpeg保存到SdCard
     */
    public static void saveBitmapToSdcard(String filePath, Bitmap img) {
        try {
            if (filePath.equals(""))
                return;
            String strDir = filePath.substring(0, filePath.lastIndexOf("/"));
            File dirFile = new File(strDir);
            if (!dirFile.exists() || !dirFile.isDirectory())
                dirFile.mkdirs();
            else
                deleteFile(filePath);
            File myCaptureFile = new File(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            if (filePath.endsWith(".png"))
                img.compress(Bitmap.CompressFormat.PNG, 100, bos);
            else
                img.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得文件大小
     */
    public static long getFileSizes(File file) throws IOException {
        long s = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            s = fis.available();
            fis.close();
        } else {
            System.out.println("文件不存在");
        }
        return s;
    }

    /**
     * 取得文件夹大小
     */
    public static long getDirSize(File file) throws IOException {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size += getDirSize(flist[i]);
            } else {
                size += flist[i].length();
            }
        }
        return size;
    }

    /**
     * 判断文件是否存在及是否是文件
     */
    public static boolean isFile(String pFILE_PATH) {
        File file = new File(getRootPath() + pFILE_PATH);
        return file.exists() && file.isFile();
    }

    /**
     * 判断文件是否存在及是否是文件夹
     */
    public static boolean isDirectory(String pFILE_PATH) {
        File file = new File(getRootPath() + pFILE_PATH);
        return file.exists() && file.isDirectory();
    }

    /**
     * 保存第一个参数的数据到第二个参数的文件中
     */
    public static boolean copyFileFromString(String pFromString,
                                             String pSavePath) {
        boolean result = false;
        OutputStream out = null;
        try {
            File file = null;
            String strRandomFILE_PATH = "";
            boolean isRandomFile = false;
            if (!FileUtils.isFileExist(pSavePath)) {
                file = FileUtils.createFile(pSavePath);
            } else {
                strRandomFILE_PATH = pSavePath.substring(0,
                        pSavePath.lastIndexOf("/") + 1)
                        + String.valueOf(System.currentTimeMillis())
                        + pSavePath.substring(pSavePath.lastIndexOf("."));
                isRandomFile = true;
                file = FileUtils.createFile(strRandomFILE_PATH);
            }

            out = new FileOutputStream(file);
            byte[] bytes = pFromString.getBytes();
            out.write(bytes);
            out.flush();
            out.close();
            if (isRandomFile) {
                if (FileUtils.deleteFile(pSavePath)) {
                    result = FileUtils.renameFromTo(strRandomFILE_PATH,
                            pSavePath);
                }
            } else
                result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String openFileGetString(String pFilePaht) {
        FileReader reader = null;
        BufferedReader bf = null;
        String strUpdateDate = "";
        String s = "";
        File file = new File(getRootPath() + pFilePaht);
        try {
            if (!file.exists()) {
                file = FileUtils.createFile(pFilePaht);
            } else {
                reader = new FileReader(file);
                bf = new BufferedReader(reader);
                while ((s = bf.readLine()) != null) {
                    strUpdateDate += s;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file = null;
            try {
                if (bf != null)
                    bf.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strUpdateDate;

    }

    public static Boolean writeStringToFile(String strInput, String pFilePath,
                                            boolean append) {
        boolean result = false;
        try {
            File dirFile;
            String path = pFilePath;
            if (pFilePath.indexOf(FILE_PATH) == -1) {
                path = FILE_PATH + pFilePath;
            }
            String dir = path.substring(0, path.lastIndexOf("/"));
            dirFile = new File(dir);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path, append);
            out.write(strInput.getBytes("utf-8"));
            out.close();
            result = true;

//			Log.d(TAG, "writeStringToFile path" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String saveImage(InputStream input, String pFilePath) {
        String path = pFilePath;
        try {
            File dirFile;
            if (pFilePath.indexOf(mFileRootDir) == -1) {
                path = mFileRootDir + pFilePath;
            }
            String dir = path.substring(0, path.lastIndexOf("/"));
            dirFile = new File(dir);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            final File file = new File(path);
            final OutputStream output = new FileOutputStream(file);
            try {
                try {
                    final byte[] buffer = new byte[1024];
                    int read;
                    while ((read = input.read(buffer)) != -1)
                        output.write(buffer, 0, read);
                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//			Log.d(TAG, "writeStringToFile path" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;

    }

    public static String readFile(String path) {
        File file;
        String str = "";
        FileInputStream in;
        try {
            if (path.indexOf(FILE_PATH) == -1) {
                path = FILE_PATH + path;
            }

            file = new File(path);
            in = new FileInputStream(file);
            int length = (int) file.length();
            byte[] temp = new byte[length];
            in.read(temp, 0, length);
            // TODO: 2016/10/11 zszs
//			str = EncodingUtils.getString(temp, "utf-8");
            in.close();
        } catch (IOException e) {

        }
        return str;

    }

    /**
     * SD卡的剩余空间小于某个值返回false，如果有足够的空间，则返回true。
     */
    public static boolean isAvaiableSpace(int sizeMb) {
        boolean ishasSpace = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            long availableSpare = (blocks * blockSize) / (1024 * 1024);
            Log.d(TAG, "剩余空间 availableSpare = " + availableSpare);
            if (availableSpare > sizeMb) {
                ishasSpace = true;
            }
        }
        return ishasSpace;
    }

    public static Bitmap LoadImage(String imgPath, String imgUrl)
            throws Exception {
        Bitmap loadBitmap = null;
        try {
            if (TextUtils.isEmpty(imgUrl) || FileUtils.isFileExist(imgPath)) {
                loadBitmap = BitmapFactory.decodeFile(imgPath);
            } else {
                Log.d(TAG, "Download Image:" + imgUrl);
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                loadBitmap = BitmapFactory.decodeStream(inputStream);
                FileUtils.saveBitmapToSdcard(imgPath, loadBitmap);
            }
        } catch (OutOfMemoryError e1) {
            Log.e("", "OutOfMemoryError");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadBitmap;
    }

    public static int[] getImageSize(String imgPath, String imgUrl)
            throws Exception {
        int[] size = new int[] { 0, 0 };
        if (isFileExist(imgPath)) {
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            bitmapFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, bitmapFactoryOptions);
            size[0] = bitmapFactoryOptions.outWidth;
            size[1] = bitmapFactoryOptions.outHeight;
        } else {
            Bitmap loadBitmap = LoadImage(imgPath, imgUrl);
            size[0] = loadBitmap.getWidth();
            size[1] = loadBitmap.getHeight();
            loadBitmap.recycle();
        }
        return size;
    }

    public static void writeFileToRootDir(Context context,String fileName,String str){
        FileWriter writer=null;
        try {
            File file = new File(FileUtils.getFileRootDir(context)
                    + "/"+fileName);
            if (file.exists()) {
                file.delete();
            }
            writer = new FileWriter(file, true);
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    /**
     * 将字符串写入到指定文件
     * @param content
     * @param targetPath
     */
    public static void writeFileTo(String content,String targetPath){
        FileWriter writer=null;
        try {
            File file = new File(targetPath);
            if (file.exists()) {
                file.delete();
            }
            writer = new FileWriter(file, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载文件
     * @param targetPath  存放目标
     * @param downloadUrl  下载路径
     */
    public static void downloadTxt(final String targetPath,final String downloadUrl){
        // 重新下载
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                BufferedReader buffer = null;
                URL url = null;
                String line = null;
                try {
                    url = new URL(downloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = buffer.readLine()) != null) {
                        sb.append(line);
                    }
                    File file=new File(targetPath);
                    FileWriter fWriter=new FileWriter(file,true);
                    fWriter.write(sb.toString());
                    fWriter.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}