package com.tom.peripherals.api;

public class TMLuaException extends RuntimeException {
	private static final long serialVersionUID = -6487582951282118489L;
	private final int level;

	public TMLuaException() {
		this("error", 1);
	}

	public TMLuaException(String message ) {
		this(message, 1);
	}

	public TMLuaException(String message, int level ) {
		super(message);
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public class TMLuaInteruptedException extends TMLuaException {
		private static final long serialVersionUID = -7396879728472447101L;

	}
}
