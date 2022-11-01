import java.awt.*;
import java.awt.image.BufferedImage;

public class GameSettings {

    public static boolean GGST(BufferedImage screenshot, int SENSITIVITY){
        for (int y=436; y<=607; y++) {
            int p = screenshot.getRGB(979,y);
            for (int b=16;b>=0; b-=8) {
                if(((p >> b) & 0xff) < SENSITIVITY) return false;
            }
        }
        return true;
    }
}
