package com.adsale.ChinaPlas.utils;

import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by Carrie on 2017/9/15.
 */

public class Parser {

    /**
     * 解析json泛型方法,传入json文件路径
     * !!! App.rootDir目录下的
     *
     * @param T        实体类
     * @param filePath 相对路径 e.g.: "MainIcon/xxx.png"
     * @return T
     */
    public static <T> T parseJsonRootDirFile(Class<T> T, String filePath) {
        return parseJson(T, FileUtil.readRootDirFile(filePath));
    }

    /**
     * 会报错：java.lang.ClassCastException: com.google.gson.internal.LinkedTreeMap cannot be cast to com.adsale.ChinaPlas.data.model.DocumentsCenter
     * 解析files目录下的ArrayListJson
     *
     * @param filePath rootDir或asset /files目录下文件  e.g.: "PDFCenterInfos.txt"
     * @param <T>      实体类 e.g.: {@link DocumentsCenter}
     * @return ArrayList<T>
     */
    public static <T> ArrayList<T> parseArrayListJson(String filePath) {
        Type listType = new TypeToken<ArrayList<T>>() {
        }.getType();
        return new Gson().fromJson(FileUtil.readFilesDirFile(filePath), listType);
    }

    /**
     * 文件在 getFilesDir目录下， App.filesDir
     *
     * @param fileName 相对路径 e.g.: "advertisement.txt" || "appContents.txt"
     */
    public static <T> T parseJsonFilesDirFile(Class<T> T, String fileName) {
        return parseJson(T, FileUtil.readFilesDirFile(fileName));
    }

    /**
     * 解析json泛型方法 直接传入json
     *
     * @param T
     * @param json String类型的json内容
     * @param <T>
     * @return
     */
    public static <T> T parseJson(Class<T> T, String json) {
        return new Gson().fromJson(json, T);
    }




}
