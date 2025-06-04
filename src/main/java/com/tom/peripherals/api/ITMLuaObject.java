package com.tom.peripherals.api;

import dan200.computercraft.api.lua.LuaException;

public interface ITMLuaObject {
	String[] getMethodNames();
	Object[] call(IComputer computer, String method, Object[] args) throws TMLuaException, LuaException;
}
