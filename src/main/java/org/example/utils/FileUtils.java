package org.example.utils;

/*
	文件工具类
 */
public class FileUtils {
	/**
	 * 获取文件名字
	 * @param url
	 * @return
	 */
	public static String getHttpFileName(String url) {
		int index = url.lastIndexOf("/");
		String httpFileName = url.substring(index + 1);
		return httpFileName;
	}
}
