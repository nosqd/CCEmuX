package cc.nsqd.ccemuxplugins.tomsperipherals.awt;

import javax.swing.*;
import java.awt.*;

public class MonitorAWTPane extends JComponent {
	public IFrameContext gpu;

	public MonitorAWTPane(IFrameContext gpu) {
		this.gpu = gpu;
		setSize(new Dimension(gpu.getWidth() * gpu.getScale(), gpu.getHeight() * gpu.getScale()));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setSize(new Dimension(gpu.getWidth() * gpu.getScale(), gpu.getHeight() * gpu.getScale()));
		g.drawImage(gpu.getImage(), 0, 0, gpu.getWidth(), gpu.getHeight(), null);
	}
}
