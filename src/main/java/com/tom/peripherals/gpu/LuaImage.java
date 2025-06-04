package com.tom.peripherals.gpu;

import java.io.IOException;

import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.api.TMLuaMethod;
import com.tom.peripherals.api.ReferenceableLuaObject;
import com.tom.peripherals.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.gpu.VRAM.VRAMObject;
import com.tom.peripherals.util.Image;
import com.tom.peripherals.util.ImageIO;
import com.tom.peripherals.util.ParamCheck;

public class LuaImage extends ReferenceableLuaObject implements GPUContext, VRAMObject {
	private final VRAM vram;
	private Image image;

	public LuaImage(VRAM vram, Image image) {
		super(vram.getRefMngr());
		this.vram = vram;
		this.image = image;
	}

	@Override
	@TMLuaMethod
	public int getWidth() {
		if (image == null)throw new TMLuaException("Error: Use after free");
		return image.getWidth();
	}

	@Override
	@TMLuaMethod
	public int getHeight() {
		if (image == null)throw new TMLuaException("Error: Use after free");
		return image.getHeight();
	}

	@TMLuaMethod
	public Object[] getAsBuffer() {
		if (image == null)throw new TMLuaException("Error: Use after free");
		Object[] a = new Object[image.getWidth() * image.getHeight()];
		int[] d = image.getData();
		for (int i = 0; i < d.length; i++) {
			a[i] = d[i];
		}
		return a;
	}

	@Override
	@TMLuaMethod
	public Object ref() {
		if (image == null)throw new TMLuaException("Error: Use after free");
		return super.ref();
	}

	@Override
	public void set(int x, int y, int c) {
		if (image == null)throw new TMLuaException("Error: Use after free");
		image.setRGB(x, y, c);
	}

	@Override
	public void sync() {}

	@TMLuaMethod
	public Object gpuDraw() throws TMLuaException {
		if (image == null)throw new TMLuaException("Error: Use after free");
		return new GPUImpl(this);
	}

	@Override
	public VRAM getVRam() {
		return vram;
	}

	@Override
	public long getSize() {
		return getWidth() * getHeight() * 4;
	}

	public Image getImage() throws TMLuaException {
		if (image == null)throw new TMLuaException("Error: Use after free");
		return image;
	}

	@TMLuaMethod
	public void free() throws TMLuaException {
		if (image == null)return;
		vram.getRefMngr().remove(this);
		vram.free(this);
		image = null;
	}

	@TMLuaMethod
	public Object[] saveImage() throws TMLuaException {
		if (image == null)throw new TMLuaException("Error: Use after free");
		LuaByteBuffer buf = new LuaByteBuffer(vram);
		try {
			ImageIO.write(image, buf.asOutputStream());
		} catch (IOException e) {
			throw new TMLuaException(e.getMessage());
		}
		return new Object[] {buf};
	}

	@TMLuaMethod
	public void setRGB(Object[] a) throws TMLuaException {
		int x = ParamCheck.getInt(a, 0) + 1;
		int y = ParamCheck.getInt(a, 1) + 1;
		int rgb = ParamCheck.toColor(a, 2);
		image.setRGB(x, y, rgb);
	}

	@TMLuaMethod
	public int getRGB(Object[] a) throws TMLuaException {
		int x = ParamCheck.getInt(a, 0) + 1;
		int y = ParamCheck.getInt(a, 1) + 1;
		return image.getRGB(x, y);
	}
}
