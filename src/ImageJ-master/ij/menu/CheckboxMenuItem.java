package ij.menu;

import javax.swing.*;

/** Swing 版复选菜单项，兼容 java.awt.CheckboxMenuItem 的 getState/setState。 */
public class CheckboxMenuItem extends JCheckBoxMenuItem {

	public CheckboxMenuItem(String label) {
		super(label);
		init();
	}

	public CheckboxMenuItem(String label, boolean state) {
		super(label, state);
		init();
	}

	private void init() {
		MenuStyle.style(this);
	}

	public String getLabel() {
		return getText();
	}

	public void setLabel(String label) {
		setText(label);
	}

	public boolean getState() {
		return isSelected();
	}

	public void setState(boolean state) {
		setSelected(state);
	}
}
