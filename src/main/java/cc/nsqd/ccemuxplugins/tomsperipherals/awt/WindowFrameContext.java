package cc.nsqd.ccemuxplugins.tomsperipherals.awt;

import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.gpu.WindowGPUContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WindowFrameContext implements IFrameContext {
	private MonitorAWTFrame frame;
	public BufferedImage image;
	private WindowGPUContext ctx;
	public WindowFrameContext(WindowGPUContext ctx) {
		this.ctx = ctx;
		this.frame = new MonitorAWTFrame(this);
		this.frame.setTitle("Tom's Peripherals Monitor");
		this.frame.setVisible(true);
		this.frame.setSize(getWidth()*getScale(), getWidth()*getScale());

	}
	public void repaint() throws TMLuaException {
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
		Graphics g = image.getGraphics();
		for (int x = 0; x < ctx.getWidth(); x++) {
			for (int y = 0; y < ctx.getHeight(); y++) {
				int argb = ctx.screen[x][y];
				g.setColor(new Color(argb));
				g.fillRect(x * getScale(), y * getScale(), getScale(), getScale());
			}
		}
		g.dispose();
		this.frame.repaint();
		this.frame.setSize(getWidth(), getHeight());
		this.frame.setTitle(String.format("Tom's Peripherals Monitor | %dx%d (scale %d)", getWidth(), getHeight(), getScale()));
	}

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return ctx.getWidth();
	}

	public int getHeight() {
		return ctx.getHeight();
	}
}
