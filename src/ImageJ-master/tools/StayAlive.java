import ij.*;

/** 启动 ImageJ 并常驻不退出，供外部脚本做窗口级操作。 */
public class StayAlive {
	public static void main(String[] a) throws Exception {
		ImageJ ij = new ImageJ(ImageJ.NORMAL);
		System.out.println("ready");
		System.out.flush();
		Thread.sleep(600000);
	}
}
