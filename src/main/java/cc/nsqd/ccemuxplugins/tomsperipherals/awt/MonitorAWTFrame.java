package cc.nsqd.ccemuxplugins.tomsperipherals.awt;

import java.awt.BorderLayout;

import javax.swing.JFrame;


public class MonitorAWTFrame extends JFrame {
	public IFrameContext gpu;
	public MonitorAWTFrame(IFrameContext gpu) {
		this.gpu = gpu;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
		add(new MonitorAWTPane(gpu), BorderLayout.CENTER);
		pack();
	}
}
