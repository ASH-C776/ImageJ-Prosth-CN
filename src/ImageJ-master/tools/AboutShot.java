import ij.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

/** 单独截取 About ImageJ 窗口。 */
public class AboutShot {
	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		ij.setLocation(30, 30);
		IJ.wait(1000);
		new ij.plugin.AboutBox().run("");
		IJ.wait(1500);
		for (Frame f : Frame.getFrames()) {
			if (f.isVisible() && f.getTitle() != null && f.getTitle().contains("About")) {
				f.toFront();
				IJ.wait(500);
				ImageIO.write(new Robot().createScreenCapture(f.getBounds()), "png", new File("shot_about.png"));
				System.out.println("saved " + f.getTitle() + " " + f.getBounds());
			}
		}
		System.exit(0);
	}
}
