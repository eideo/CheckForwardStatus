package com.autotoll.forward.thread;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.autotoll.forward.check.Config;

public class CheckerThread {
	private static Logger logger = Logger.getLogger(CheckerThread.class);
	private static Object lock = new Object();
	private static AtomicBoolean isStopping = new AtomicBoolean(false);
	private static String url = null;

	private static String newURL() {
		if (url == null) {
			String forwardUrl = Config.getProperty("forward.url", "");
			StringBuilder builder = new StringBuilder(forwardUrl);
			for (int i = 1; i <= 50; i++) {
				builder.append(",").append(i);
			}
			
			url = builder.toString();
		}
		System.out.println(url);
		return url;
	}

	public static void check() {
		int unHandleMax = Config.getInt("forward.unhandle.max", 1000000);
		int checkInterval = Config.getInt("thread.check.interval", 30);
		int delay = Config.getInt("forward.restart.delay", 25);
		JSONObject obj = null;
		JSONArray list = null;
		int count = 0;
		while (true) {
			try {
				if (isStopping.get()) {
					logger.info("等待转发服务重启");
					synchronized (lock) {
						lock.wait();
					}
				}
				count = 0;
				isStopping.set(false);
				logger.info("检测转发服务数据开始");
				java.net.URL url = new URL(newURL());
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(30000);
				InputStream stream = connection.getInputStream();
				byte[] buffer = new byte[stream.available()];
				String str = "[]";
				while (stream.read(buffer, 0, buffer.length) != -1) {
					str = new String(buffer, "utf-8");
				}
				stream.close();
				list = JSONArray.fromObject(str);
				for (Object object : list) {
					obj = JSONObject.fromObject(object);
					count += obj.getInt("messageSize");
				}
				if (count >= unHandleMax) {
					StringBuilder builder = new StringBuilder();
					builder.append("转发服务出现异常\n总待发送数据为:").append(count);
					builder.append('\n').append("系统将在").append(delay)
							.append("分钟后重启转发服务,请及时处理!");
					MailSenderRunner.start(builder.toString());
					ForwardRestartRunner.start(lock);
					isStopping.set(true);
				} else {
					logger.info("检测转发服务数据结束");
					Thread.sleep(checkInterval * 60 * 1000);
				}
			} catch (Exception e) {
				logger.error("检测线程出现异常:", e);
				try {
					Thread.sleep(checkInterval * 60 * 1000);
				} catch (Exception ex) {
				}
			}
		}
	}

	public static void main(String[] args) {
		CheckerThread.check();
	}
}
