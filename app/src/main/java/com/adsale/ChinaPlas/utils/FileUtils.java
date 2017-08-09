package com.adsale.ChinaPlas.utils;

import android.content.Context;
import android.os.Environment;

//import com.sina.org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * *****************************************
 *
 * @author 廖乃波
 * @文件名称 : FileUtils.java
 * @创建时间 : 2013-1-27 下午02:35:09
 * @文件描述 : 文件工具类
 * *****************************************
 */
public class FileUtils implements FilenameFilter {
    private static final String TAG = "FileUtils";



    public static void main(String[] args) throws Exception {
        //递归显示C盘下所有文件夹及其中文件
        File root = new File("c:");
        showAllFiles(root);
    }

    /**
     * 递归显示文件夹下所有文件
     *
     * @param dir
     * @throws Exception
     */
    public static void showAllFiles(File dir) throws Exception {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            System.out.println(fs[i].getAbsolutePath());
            if (fs[i].isDirectory()) {
                try {
                    showAllFiles(fs[i]);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 文件夹是否存在，不存在则新建
     *
     * @param dirName
     */
    public static void createDir(String dirName) {
        //	String path= App.mRootDir+
    }

    private static String FILE_PATH;

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
     * 全路径
     *
     * @param path
     * @return
     */
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
//            str = EncodingUtils.getString(temp, "utf-8");
            LogUtil.i(TAG, "str=" + str);
            in.close();
        } catch (IOException e) {

        }
        return str;

    }

    /**
     * 获取地址最后的文件名
     *
     * @param str
     * @return abc.png
     */
    public static String getSubFullName(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    /**
     * 获取 abc.png 中的 abc
     *
     * @param str
     * @return
     */
    public static String getSubName(String str, char c) {
        return str.substring(0, str.length() - str.substring(str.lastIndexOf(c)).length());
    }


    @Override
    public boolean accept(File dir, String filename) {
        return filename.endsWith(".png");
    }

//	public static void getFileList(ArrayList<Emoji> emojis, String emojiPath){
//		Emoji emoji=null;
//		File file=new File(emojiPath);
//		FileUtils fileFilter=new FileUtils();
//		String[] files=file.list(fileFilter);
//		LogUtil.i(TAG,"*****files="+files.toString());
////		for(int i=0;i<files.length;i++){
////			emoji=new Emoji();
////			emoji.setFaceName(emojiPath+);
////		}
//	}



    /**
     * 读取sd卡上指定后缀的所有文件
     *
     * @param files    返回的所有文件
     * @param filePath 路径(可传入sd卡路径)
     * @param suffere  后缀名称 比如 .gif
     * @return
     */
    public static ArrayList<File> getSuffixFile(ArrayList<File> files, String filePath, String suffere) {
        LogUtil.i(TAG, "filePath=" + filePath);
        File f = new File(filePath);

        if (!f.exists() || !f.isDirectory()) {
            return null;
        }

        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if (subFile.isFile() && subFile.getName().endsWith(suffere)) {
                files.add(subFile);
            } else if (subFile.isDirectory()) {
                getSuffixFile(files, subFile.getAbsolutePath(), suffere);
            } else {
                //非指定目录文件 不做处理
            }

        }

        return files;
    }


}
