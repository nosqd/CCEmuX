package cc.nsqd.ccemuxplugins.tomsperipherals;

import cc.nsqd.ccemuxplugins.tomsperipherals.awt.IFrameContext;
import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.gpu.BaseGPU;
import com.tom.peripherals.gpu.Rect;
import com.tom.peripherals.gpu.WindowGPUContext;

import java.util.ArrayList;

public class WindowGPUContextWithFrames extends WindowGPUContext {
	public ArrayList<IFrameContext> frames = new ArrayList<>();
	public WindowGPUContextWithFrames(BaseGPU.GPUContext ctx, Rect rect) {
		super(ctx, rect);
	}

	public void addFrameContext(IFrameContext ctx) {
		frames.add(ctx);
	}

	@Override
	public void sync() throws TMLuaException {
		super.sync();
		for (IFrameContext frame : frames) {
			frame.repaint();
		}
	}
}
