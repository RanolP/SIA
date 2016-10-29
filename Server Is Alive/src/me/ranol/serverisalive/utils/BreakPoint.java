package me.ranol.serverisalive.utils;

public class BreakPoint {
	String msg = "";

	public void update(String msg) {
		this.msg = msg;
	}

	public String check() {
		return msg;
	}
}
