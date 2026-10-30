package ij.ui;

import java.awt.Color;

/** 统一设计令牌（design tokens），菜单、工具栏、对话框、状态栏共用同一套配色与尺寸。
 * <p>采用临床蓝（clinical blue）强调色，契合口腔/正畸影像的洁净专业观感。
 * 所有界面组件改这里一处即可整体换肤；后续若要加深色模式，只需在此扩展 light/dark 两套。 */
public final class Theme {
	// 强调色（主按钮、选中态、链接/焦点）
	public static final Color ACCENT       = new Color(0x18, 0x5F, 0xA5);
	public static final Color ACCENT_SOFT  = new Color(0xE8, 0xF0, 0xFB); // 选中/悬浮浅底
	// 表面
	public static final Color SURFACE      = new Color(0xFF, 0xFF, 0xFF); // 卡片/按钮白底
	public static final Color PANEL        = new Color(0xF5, 0xF8, 0xFC); // 次级面板浅蓝白
	public static final Color BORDER       = new Color(0xD6, 0xE2, 0xF0); // 描边/分隔
	// 文本
	public static final Color TEXT         = new Color(0x1F, 0x23, 0x29);
	public static final Color TEXT_SECOND  = new Color(0x5F, 0x6B, 0x7A);
	public static final Color TEXT_DISABLED = new Color(0xA8, 0xAD, 0xB4);
	// 图标（普通态用偏深的近黑，避免纯黑生硬）
	public static final Color ICON         = new Color(0x2B, 0x2F, 0x36);

	public static final int RADIUS = 8;

	private Theme() {}
}
