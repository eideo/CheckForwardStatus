package com.autotoll.forward.thread;

public class DaemonThread {
	public static void start() throws InterruptedException{
		synchronized (DaemonThread.class) {
			DaemonThread.class.wait();
		}
	}
}
