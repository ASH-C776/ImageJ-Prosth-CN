import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** 离屏渲染测试：不同字号下 AWT Checkbox 的方框是否完整。 */
public class OffscreenCb {
	public static void main(String[] a) throws Exception {
		Dialog d = new Dialog((Frame)null, "cb", false);
		d.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(6, 20, 0, 20);
		int[] sizes = {12, 16, 20, 26};
		Checkbox[] boxes = new Checkbox[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			boxes[i] = new Checkbox("Interpolate zoomed images " + sizes[i] + "pt");
			boxes[i].setFont(new Font("Microsoft YaHei UI", Font.PLAIN, sizes[i]));
			d.add(boxes[i], c);
		}
		d.pack();
		d.setLocation(-6000, -6000);
		d.setVisible(true);
		Thread.sleep(800);

		BufferedImage img = new BufferedImage(Math.max(300, d.getWidth()), Math.max(100, d.getHeight()), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.WHITE); g.fillRect(0, 0, img.getWidth(), img.getHeight());
		d.printAll(g);
		g.dispose();
		ImageIO.write(img, "png", new File("shot_offscreencb.png"));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < boxes.length; i++) {
			sb.append("cb").append(sizes[i]).append(": size=").append(boxes[i].getSize())
			  .append(" pref=").append(boxes[i].getPreferredSize())
			  .append(" font=").append(boxes[i].getFont().getSize()).append("\n");
		}
		System.out.println(sb);
		d.dispose();
		System.exit(0);
	}
}
