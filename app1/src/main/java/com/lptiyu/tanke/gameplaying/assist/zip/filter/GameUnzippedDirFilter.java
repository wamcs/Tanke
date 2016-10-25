package com.lptiyu.tanke.gameplaying.assist.zip.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xiaoxiaoda
 * date: 16-5-24
 * email: wonderfulifeel@gmail.com
 */

/**
 * this class is to filter the file
 * which is unzipped from the game zip
 * now it is a dir
 * 筛选出解压后的游戏文件
 */
public class GameUnzippedDirFilter implements FilenameFilter {

    static Pattern gameUpzippedPattern = Pattern.compile("[0-9]+_[0-9]+_[0-9]");

    @Override
    public boolean accept(File dir, String filename) {
        Matcher matcher = gameUpzippedPattern.matcher(filename);
        return matcher.matches();
    }
}
