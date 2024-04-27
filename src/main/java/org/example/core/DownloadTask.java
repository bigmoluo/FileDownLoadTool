package org.example.core;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.example.constant.Constant;
import org.example.utils.FileUtils;
import org.example.utils.HttpUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
/*
	下载任务类
 */
@Log
public class DownloadTask extends RecursiveTask<Integer> {
	public static List<Long> list = new ArrayList<>();
	private String url;
	private long startPos;
	private long endPos;
	private long fileContentLength;
	public DownloadTask(String url, long startPos, long endPos, long fileContentLength) {
		this.url = url;
		this.startPos = startPos;
		this.endPos = endPos;
		this.fileContentLength = fileContentLength;
	}

	/**
	 * 分割任务下载
	 * @return
	 */
	@SneakyThrows
	@Override
	protected Integer compute() {
		if (endPos - startPos <= fileContentLength / Constant.MAX_THREAD) {
			list.add(startPos);
			String httpFileName = FileUtils.getHttpFileName(url);
			if (endPos == fileContentLength) endPos = 0;
			HttpURLConnection connection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

			try (
					InputStream inputStream = connection.getInputStream();
					BufferedInputStream input = new BufferedInputStream(inputStream);
					RandomAccessFile accessFile = new RandomAccessFile(
							Constant.PATH + httpFileName + ".temp" + startPos, "rw")
			) {
				byte[] buffer = new byte[Constant.BYTE_SIZE];
				int len = -1;
				while((len = input.read(buffer)) != -1) {
					DownLoadInfo.downSize.add(len);
					accessFile.write(buffer, 0, len);
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				connection.disconnect();
			}
			return 1;
		} else {	//划分子任务
			long middle = (endPos + startPos) / 2;
			DownloadTask left = new DownloadTask(url, startPos, middle,  fileContentLength);
			left.fork();
			DownloadTask right = new DownloadTask(url,middle+1, endPos, fileContentLength);
			right.fork();
			return left.join() + right.join();
		}
	}
}
