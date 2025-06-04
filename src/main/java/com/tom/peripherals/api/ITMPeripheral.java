package com.tom.peripherals.api;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tom.peripherals.function.ThrowingBiConsumer;
import com.tom.peripherals.function.ThrowingBiFunction;
import com.tom.peripherals.function.ThrowingConsumer;
import com.tom.peripherals.function.ThrowingFunction;
import com.tom.peripherals.function.ThrowingTriConsumer;
import com.tom.peripherals.function.ThrowingTriFunction;
import com.tom.peripherals.util.MapStream;

public interface ITMPeripheral {
	String getType();
	String[] getMethodNames();
	Object[] call(IComputer computer, String method, Object[] args) throws TMLuaException;
	default void attach(IComputer computer){}
	default void detach(IComputer computer){}

	public static final Object[] E = new Object[0];

	@SuppressWarnings("rawtypes")
	public static Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>> findLuaMethods(Class c){
		final MethodHandles.Lookup lookup = MethodHandles.lookup();
		Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>> map =
				listMethods(lookup, c, ThrowingTriFunction.class, Object[].class, IComputer.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Object[], TMLuaException>> map2 =
				listMethods(lookup, c, ThrowingBiFunction.class, Object[].class, Object[].class);
		Map<String, ThrowingFunction<Object, Object[], TMLuaException>> map3 =
				listMethods(lookup, c, ThrowingFunction.class, Object[].class);
		Map<String, ThrowingBiConsumer<Object, Object[], TMLuaException>> map4 =
				listMethods(lookup, c, ThrowingBiConsumer.class, void.class, Object[].class);
		Map<String, ThrowingTriConsumer<Object, IComputer, Object[], TMLuaException>> map5 =
				listMethods(lookup, c, ThrowingTriConsumer.class, void.class, IComputer.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Boolean, TMLuaException>> map6 =
				listMethods(lookup, c, ThrowingBiFunction.class, boolean.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Integer, TMLuaException>> map7 =
				listMethods(lookup, c, ThrowingBiFunction.class, int.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Object, TMLuaException>> map8 =
				listMethods(lookup, c, ThrowingBiFunction.class, Object.class, Object[].class);
		Map<String, ThrowingFunction<Object, Integer, TMLuaException>> map9 =
				listMethods(lookup, c, ThrowingFunction.class, int.class);
		Map<String, ThrowingFunction<Object, Boolean, TMLuaException>> map10 =
				listMethods(lookup, c, ThrowingFunction.class, boolean.class);
		Map<String, ThrowingFunction<Object, Object, TMLuaException>> map11 =
				listMethods(lookup, c, ThrowingFunction.class, Object.class);
		Map<String, ThrowingConsumer<Object, TMLuaException>> map12 =
				listMethods(lookup, c, ThrowingConsumer.class, void.class);
		Map<String, ThrowingFunction<Object, Double, TMLuaException>> map13 =
				listMethods(lookup, c, ThrowingFunction.class, double.class);
		MapStream.of(map2 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> l.apply(o, a)).put(map);
		MapStream.of(map3 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> l.apply(o)).put(map);
		MapStream.of(map4 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> {l.accept(o, a);return E;}).put(map);
		MapStream.of(map5 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> {l.accept(o, co, a);return E;}).put(map);
		MapStream.of(map6 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map7 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map8 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map9 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map10).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map11).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map12).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> {l.accept(o);return E;}).put(map);
		MapStream.of(map13).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], TMLuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		map.put("listMethods", (o, co, a) -> map.keySet().stream().toArray(Object[]::new));
		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Map<String, T> listMethods(MethodHandles.Lookup lookup, Class clazz, Class<? super T> interfaceClass, Class returnType, Class... params){
		Map<String, T> map = new HashMap<>();
		try {
			Method im = interfaceClass.getDeclaredMethods()[0];
			MethodType imt = MethodType.methodType(im.getReturnType(), im.getParameterTypes());
			int args = im.getParameterCount();
			for(Method m : clazz.getMethods()){
				if(m.isAnnotationPresent(TMLuaMethod.class) &&
						((m.getModifiers() & Modifier.STATIC) != 0 ? m.getParameterCount() : m.getParameterCount()+1) == args &&
						m.getReturnType() == returnType && Arrays.equals(m.getParameterTypes(), params)){
					try {
						TMLuaMethod attr = m.getAnnotation(TMLuaMethod.class);
						MethodHandle mh = lookup.unreflect(m);
						T call = (T) LambdaMetafactory.metafactory(MethodHandles.lookup(), im.getName(),
								MethodType.methodType(interfaceClass), imt, mh, mh.type()).getTarget().invoke();
						map.put(!attr.value().isEmpty() ? attr.value() : m.getName(), call);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
