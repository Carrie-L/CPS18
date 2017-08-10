package com.adsale.ChinaPlas.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Carrie on 2017/8/10.
 */

public class FileUtil {

    public static boolean saveToMemory(String dir,String fileName, byte[] bytes){
        File file = new File(dir,fileName);
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










}
