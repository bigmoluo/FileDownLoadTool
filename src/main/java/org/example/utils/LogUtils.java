package org.example.utils;

public class LogUtils {

	public static String log(String msg) {
		return Thread.currentThread().getName() + msg;
	}
}
