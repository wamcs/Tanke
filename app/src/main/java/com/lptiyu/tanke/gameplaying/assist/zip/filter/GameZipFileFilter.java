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
 * which is match with the game zip file name rules
 * 筛选游戏压缩包文件
 */
public class GameZipFileFilter implements FilenameFilter {

    /**
     * 文件名格式：37_31_12
     */
    static Pattern gameZipPattern = Pattern.compile("[0-9]+_[0-9]+_[0-9].zip$");

    /**
     * 文件过滤器，返回true的文件则合格
     *
     * @param dir      文件的当前目录
     * @param filename 文件名称
     * @return
     */
    @Override
    public boolean accept(File dir, String filename) {
        Matcher matcher = gameZipPattern.matcher(filename);
        return matcher.matches();
    }

}
