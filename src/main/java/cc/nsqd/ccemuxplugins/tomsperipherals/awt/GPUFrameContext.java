package cc.nsqd.ccemuxplugins.tomsperipherals.awt;

import cc.nsqd.ccemuxplugins.tomsperipherals.peripherals.GPUExtPeripheral;
import com.tom.peripherals.api.TMLuaException;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GPUFrameContext implements IFrameContext {
	private MonitorAWTFrame frame;
	public BufferedImage image;
	private GPUExtPeripheral ctx;
	public GPUFrameContext(GPUExtPeripheral ctx) {
		this.ctx = ctx;
		this.frame = new MonitorAWTFrame(this);
		this.frame.setTitle("Tom's Peripherals GPU");
		this.frame.setVisible(true);

	}
	public void repaint() throws TMLuaException {
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
		Graphics g = image.getGraphics();
		for (int x = 0; x < ctx.getWidth(); x++) {
			for (int y = 0; y < ctx.getHeight(); y++) {
				int argb = ctx.screen[x][y];
				if (getScale() == 1) {
					image.setRGB(x * getScale(), y * getScale(), argb);
				}
				else {
					g.setColor(new Color(argb));
					g.fillRect(x * getScale(), y * getScale(), getScale(), getScale());
				}

			}
		}
		this.frame.repaint();
		this.frame.setSize(getWidth(), getHeight());
		this.frame.setTitle(String.format("Tom's Peripherals Monitor | %dx%d (scale %d)", getWidth(), getHeight(), getScale()));
	}


	@Override
	public int getScale() {
		return 8;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return ctx.getWidth()*getScale();
	}

	public int getHeight() {
		return ctx.getHeight()*getScale();
	}
}
