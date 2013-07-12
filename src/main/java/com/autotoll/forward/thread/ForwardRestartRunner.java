package com.autotoll.forward.thread;

import org.apache.log4j.Logger;

import com.autotoll.forward.check.Config;

public class ForwardRestartRunner implements Runnable{
	private static final Logger logger = Logger.getLogger(ForwardRestartRunner.class);
	private Object lock = null;
	private ForwardRestartRunner(Object lock){
		this.lock = lock;
	}
	public static void start(Object obj) {
		Thread t = new Thread(new ForwardRestartRunner(obj));
		t.start();
	}
	@Override
	public void run() {
		int delay = Config.getInt("forward.restart.delay", 25);
		try {
			Thread.sleep(delay * 60 * 1000);
			 logger.info("开始关闭转发服务");
			 String[] stopShell = new String[]{"/bin/sh", "-c", Config.getProperty("forward.stop.path")};
			 Runtime.getRuntime().exec(stopShell);
			 logger.info("关闭转发服务结束");
			 logger.info("开始启动转发服务");
			 String[] startShell = new String[]{"/bin/sh", "-c", Config.getProperty("forward.start.path")};
			 Runtime.getRuntime().exec(startShell);
			 logger.info("启动转发服务结束");
			 
		} catch (Exception e) {
			logger.error("重启转发服务出错",e);
		}finally{
			synchronized (lock){
				 if(lock != null){
					 lock.notifyAll();
				 }
			 }
		}
	}

}