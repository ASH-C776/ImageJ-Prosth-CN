package ij.gui;

import java.awt.*;
import java.awt.event.*;
import ij.Prefs;
import ij.ui.Theme;

/** 自绘圆角按钮，用于 GenericDialog 的 OK / Cancel / No / Help。
 * <p>主按钮（primary）= 强调色实心 + 白字；次按钮 = 白底 + 强调色描边 + 强调色文字。
 * 用自绘取代 AWT 原生按钮，确保 Windows 下强调色与 4K 缩放一致生效，
 * 不依赖系统主题对 Button.setBackground 的支持。内部事件命令名仍保持英文，由调用方处理。 */
public class AccentButton extends Button implements MouseListener {
	private final boolean primary;
	private boolean pressed;

	public AccentButton(String label, boolean primary) {
		super(label);
		this.primary = primary;
		addMouseListener(this);
	}

	public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
	public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
	public void mouseClicked(MouseEvent e)  {}
	public void mouseEntered(MouseEvent e)  { repaint(); }
	public void mouseExited(MouseEvent e)   { repaint(); }

	public void paint(Graphics g) {
		Dimension d = getSize();
		if (d.width <= 0 || d.height <= 0) { super.paint(g); return; }
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color base   = primary ? Theme.ACCENT : Theme.SURFACE;
		Color fg     = primary ? Color.WHITE  : Theme.ACCENT;
		Color border = primary ? Theme.ACCENT : Theme.BORDER;
		if (pressed) base = base.darker();

		int r = Math.min(6, d.height / 4);
		g2.setColor(base);
		g2.fillRoundRect(0, 0, d.width - 1, d.height - 1, r, r);
		g2.setColor(border);
		g2.drawRoundRect(0, 0, d.width - 1, d.height - 1, r, r);

		Font f = getFont();
		if (f != null) g2.setFont(f);
		FontMetrics fm = g2.getFontMetrics();
		String label = getLabel();
		int tw = fm.stringWidth(label);
		int th = fm.getAscent();
		g2.setColor(fg);
		g2.drawString(label, (d.width - tw) / 2, (d.height + th) / 2 - fm.getDescent());

		if (isFocusOwner()) {
			g2.setColor(Theme.ACCENT);
			g2.drawRoundRect(2, 2, d.width - 5, d.height - 5, Math.max(2, r - 2), Math.max(2, r - 2));
		}
	}

	public Dimension getPreferredSize() {
		double s = Prefs.getGuiScale();
		Dimension d = super.getPreferredSize();
		int w = d.width > 0 ? d.width : 60;
		int h = d.height > 0 ? d.height : 22;
		return new Dimension((int)(w + 18 * s), (int)Math.max(h, 26 * s));
	}
}
