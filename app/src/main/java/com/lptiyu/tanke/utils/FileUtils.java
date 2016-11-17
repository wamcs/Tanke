package com.lptiyu.tanke.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.stream.JsonReader;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/11
 *
 * @author ldx
 */
public class FileUtils {

    static final int BUFFER = 2048;

    public static String tail(File file) throws IOException {
        return tail(file, 1);
    }

    public static String tail(File file, int lines) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        int line = 0;
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        length--;
        randomAccessFile.seek(length);
        char x = (char) randomAccessFile.read();
        if (x == '\n') {
            length--;
        }
        for (long seek = length; seek >= 0; --seek) {
            randomAccessFile.seek(seek);
            char c = (char) randomAccessFile.read();
            builder.append(c);
            if (c == '\n') {
                line++;
                if (line == lines) {
                    break;
                }
            }
        }
        return builder.reverse().toString();
    }

    //    /**
    //     * 解压.zip文件
    //     *
    //     * @param filePath
    //     * @return 解压后的游戏文件夹的绝对路径
    //     */
    //    //TODO : UTF-8 only, do not support GBK
    //    public static String unzipFile(String fileName, String filePath) {
    //        String dirPath = null;
    //        try {
    //            //            ZipFile zipFile = playing ZipFile(fileName, "GBK");
    //            ZipFile zipFile = new ZipFile(filePath, "GBK");
    //            Enumeration emu = zipFile.getEntries();
    //            dirPath = filePath.substring(0, filePath.length() - 4);
    //            while (emu.hasMoreElements()) {
    //                ZipEntry entry = (ZipEntry) emu.nextElement();
    //                if (entry.isDirectory()) {
    //                    new File(filePath + entry.getName()).mkdirs();
    //                    continue;
    //                }
    //                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
    //                File file = new File(dirPath + "/" + entry.getName());
    //                File parent = file.getParentFile();
    //                if (parent != null && (!parent.exists())) {
    //                    parent.mkdirs();
    //                }
    //                FileOutputStream fos = new FileOutputStream(file);
    //                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
    //
    //                int count;
    //                byte data[] = new byte[BUFFER];
    //                while ((count = bis.read(data, 0, BUFFER)) != -1) {
    //                    bos.write(data, 0, count);
    //                }
    //                bos.flush();
    //                bos.close();
    //                bis.close();
    //            }
    //            zipFile.close();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return dirPath;
    //    }
    //
    //    /**
    //     * 解压.zip文件
    //     *
    //     * @param filePath
    //     * @return 解压后的游戏文件夹的绝对路径
    //     */
    //    //TODO : UTF-8 only, do not support GBK
    //    public static String unzipFile(String filePath) {
    //        String dirPath = null;
    //        try {
    //            ZipFile zipFile = new ZipFile(filePath, "GBK");
    //            Enumeration emu = zipFile.getEntries();
    //            String rootPath = new File(filePath).getParent();
    //            while (emu.hasMoreElements()) {
    //                ZipEntry entry = (ZipEntry) emu.nextElement();
    //                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
    //                File file = new File(rootPath + "/" + entry.getName());
    //                File parent = file.getParentFile();
    //                if (parent != null && (!parent.exists())) {
    //                    parent.mkdirs();
    //                }
    //                FileOutputStream fos = new FileOutputStream(file);
    //                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
    //
    //                int count;
    //                byte data[] = new byte[BUFFER];
    //                while ((count = bis.read(data, 0, BUFFER)) != -1) {
    //                    bos.write(data, 0, count);
    //                }
    //                bos.flush();
    //                bos.close();
    //                bis.close();
    //            }
    //            zipFile.close();
    //            dirPath = filePath.substring(0, filePath.length() - 4);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return dirPath;
    //    }

    public static String readFileByLine(String filePath) {
        return readFileByLine(new File(filePath));
    }

    public static String readFileByLine(File file) {
        StringBuilder resultBuilder = new StringBuilder("");
        String encoding = "utf-8";
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    resultBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultBuilder.toString();
    }

    public static <T> T parseJsonFile(String filePath, Class<T> clazz) {
        File file = new File(filePath);
        return parseJsonFile(file, clazz);
    }

    /**
     * 解析json文件为class对象
     *
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseJsonFile(File file, Class<T> clazz) {
        T result = null;
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            result = AppData.globalGson().fromJson(new JsonReader(isr), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // 保存文件
    public static void saveFile(String newsRootPath, String filename,
                                File picFile) {
        try {
            File newsFileRoot = new File(newsRootPath);
            if (!newsFileRoot.exists()) {
                newsFileRoot.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(newsRootPath + filename);
            FileInputStream fis = new FileInputStream(picFile);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // 删除文件
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    // 删除文件与目录
    public static boolean deleteFolder(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    // 删除目录
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get file size 通过URL地址获取文件大小
     *
     * @param fileUrl * url file path
     * @return filesize
     * @throws MalformedURLException
     */
    public static double getFileSizeByUrl(String fileUrl) {
        HttpURLConnection urlcon = null;
        try {
            if (!fileUrl.startsWith("http://"))
                fileUrl = "http://" + fileUrl;
            URL url = new URL(fileUrl);
            urlcon = (HttpURLConnection) url.openConnection();
            double filesize = urlcon.getContentLength();
            return filesize;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlcon.disconnect();
        }
        return 0;
    }

    public static HashMap<String, String> getAPKInfo(Context context, String apkFilePath) {
        if (apkFilePath == null || context == null || !apkFilePath.endsWith(".apk")) {
            return null;
        }
        //        String archiveFilePath = this.getExternalFilesDir(null).getAbsolutePath() + "/budao.apk";//安装包路径
        //        String archiveFilePath = apkFilePath;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            HashMap<String, String> hashMap = new HashMap<>();
            ApplicationInfo appInfo = info.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;  //得到安装包名称
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            //            Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
            hashMap.put(Conf.APP_NAME, appName);
            hashMap.put(Conf.PACKAGE_NAME, packageName);
            hashMap.put(Conf.VERSION_NAME, versionName);
            hashMap.put(Conf.VERSION_CODE, versionCode + "");
            return hashMap;
        }
        return null;
    }

    public static File isLocalAPKFileExist(int targetVersionCode) {
        File[] files = DirUtils.getAPKDirectory().listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                HashMap<String, String> map = getAPKInfo(AppData.getContext(), file.getAbsolutePath());
                if (map != null) {
                    String packageName = map.get(Conf.PACKAGE_NAME);
                    String versionCode = map.get(Conf.VERSION_CODE);
                    if (packageName.equals(AppData.getPackageName()) && targetVersionCode == Integer.parseInt
                            (versionCode)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

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

    public static String getFileNameFromURL(String fileUrl) {
        if (fileUrl == null)
            return null;
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
