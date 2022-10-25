import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

public class RageCam {

    static final Integer SENSITIVITY    = 250 ;
    static final Integer POLLRATE       = 1500;
    static String settingLocation = new SettingsManager().getSettings()[0]+(System.getProperty("os.name").startsWith("Windows") ? "\\" : "/");
    static FrameGrabber webcam = new OpenCVFrameGrabber(0);;
    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

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
        Frame frame;
        try {
            frame = webcam.grab();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        IplImage img = converter.convert(frame);
        saveCapture(img);

//        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
//        Frame _frame = converter.convert(img);
//        BufferedImage bimage = paintConverter.getBufferedImage(frame,1);
//        System.out.println(bimage);
    }

    static void saveCapture(IplImage image){
        String savePath = settingLocation + new Date().toString().replace(':', '-') + ".jpg";
        opencv_imgcodecs.cvSaveImage(savePath, image);
        System.out.println("Saved at: "+ savePath);
    }

    public static void main(String[] args) {
       // check for images folder
        File imagesPath = new File(System.getProperty("user.home")+ "\\RageCam\\");
        if (!imagesPath.exists()){
            imagesPath.mkdirs();
        }
        try {
            webcam.start();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){checkScreenshot();}
        },0,POLLRATE);
    }
}
