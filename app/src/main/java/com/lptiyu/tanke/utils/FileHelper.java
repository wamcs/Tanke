package com.lptiyu.tanke.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jason on 2016/11/10.
 */

public class FileHelper {
    /**
     * 传入文件名以及字符串, 将字符串信息保存到文件中
     *
     * @param fileName
     * @param content
     */
    public static void textToFile(String fileName, String content) {
        try {
            // 创建文件对象
            File fileText = new File(fileName);
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(fileText);
            // 写文件
            fileWriter.write(content);
            // 关闭
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
