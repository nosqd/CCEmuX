package com.tom.peripherals.util;

import com.tom.peripherals.api.TMLuaMethod;

public interface IReferenceable {

	@TMLuaMethod
	public Object ref();
}
