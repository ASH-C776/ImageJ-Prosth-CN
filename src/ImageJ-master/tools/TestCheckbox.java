import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** 复现 AWT Checkbox 在高 DPI 下的方框/文字重叠问题。 */
public class TestCheckbox {
	public static void main(String[] a) throws Exception {
		final Frame f = new Frame("checkbox test");
		f.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(6, 20, 0, 0);
		f.add(new Checkbox("Default font checkbox"), c);
		Checkbox cb2 = new Checkbox("YaHei 12 checkbox");
		cb2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		f.add(cb2, c);
		Checkbox cb3 = new Checkbox("YaHei 16 checkbox pref200x40");
		cb3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		cb3.setPreferredSize(new Dimension(260, 40));
		f.add(cb3, c);
		Checkbox cb4 = new Checkbox("pref260x24");
		cb4.setPreferredSize(new Dimension(260, 24));
		f.add(cb4, c);
		Checkbox cb5 = new Checkbox("pref260x32 large font");
		cb5.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 20));
		cb5.setPreferredSize(new Dimension(300, 36));
		f.add(cb5, c);
		Label l = new Label("Label: Menu font size:");
		f.add(l, c);
		f.setSize(420, 260);
		f.setLocation(80, 80);
		f.setVisible(true);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2500);
					Rectangle r = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					BufferedImage img = new Robot().createScreenCapture(r);
					ImageIO.write(img, "png", new File("shot_cbtest.png"));
					System.out.println("SAVED " + img.getWidth() + "x" + img.getHeight());
				} catch (Throwable e) { System.out.println("FAIL " + e); }
				System.out.flush();
				Runtime.getRuntime().halt(0);
			}
		});
		t.setDaemon(true);
		t.start();
	}
}
