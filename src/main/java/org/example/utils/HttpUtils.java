package org.example.utils;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/*
	http工具类
 */
@Log
public class HttpUtils {

	/**
	 * 获取文件连接对象
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
		URL httpUrl = new URL(url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
		httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
						"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
		return httpURLConnection;
	}

	/**
	 * 分割文件连接对象
	 * @param url
	 * @param startPos 起始位置
	 * @param endPos 终止位置
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
		HttpURLConnection httpURLConnection = getHttpURLConnection(url);
		log.info(LogUtils.log("下载区间：" + startPos + "-" + endPos));
		if (endPos != 0) {
			httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
		} else {
			httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
		}
		return httpURLConnection;
	}

}
