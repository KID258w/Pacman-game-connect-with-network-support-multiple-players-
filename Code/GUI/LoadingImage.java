package Code.GUI;

import javax.swing.*;
import java.awt.*;

public class LoadingImage {
    private final static Image image=new ImageIcon("src/resources/images/LoadingImage.jpeg").getImage();

    public static void showLogo() {
        JFrame logoframe = new JFrame();
        logoframe.setUndecorated(true); // 无边框
        logoframe.setAlwaysOnTop(true); // 保持在最前
        logoframe.setSize(1120, 750);
        try {
            JPanel logoPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            };

            logoframe.add(logoPanel);
            logoframe.setLocationRelativeTo(null); // 居中显示
            logoframe.setVisible(true);
            // 4秒后自动关闭
            Timer timer=new Timer(4000, e -> {
                logoframe.dispose();
            });
            timer.start();
            timer.setRepeats(false);

        } catch (Exception e) {
            System.err.println("无法加载Logo图片: " + e.getMessage());

        }
    }

}
