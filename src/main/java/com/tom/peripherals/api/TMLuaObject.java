package com.tom.peripherals.api;

import java.util.HashMap;
import java.util.Map;

import com.tom.peripherals.function.ThrowingTriFunction;
import com.tom.peripherals.util.Pair;
import dan200.computercraft.api.lua.LuaException;

public class TMLuaObject implements ITMLuaObject {
	@SuppressWarnings("rawtypes")
	private static Map<Class, Pair<String[], Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>>>> callCache = new HashMap<>();
	Pair<String[], Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>>> call;
	public TMLuaObject() {
		call = callCache.computeIfAbsent(getClass(), c -> {
			Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>> map = ITMPeripheral.findLuaMethods(c);
			return Pair.of(map.keySet().stream().toArray(String[]::new), map);
		});

	}
	@Override
	public String[] getMethodNames() {
		return call.getKey();
	}

	@Override
	public Object[] call(IComputer computer, String method, Object[] args) throws TMLuaException, LuaException {
		//System.out.println(method);
		ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException> c = call.getValue().get(method);
		if(c == null)throw new TMLuaException("No such method: " + method);
		else{
			try {
				return c.apply(this, computer, args);
			} catch (TMLuaException e){
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
				throw new TMLuaException("Java Exception: " + e.toString());
			}
		}
	}
	public Object[] callInt(IComputer computer, String method, Object[] args) throws TMLuaException, NoSuchMethodException {
		ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException> c = call.getValue().get(method);
		if(c == null)throw new NoSuchMethodException(method);
		else return c.apply(this, computer, args);
	}
}
