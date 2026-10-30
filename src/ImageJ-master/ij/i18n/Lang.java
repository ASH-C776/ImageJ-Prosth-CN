package ij.i18n;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * ImageJ 中文界面支持（显示层汉化）。
 * <p>
 * 设计原则：中文只用于 "显示"，所有内部命令名、菜单路径、插件表键值始终使用英文，
 * 因此 macro 的 run("Open...")、插件文件名匹配、快捷键表、结果表列名等均不受影响。
 * <p>
 * 词条文件：ij/i18n/IJ_Lang_zh_CN.txt（Tab 分隔），可通过在 ImageJ 目录放置同名文件覆盖。
 */
public class Lang {

	public static final String LOCALE_FILE = "IJ_Lang_zh_CN.txt";

	private static final Map<String, String> exact = new HashMap<String, String>();
	private static final Map<String, String> norm = new HashMap<String, String>();
	private static final Map<String, String> reverse = new HashMap<String, String>();
	private static boolean enabled = true;
	private static boolean loaded = false;
	private static String uiFontName;

	private Lang() {}

	private static synchronized void load() {
		if (loaded) return;
		loaded = true;
		InputStream in = null;
		try {
			in = Lang.class.getResourceAsStream("/ij/i18n/" + LOCALE_FILE);
		} catch (Throwable t) {}
		if (in != null) parse(in, "jar");
		// 外部覆盖文件：ImageJ 工作目录
		try {
			String dir = System.getProperty("user.dir");
			if (dir != null) {
				File f = new File(dir, LOCALE_FILE);
				if (!f.exists()) f = new File(dir, "ij" + File.separator + "i18n" + File.separator + LOCALE_FILE);
				if (f.exists()) parse(new FileInputStream(f), "file");
			}
		} catch (Throwable t) {}
	}

