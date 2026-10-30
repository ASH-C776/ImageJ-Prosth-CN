package ij.menu;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Swing 版菜单（可含子菜单），与 java.awt.Menu 的常用 API 兼容。
 * <p>
 * 关键差异处理：AWT 的 {@code Menu.addActionListener()} 会把子菜单项的点击事件
 * 一并上报给父菜单（ImageJ 大量依赖这个行为，如“窗口”菜单、宏菜单、Editor 菜单）。
 * Swing 的 JMenu 不会转发，所以这里补上转发逻辑，保证原有代码不改也能正常工作。
 */
public class Menu extends JMenu {

	/** 分隔线的占位对象：让 getItem() 的返回值对分隔线也给出一致的 label "-"。 */
	private static final MenuItem SEPARATOR = new MenuItem("-");

	/** 本菜单（含其所有子项、子菜单）需要转发的监听器。 */
	private final List<ActionListener> inherited = new ArrayList<ActionListener>();

	public Menu() {
		super();
		init();
	}

	public Menu(String label) {
		super(label);
		init();
	}

	private void init() {
		MenuStyle.styleMenu(this);
	}

	public String getLabel() {
		return getText();
	}

	public void setLabel(String label) {
		setText(label);
	}

	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);
		if (l == null) return;
		if (!inherited.contains(l)) {
			inherited.add(l);
			for (int i = 0; i < getItemCount(); i++) attach(getItem(i));
		}
	}

	@Override
	public void removeActionListener(ActionListener l) {
		super.removeActionListener(l);
		if (l == null) return;
		inherited.remove(l);
		for (int i = 0; i < getItemCount(); i++) detach(getItem(i), l);
	}

	/** 把继承来的监听器挂到菜单项（以及它的子菜单）上，已挂过的不重复挂。 */
	private void attach(JMenuItem item) {
		if (item == null) return;
		for (int i = 0; i < inherited.size(); i++) {
			ActionListener l = inherited.get(i);
			boolean has = false;
			ActionListener[] ls = item.getActionListeners();
			for (int j = 0; j < ls.length; j++) if (ls[j] == l) { has = true; break; }
			if (!has) item.addActionListener(l);
		}
		if (item instanceof Menu) ((Menu) item).addInherited(inherited);
	}

	private void detach(JMenuItem item, ActionListener l) {
		if (item == null) return;
		item.removeActionListener(l);
		if (item instanceof Menu) ((Menu) item).removeInherited(l);
	}

	private void addInherited(List<ActionListener> ls) {
		for (int i = 0; i < ls.size(); i++) {
			ActionListener l = ls.get(i);
			if (!inherited.contains(l)) inherited.add(l);
		}
		for (int i = 0; i < getItemCount(); i++) attach(getItem(i));
	}

	private void removeInherited(ActionListener l) {
		inherited.remove(l);
		for (int i = 0; i < getItemCount(); i++) detach(getItem(i), l);
	}

	/** 与 AWT 一致：分隔线返回 label 为 "-" 的占位项。 */
	@Override
	public JMenuItem getItem(int pos) {
		if (pos < 0 || pos >= getItemCount()) return null;
		java.awt.Component c = getMenuComponent(pos);
		if (c instanceof JPopupMenu.Separator) return SEPARATOR;
		if (c instanceof JMenuItem) return (JMenuItem) c;
		return SEPARATOR;
	}

	@Override
	public int getItemCount() {
		return getMenuComponentCount();
	}

	/** AWT 的 add(String) 语义：返回新建的菜单项。 */
	@Override
	public MenuItem add(String label) {
		MenuItem item = new MenuItem(label);
		add(item);
		return item;
	}

	@Override
	public JMenuItem add(JMenuItem item) {
		MenuStyle.style(item);
		attach(item);
		return super.add(item);
	}

	@Override
	public JMenuItem insert(JMenuItem item, int pos) {
		MenuStyle.style(item);
		attach(item);
		return super.insert(item, pos);
	}

	/** 分隔线统一加上下留白。 */
	@Override
	public void addSeparator() {
		JPopupMenu.Separator sep = new JPopupMenu.Separator();
		MenuStyle.styleSeparator(sep);
		add(sep);
	}

	@Override
	public void insertSeparator(int index) {
		JPopupMenu.Separator sep = new JPopupMenu.Separator();
		MenuStyle.styleSeparator(sep);
		getPopupMenu().insert(sep, index);
	}
}
