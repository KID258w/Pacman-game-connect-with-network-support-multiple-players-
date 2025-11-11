package Code.GameCore;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Base64;

public class Map{

    private final Image lemon=new ImageIcon("src/resources/images/lemon.png").getImage();
    private final Image cherry=new ImageIcon("src/resources/images/cherry.png").getImage();
    private final Image peach=new ImageIcon("src/resources/images/peach.png").getImage();
    private final Image pill=new ImageIcon("src/resources/images/pill.png").getImage();
    private final int BLOCK_SIZE=24;                      //每个方块的像素点大小
    private final int N_BLOCKS=15;                        //每行或每列的方块个数
    private final int SCREEN_SIZE=N_BLOCKS*BLOCK_SIZE;    //单列或单行的像素

    private short[] screenData;                                    //游戏地图
    private final short[]levelData={                         //地图数据
       19, 26, 26, 26, 18, 18, 18, 26, 18, 18, 18, 26, 26, 26,22,
       21, 0  ,0  ,0 , 17, 16, 28, 0 , 25, 16, 20, 0 , 0 , 0 ,21,
       21, 0,  0,  0,  17, 20, 0 , 0 , 0 , 17, 20, 0 , 0 , 0 ,21,
       257, 26, 26, 26, 16, 16, 18, 34, 18, 16, 16, 26, 26, 26,20,
       21, 0 , 0 , 0,  17, 24, 24, 16, 24, 24, 20, 0 , 0 , 0 ,21,
       17, 18, 26, 18, 20, 0,  0,  21, 0,  0 , 17, 18, 26, 18,20,
       17, 28, 0  ,17, 20, 0, 19,  16, 22, 0,  17, 20, 0, 25, 20,
       21, 0,  0,  17, 16, 266,16, 128, 16, 266,16, 20, 0, 0,  21,
       17, 22, 0,  17, 20, 0, 25,  16 , 28, 0, 17, 20, 0, 19, 20,
       17, 24, 26, 24, 20, 0, 0,   21,  0,  0, 17, 24, 26, 24,20,
       21, 0  ,0,  0,  17, 18, 18, 16, 18, 18, 20, 0,  0 , 0, 21,
       17, 26, 26, 26, 16, 16, 24, 72, 24, 16, 16, 26, 26, 26,260,
       21, 0,  0,  0,  17, 20, 0,  0,  0 ,17,  20, 0,  0,  0, 21,
       21, 0,  0,  0, 17,  16, 22, 0, 19, 16, 20,  0, 0 ,0 ,21,
       25, 26, 26, 26, 24, 24, 24, 26, 24, 24, 24, 26,26,26,28
    };

private final Color mazeColor=new Color(5,100,5);
private final Color dotColor=new Color(192,192,0);

    public void setScreenData(short[] screenData) {
        this.screenData = screenData;
    }

    public int getSCREEN_SIZE(){
       return SCREEN_SIZE;
   }

    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }

    public Map(){
    screenData=new short[N_BLOCKS*BLOCK_SIZE];
    int i;
    for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {    //填入游戏地图数据
        screenData[i] = levelData[i];
    }
    }

    public void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {    //双重循环遍历15*15的游戏地图
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {      //绘制左墙
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {      //绘制上墙
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {       //绘制右墙
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {        //绘制下墙
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {       //绘制豆子
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                if ((screenData[i] & 32) != 0) {       //绘制柠檬
                    g2d.drawImage(lemon, x+1, y+1, null);
                }

                if((screenData[i] & 64) != 0){         //绘制樱桃
                    g2d.drawImage(cherry, x-1, y-2, null);
                }

                if((screenData[i] & 128) != 0){        //绘制桃子
                    g2d.drawImage(peach, x+1, y+1, null);
                }

                if((screenData[i] & 256) != 0){        //绘制药丸
                    g2d.drawImage(pill, x+8, y+8, null);
                }

                i++;
            }
        }
    }

    public boolean isWall(int x,int y,int direction) {    //判断墙壁
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        return ((screenData[pos]&(1<<direction))!=0);
    }

    public boolean eatDot(int x, int y) {          //判断吃豆子，修改地图数据
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        if((screenData[pos]&16)!=0){
            screenData[pos]=(short)(screenData[pos]&15);
            return true;
        }
        return false;
    }

    public boolean eatLemon(int x,int y){       //判断吃柠檬
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        if((screenData[pos]&32)!=0){
            screenData[pos]=(short)(screenData[pos]&31);
            return true;
        }
        return false;
    }

    public boolean eatCherry(int x,int y){       //判断吃樱桃
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        if((screenData[pos]&64)!=0){
            screenData[pos]=(short)(screenData[pos]&63);
            return true;
        }
        return false;
    }

    public boolean eatPeach(int x,int y){       //判断吃桃子
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        if((screenData[pos]&128)!=0){
            screenData[pos]=(short)(screenData[pos]&127);
            return true;
        }
        return false;
    }

    public boolean eatPill(int x,int y){       //判断吃药丸
        int pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
        if((screenData[pos]&256)!=0){
            screenData[pos]=(short)(screenData[pos]&255);
            return true;
        }
        return false;
    }

    public boolean isLevelCompleted() {        //判断游戏是否结束
        for (short cell : screenData) {
            if ((cell & 16) != 0) return false;
        }
        return true;
    }

    // 服务器端：使用Base64压缩
    public String serializeMap() {
        ByteBuffer buffer = ByteBuffer.allocate(screenData.length * 2);
        for (short value : screenData) {
            buffer.putShort(value);
        }
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    // 客户端：解压
    public static short[] deserializeMap(String base64Str) {
        byte[] bytes = Base64.getDecoder().decode(base64Str);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short[] data = new short[bytes.length / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = buffer.getShort();
        }
        return data;
    }

}



