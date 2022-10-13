import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class RageCam {

    static final Integer SENSITIVITY = 250;


    public static BufferedImage screenshot(){
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "Unable to capture screen.", "Error", JOptionPane.WARNING_MESSAGE);
            // System.exit(1);
        }
        return robot.createScreenCapture(new Rectangle(1920, 1080));
    }

    public static void checkScreenshot() {
        BufferedImage screenshot = screenshot();
        Boolean flag = true;
        for (int y=436; y<=607; y++) {
            int p = screenshot.getRGB(979,y);
            for (int b=16;b>=0; b-=8) {
                if(((p >> b) & 0xff) < SENSITIVITY) return;
            }

        }
        performCapture();
    }

    private static void performCapture() {
        Toolkit.getDefaultToolkit().beep();
        System.out.println("trigger: " + new Date());
    }


    public static void main(String[] args) {
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){checkScreenshot();}
        },0,1500);
    }
}
