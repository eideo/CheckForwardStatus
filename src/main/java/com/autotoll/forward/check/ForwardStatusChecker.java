package com.autotoll.forward.check;

import org.apache.log4j.Logger;

import com.autotoll.forward.thread.CheckerThread;
import com.autotoll.forward.thread.DaemonThread;

public class ForwardStatusChecker {
	public static final Logger logger = Logger.getLogger(ForwardStatusChecker.class);
	public static void main(String[] args) throws InterruptedException {
		CheckerThread.check();
		logger.info("成功启动检测服务");
		DaemonThread.start();
	}
}
