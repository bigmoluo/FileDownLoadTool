package org.example;

import org.example.core.Downloader;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);
		System.out.println("请输入文件地址：");
		String url = cin.nextLine();
		Downloader downloader = new Downloader();
		downloader.download(url);
	}
}