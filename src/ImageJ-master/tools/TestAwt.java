import java.awt.*;
public class TestAwt {
	public static void main(String[] a) throws Exception {
		System.out.println("headless=" + GraphicsEnvironment.isHeadless());
		Frame f = new Frame("t");
		f.setSize(200, 100);
		f.setVisible(true);
		System.out.println("visible ok " + f.getSize());
		f.dispose();
		System.exit(0);
	}
}
