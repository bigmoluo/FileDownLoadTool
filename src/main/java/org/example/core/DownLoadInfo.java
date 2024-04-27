package org.example.core;

import org.example.constant.Constant;
import java.util.concurrent.atomic.LongAdder;

/*
下载信息类
 */
public class DownLoadInfo implements Runnable{
	private long fileContentLength;
	public static LongAdder downSize = new LongAdder();
	private double preSize;
	public DownLoadInfo(long fileContentLength) {
		this.fileContentLength = fileContentLength;
	}

	@Override
	public void run() {
		String httpFileSize = String.format("%.2f", fileContentLength / Constant.MB);
		int speed = (int) ((downSize.doubleValue() - preSize) / Constant.KB);
		preSize = downSize.doubleValue();
		String currentDownSize = String.format("%.2f", downSize.doubleValue() / Constant.MB);
		double remainFileSize = fileContentLength - downSize.doubleValue();
		String remainTime = String.format("%.1f", remainFileSize/ Constant.KB / speed);

		if ("Infinity".equalsIgnoreCase(remainTime)) {
			remainTime = "-";
		}

		String downInfo = String.format("已下载：%smb/%smb,速度：%skb/s,剩余时间：%ss",
				currentDownSize,httpFileSize,speed,remainTime);

		System.out.print("\r");
		System.out.print(downInfo);
	}
}
