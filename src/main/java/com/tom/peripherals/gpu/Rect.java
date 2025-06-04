package com.tom.peripherals.gpu;

import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.api.TMLuaMethod;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.util.ParamCheck;

public class Rect extends TMLuaObject {
	private boolean modif;
	private int x;
	private int y;
	private int w;
	private int h;

	public Rect() {
		this.modif = true;
	}

	public Rect(int x, int y, int w, int h) {
		this.modif = true;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rect(int w, int h) {
		this.modif = false;
		this.w = w;
		this.h = h;
	}

	@TMLuaMethod
	public int getX() {
		return x;
	}

	@TMLuaMethod
	public void setX(Object[] args) throws TMLuaException {
		if (!modif)throw new TMLuaException("Can't modify");
		this.x = ParamCheck.getInt(args, 0);
	}

	@TMLuaMethod
	public int getY() {
		return y;
	}

	@TMLuaMethod
	public void setY(Object[] args) throws TMLuaException {
		if (!modif)throw new TMLuaException("Can't modify");
		this.y = ParamCheck.getInt(args, 0);
	}

	@TMLuaMethod
	public int getW() {
		return w;
	}

	@TMLuaMethod
	public void setW(Object[] args) throws TMLuaException {
		if (!modif)throw new TMLuaException("Can't modify");
		int n = ParamCheck.getInt(args, 0);
		if (n < 0)throw new TMLuaException("Width value is less than 0");
		this.w = n;
	}

	@TMLuaMethod
	public int getH() {
		return h;
	}

	@TMLuaMethod
	public void setH(Object[] args) throws TMLuaException {
		if (!modif)throw new TMLuaException("Can't modify");
		int n = ParamCheck.getInt(args, 0);
		if (n < 0)throw new TMLuaException("Height value is less than 0");
		this.h = n;
	}

	public static Rect parseRect(Object[] a) throws TMLuaException {
		if (a.length < 4) {
			throw new TMLuaException("Too few arguments (expected x,y,width,height)");
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int w = ParamCheck.getInt(a, 2);
		int h = ParamCheck.getInt(a, 3);
		if (w > 0 && h > 0) {
			return new Rect(xStart, yStart, w, h);
		} else {
			throw new TMLuaException("Out of boundary");
		}
	}
}
