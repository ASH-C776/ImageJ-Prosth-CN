package ij.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Swing 版菜单项，API 与 java.awt.MenuItem 兼容（getLabel/setLabel/getState 等），
 * 这样原有代码基本不用改。行高由 Swing 自绘，随字号自适应，不会像原生菜单那样挤压。
 */
public class MenuItem extends JMenuItem {

	public MenuItem() {
		super();
		init();
	}

	public MenuItem(String label) {
		super(label);
		init();
	}

	public MenuItem(String label, MenuShortcut ms) {
		super(label);
		init();
		if (ms != null) setAccelerator(toKeyStroke(ms));
	}

	public MenuItem(String label, int shortcut) {
		super(label);
		init();
		if (shortcut != 0) setAccelerator(KeyStroke.getKeyStroke(shortcut, 0));
	}

	private void init() {
		MenuStyle.style(this);
	}

	/** AWT 用 getLabel()，Swing 用 getText()，这里做兼容。 */
	public String getLabel() {
		return getText();
	}

	public void setLabel(String label) {
		setText(label);
	}

	/** 中->英 的英文原文（用于逻辑判断），未翻译时等于 label。 */
	public String getEnLabel() {
		return null;
	}

	public static KeyStroke toKeyStroke(MenuShortcut ms) {
		return KeyStroke.getKeyStroke(ms.getKey(),
			ms.usesShiftModifier() ? InputEvent.SHIFT_DOWN_MASK : 0);
	}
}
