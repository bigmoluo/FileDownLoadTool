package org.example.core;

import lombok.extern.java.Log;
import org.example.constant.Constant;
import org.example.utils.FileUtils;
import org.example.utils.HttpUtils;


import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.*;
/*
下载主类
 */
@Log
public class Downloader {
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private CountDownLatch latch = new CountDownLatch(Constant.MAX_THREAD);
	//Fork/Join线程池多线程下载
	private ForkJoinPool pool = new ForkJoinPool();

	/**
	 * 下载主方法
	 * @param url
	 */
	public void download(String url) {
		String httpFileName = FileUtils.getHttpFileName(url);
		HttpURLConnection connection = null;
		DownLoadInfo downLoadInfo = null;
		try {
			connection = HttpUtils.getHttpURLConnection(url);
			int contentLength = connection.getContentLength();
			downLoadInfo = new DownLoadInfo(contentLength);

			scheduledExecutorService.scheduleAtFixedRate(downLoadInfo,1,1, TimeUnit.SECONDS);

			DownloadTask task = new DownloadTask(url,0, contentLength, contentLength);
			pool.submit(task).get();
			if (merge(url)) {
				clearFile(url);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.print("\r");
			System.out.print("下载完成");
			connection.disconnect();
			scheduledExecutorService.shutdownNow();
			pool.shutdownNow();
		}
	}

	/**
	 * 合并临时文件
	 * @param url
	 * @return
	 */
	private boolean merge(String url) {
		List<Long> list = DownloadTask.list;
		list.sort(Long::compareTo);
		int length = list.size();
		String httpFileName = FileUtils.getHttpFileName(url);
		byte[] buffer = new byte[Constant.BYTE_SIZE];
		int len = -1;
		try( RandomAccessFile accessFile = new RandomAccessFile(Constant.PATH + httpFileName, "rw")) {
			for (int i = 0; i < length; i++) {
				try( BufferedInputStream input = new BufferedInputStream(
								new FileInputStream(Constant.PATH + httpFileName + ".temp" + list.get(i)))
				) {
					while((len = input.read(buffer)) != -1) {
						accessFile.write(buffer, 0, len);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 清空临时文件
	 * @param url
	 */
	private void clearFile(String url) {
		List<Long> list = DownloadTask.list;
		String httpFileName = FileUtils.getHttpFileName(url);
		for (int i = 0; i < list.size(); i++) {
			File file = new File(Constant.PATH + httpFileName + ".temp" + list.get(i));
			if (file.exists()) {
				file.delete();
			}
		}

	}
}
