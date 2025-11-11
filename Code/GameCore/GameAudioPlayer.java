package Code.GameCore;

import javazoom.jl.player.Player;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameAudioPlayer {
    private static final ExecutorService executor= Executors.newCachedThreadPool();
    public static Player bgmPlayer;
    public static boolean isBgmPlaying = false;
    public static String menuBgmPath="src/resources/music/MainMenu.mp3";;
    public static String topScoresBgmName="src/resources/music/TopScores.mp3";
    public static String gameStartBgmPath="src/resources/music/gs_start.mp3";
    public static String defaultBgmPath="src/resources/music/gs_siren_soft.mp3";
    public static String eatDotSoundPath="src/resources/music/gs_chomp.mp3";
    public static String eatFruitSoundPath="src/resources/music/gs_eatfruit.mp3";
    public static String PowerUpSoundPath="src/resources/music/gs_ghostblue.mp3";
    public static String deathSoundPath="src/resources/music/gs_pacmandies.mp3";

    public static void stopBgm() {
        isBgmPlaying = false;
        if (bgmPlayer != null) {
            bgmPlayer.close();
            bgmPlayer = null;
        }
    }

    // 播放默认游戏背景音乐（循环）
    public static void playDefaultBgm(String bgmPath) {
        stopBgm(); // 先停止当前背景音乐
        isBgmPlaying = true;
        executor.execute(() -> {
            try {
                while (isBgmPlaying) {
                    InputStream fis = new FileInputStream(bgmPath);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bgmPlayer = new Player(bis);
                    bgmPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 播放主菜单背景音乐（循环）
    public static void playMenuBgm(String path) {
        stopBgm(); // 先停止当前背景音乐
        isBgmPlaying = true;
        executor.execute(() -> {
            try {
                while (isBgmPlaying) {
                    InputStream fis = new FileInputStream(path);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bgmPlayer = new Player(bis);
                    bgmPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    // 播放短音效（短音效，不循环）

    public static void playShortSound(String path) {
        executor.execute(() -> {
            try {
                InputStream fis = new FileInputStream(path);
                BufferedInputStream bis = new BufferedInputStream(fis);
                Player soundPlayer = new Player(bis);
                soundPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void playLongSound(String path, int durationMs) {
        executor.execute(() -> {
            try {
                InputStream fis = new FileInputStream(path);
                BufferedInputStream bis = new BufferedInputStream(fis);
                Player soundPlayer = new Player(bis);

                // 启动音效播放
                soundPlayer.play();

                // 持续播放指定时间
                Thread.sleep(durationMs);

                // 停止播放
                soundPlayer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

