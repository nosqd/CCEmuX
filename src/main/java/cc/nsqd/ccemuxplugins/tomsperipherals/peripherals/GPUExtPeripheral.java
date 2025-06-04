package cc.nsqd.ccemuxplugins.tomsperipherals.peripherals;

import cc.nsqd.ccemuxplugins.tomsperipherals.awt.GPUFrameContext;
import cc.nsqd.ccemuxplugins.tomsperipherals.awt.IFrameContext;
import cc.nsqd.ccemuxplugins.tomsperipherals.cc.CCComputer;
import com.tom.peripherals.api.IComputer;
import com.tom.peripherals.api.ITMPeripheral;
import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.api.TMLuaMethod;
import com.tom.peripherals.gpu.*;
import com.tom.peripherals.util.ParamCheck;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.clgd.ccemux.api.peripheral.Peripheral;
import org.jetbrains.annotations.Nullable;

public class GPUExtPeripheral extends GPUImpl implements Peripheral, BaseGPU.GPUContext, VRAM.VRAMObject, IDynamicPeripheral {
	public IComputerAccess computer;
	public int maxX = 0;
	public int maxY = 0;
	public int size = 16;
	public int[][] screen = new int[16][16];
	public VRAM vram = new VRAM(16 * 1024 * 1024);
	public IFrameContext window = null;

	public GPUExtPeripheral() throws LuaException {
	}

	@Override
	public void set(int x, int y, int c) {
		screen[x][y] = Integer.reverseBytes(c) >> 8 | 0xFF000000;
	}

	@Override
	@TMLuaMethod
	public void sync() throws TMLuaException {
		if (window != null) {
			window.repaint();
		}
	}

	@Override
	public VRAM getVRam() {
		return vram;
	}

	@Override
	public int getWidth() {
		return maxX * size;
	}

	@Override
	public int getHeight() {
		return maxY * size;
	}

	@Override
	public Rect getBounds() {
		return BaseGPU.GPUContext.super.getBounds();
	}

	@Override
	public String getType() {
		return "tm_gpu";
	}

	@Override
	public void attach(IComputerAccess computer) {
		this.computer = computer;
	}

	@TMLuaMethod
	public void createGpuView(Object[] a) {
		this.window = new GPUFrameContext(this);
	}

	@Override
	public void detach(IComputerAccess computer) {
		this.computer = null;
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return false;
	}

	@TMLuaMethod
	public void setMonRes(Object[] a) {
		int w = ParamCheck.getInt(a, 0);
		int h = ParamCheck.getInt(a, 1);
		this.maxX = w;
		this.maxY = h;
	}

	@TMLuaMethod
	public void setSize(Object[] a) throws LuaException {
		int s = ParamCheck.getInt(a, 0);
		if (s < 16)
			throw new LuaException("Bad Argument #1, (too small number (" + s + ") minimum value is 16 )");
		if (s > 64)
			throw new LuaException("Bad Argument #1, (too big number (" + s + ") maximum value is " + 64 + " )");

		int size = s * maxX * s * maxY * 4;
		vram.reallocEx(this, size);

		screen = new int[s * maxX][s * maxY];
		size = s;
	}

	@TMLuaMethod
	public void refreshSize() {

	}

	@Override
	@TMLuaMethod("getSize")
	public Object[] getSizeBaseGPU() {
		return new Object[]{this.ctx.getWidth(), this.ctx.getHeight(), maxX, maxY, size};
	}

	@Override
	public long getSize() {
		return this.maxX * this.size * this.maxY * this.size * 4;
	}

	@Override
	public String[] getMethodNames() {
		return ITMPeripheral.findLuaMethods(GPUExtPeripheral.class).keySet().toArray(String[]::new);
	}

	@Override
	public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
		try {
			return callInt(computer, method, args);
		} catch (NoSuchMethodException e) {
			throw new LuaException("No such method");
		}
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments) throws LuaException {
		IComputer c = new CCComputer(computer, context);
		return MethodResult.of(c.mapTo(call(c, getMethodNames()[method], arguments.getAll())));

	}
}