	private static void parse(InputStream in, String source) {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			while ((line = r.readLine()) != null) {
				if (line.length() == 0 || line.charAt(0) == '#') continue;
				int tab = line.indexOf('\t');
				if (tab <= 0) continue;
				String k = line.substring(0, tab);
				String v = line.substring(tab + 1);
				if (v.trim().length() == 0) continue;
				exact.put(k, v);
				String nk = normalize(k);
				if (nk.length() > 0 && !norm.containsKey(nk)) norm.put(nk, v);
				reverse.put(v.trim(), k.trim());
			}
		} catch (Throwable t) {
			// 语言包解析失败时静默退回英文界面
		} finally {
			if (r != null) try { r.close(); } catch (IOException e) {}
		}
	}

	/** 归一化 key：去首尾空白、压缩空格、去掉结尾省略号、转小写。 */
	private static String normalize(String s) {
		if (s == null) return "";
		s = s.trim();
		while (s.endsWith("...")) s = s.substring(0, s.length() - 3);
		if (s.endsWith("\u2026")) s = s.substring(0, s.length() - 1);
		StringBuilder sb = new StringBuilder();
		boolean space = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == ' ' || c == '\t') { space = true; continue; }
			if (space && sb.length() > 0) sb.append(' ');
			space = false;
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString().trim();
	}

	/** 去掉菜单项末尾的快捷键标记，如 "Main Window [enter]" -> "Main Window"。 */
	private static String stripShortcut(String s) {
		int i = s.lastIndexOf(" [");
		if (i > 0 && s.endsWith("]")) return s.substring(0, i);
		return s;
	}

	/** 英->中。找不到词条时原样返回。 */
	public static String tr(String s) {
		if (!enabled || s == null || s.length() == 0) return s;
		load();
		String v = exact.get(s);
		if (v != null) return v;
		String base = stripShortcut(s);
		if (!base.equals(s)) {
			v = exact.get(base);
			if (v != null) return v + s.substring(base.length());
		}
		v = norm.get(normalize(s));
		if (v != null) return v;
		if (!base.equals(s)) {
			v = norm.get(normalize(base));
			if (v != null) return v + s.substring(base.length());
		}
		// 形如 "Undo  Ctrl+Z" 的菜单项：翻译前半，保留右侧快捷键列
		int dbl = s.indexOf("  ");
		while (dbl > 0) {
			String head = s.substring(0, dbl);
			v = exact.get(head);
			if (v == null) v = norm.get(normalize(head));
			if (v != null) return v + s.substring(dbl);
			dbl = s.indexOf("  ", dbl + 2);
		}
		// 多行提示：逐行翻译后拼回（词典不支持换行）
		if (s.indexOf('\n') >= 0) {
			String[] parts = s.split("\n", -1);
			boolean any = false;
			for (int i = 0; i < parts.length; i++) {
				String t = tr(parts[i]);
				if (!t.equals(parts[i])) any = true;
				parts[i] = t;
			}
			if (any) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < parts.length; i++) {
					if (i > 0) sb.append('\n');
					sb.append(parts[i]);
				}
				return sb.toString();
			}
		}
		// 动态拼接的标题，如 "Histogram of cameraman"：翻译最长匹配前缀，保留后半变量
		for (int i = s.length(); i >= 4; i--) {
			if (i < s.length() && Character.isLetterOrDigit(s.charAt(i))
					&& Character.isLetterOrDigit(s.charAt(i - 1))) continue;  // 只从词边界切开
			String head = s.substring(0, i);
			if (head.length() != head.trim().length() && i < s.length()) head = head.trim();
			String hv = exact.get(head);
			if (hv == null) hv = norm.get(normalize(head));
			if (hv != null) return hv + s.substring(i);
		}
		return s;
	}

	/** 中->英（用于标签被翻译后仍需用英文做逻辑判断的地方）。 */
	public static String en(String s) {
		if (s == null || s.length() == 0) return s;
		load();
		String v = reverse.get(s.trim());
		if (v != null) return v;
		return s;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean b) {
		enabled = b;
	}

	/** 翻译单个菜单项：只改显示文本，action command 保持英文。 */
	public static void labelize(MenuItem item) {
		if (!enabled || item == null) return;
		if (item instanceof Menu) { translateMenu((Menu) item); return; }
		String en = item.getLabel();
		if (en == null || en.equals("-") || en.length() == 0) return;
		String zh = tr(en);
		if (zh == null || zh.equals(en)) return;
		String ac = item.getActionCommand();
		item.setLabel(zh);
		item.setActionCommand(ac != null ? ac : en);
	}

	/** 递归翻译子菜单及其条目（幂等，可重复调用）。 */
	public static void translateMenu(Menu menu) {
		if (!enabled || menu == null) return;
		String en = menu.getLabel();
		if (en != null && en.length() > 0 && !en.equals("-")) {
			String zh = tr(en);
			if (zh != null && !zh.equals(en)) menu.setLabel(zh);
		}
		int n = menu.getItemCount();
		for (int i = 0; i < n; i++) {
			MenuItem item = menu.getItem(i);
			if (item == null) continue;
			if (item instanceof Menu) translateMenu((Menu) item);
			else labelize(item);
		}
	}

	/** 翻译整个菜单栏。 */
	public static void translateMenuBar(MenuBar mb) {
		if (!enabled || mb == null) return;
		int n = mb.getMenuCount();
		for (int i = 0; i < n; i++) {
			Menu m = mb.getMenu(i);
			if (m != null) translateMenu(m);
		}
	}

	// ---- Swing 菜单（ij.menu 包）版本：主菜单栏改用 Swing 后走这里 ----

	/** 翻译单个 Swing 菜单项（含复选菜单项），只改显示文本，action command 保持英文。 */
	public static void labelize(javax.swing.JMenuItem item) {
		if (!enabled || item == null) return;
		if (item instanceof ij.menu.Menu) { translateMenu((ij.menu.Menu) item); return; }
		String en = item.getText();
		if (en == null || en.equals("-") || en.length() == 0) return;
		String zh = tr(en);
		if (zh == null || zh.equals(en)) return;
		String ac = item.getActionCommand();
		item.setText(zh);
		item.setActionCommand(ac != null ? ac : en);
	}

	/** 递归翻译 Swing 子菜单及其条目（幂等，可重复调用）。 */
	public static void translateMenu(ij.menu.Menu menu) {
		if (!enabled || menu == null) return;
		String en = menu.getText();
		if (en != null && en.length() > 0 && !en.equals("-")) {
			String zh = tr(en);
			if (zh != null && !zh.equals(en)) menu.setText(zh);
		}
		int n = menu.getItemCount();
		for (int i = 0; i < n; i++) {
			javax.swing.JMenuItem item = menu.getItem(i);
			if (item == null) continue;
			if (item instanceof ij.menu.Menu) translateMenu((ij.menu.Menu) item);
			else labelize(item);
		}
	}

	/** 翻译整个 Swing 菜单栏。 */
	public static void translateMenuBar(ij.menu.MenuBar mb) {
		if (!enabled || mb == null) return;
		int n = mb.getMenuCount();
		for (int i = 0; i < n; i++) {
			ij.menu.Menu m = mb.getMenu(i);
			if (m != null) translateMenu(m);
		}
	}

	/** 将整个 Swing 菜单栏还原为英文显示（切换到英文界面时调用）。 */
	public static void restoreMenuBar(ij.menu.MenuBar mb) {
		if (mb == null) return;
		int n = mb.getMenuCount();
		for (int i = 0; i < n; i++) {
			ij.menu.Menu m = mb.getMenu(i);
			if (m != null) restoreMenu(m);
		}
	}

	/** 递归还原 Swing 子菜单及其条目的英文文本（幂等，可重复调用）。 */
	public static void restoreMenu(ij.menu.Menu menu) {
		if (menu == null) return;
		restoreText(menu);
		int n = menu.getItemCount();
		for (int i = 0; i < n; i++) {
			javax.swing.JMenuItem item = menu.getItem(i);
			if (item == null) continue;
			if (item instanceof ij.menu.Menu) restoreMenu((ij.menu.Menu) item);
			else restoreText(item);
		}
	}

	/** 还原单个菜单项文本：优先取 actionCommand 里的英文原文，否则用 en() 反查。 */
	private static void restoreText(javax.swing.JMenuItem item) {
		String txt = item.getText();
		if (txt == null || txt.length() == 0 || txt.equals("-")) return;
		String ac = item.getActionCommand();
		if (ac != null && ac.length() > 0 && !ac.equals(txt)) { item.setText(ac); return; }
		String en = en(txt);
		if (en != null && !en.equals(txt)) item.setText(en);
	}

	/** 优先使用的中文界面字体；无合适字体时返回 null。 */
	public static String uiFontName() {
		if (uiFontName != null) return uiFontName.length() == 0 ? null : uiFontName;
		uiFontName = "";
		if (System.getProperty("os.name", "").startsWith("Windows")) {
			try {
				String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
				String[] prefer = {"Microsoft YaHei UI", "Microsoft YaHei", "微软雅黑", "SimHei", "Segoe UI"};
				for (int p = 0; p < prefer.length; p++) {
					for (int i = 0; i < names.length; i++) {
						if (prefer[p].equalsIgnoreCase(names[i])) { uiFontName = prefer[p]; return uiFontName; }
					}
				}
			} catch (Throwable t) {}
		}
		return null;
	}

	/** 返回指定字号的界面字体（无法确定时退回 SansSerif）。 */
	public static Font uiFont(int style, int size) {
		String name = uiFontName();
		return new Font(name != null ? name : "SansSerif", style, size);
	}

	/** 词条数量，便于自检。 */
	public static int size() {
		load();
		return exact.size();
	}
}
