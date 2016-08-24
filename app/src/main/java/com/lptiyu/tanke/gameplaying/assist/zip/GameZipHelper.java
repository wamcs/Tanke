package com.lptiyu.tanke.gameplaying.assist.zip;

import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.ThemeLine;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.FileUtils;

import java.util.List;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class GameZipHelper {

    private GameZipScanner mGameZipScanner;
    private GameZipParser mGameZipParser;

    public GameZipHelper() {
        mGameZipScanner = new GameZipScanner();
        mGameZipParser = new GameZipParser();
    }

    /**
     * check、unzip、parse the zip file
     *
     * @param gameId
     */
    public boolean checkAndParseGameZip(long gameId) {
        //判断游戏包压缩文件是否存在
        if (mGameZipScanner.isZipFileExist(gameId) == GameZipScanner.ZIP_FILE_NOT_FOUND_VERSION) {
            Timber.e("zip file not found which gameId : %d", gameId);
            return false;
        }
        //根据游戏ID获取该游戏包解压后的文件的绝对路径
        String unzippedDir = mGameZipScanner.isZipFileUnzipped(gameId);
        if (unzippedDir == null) {
            //目录不存在，则需要根据游戏包的绝对路径手动解析.zip文件
            unzippedDir = FileUtils.unzipFile(mGameZipScanner.getGameZipFileAbsolutePath(gameId), DirUtils
                    .getTempDirectory().getAbsolutePath() + "/");
        }
        this.unZippedDir = unzippedDir;
        //解析解压后的文件夹，将每个章节下的文件解析成实体类对象
        return mGameZipParser.parseGameZip(unzippedDir);
    }

    public String unZippedDir;

    public ThemeLine getmThemeLine() {
        return mGameZipParser.getmThemeLine();
    }

    public List<Point> getmPoints() {
        return mGameZipParser.getmPoints();
    }

}
