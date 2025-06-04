package cc.nsqd.ccemuxplugins.tomsperipherals.awt;

import java.awt.image.BufferedImage;

public interface IFrameContext {
	public BufferedImage getImage();
	public int getWidth();
	public int getHeight();
	public void repaint();
	public int getScale();
}
