package com.autotoll.forward.thread;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.autotoll.forward.check.Config;

public class MailSenderRunner implements Runnable {
	private static final Logger logger = Logger
			.getLogger(MailSenderRunner.class);
	private static String[] receiverList = Config.getProperty("mail.send.list",
			"").split(";");
	private static String senderHost = Config.getProperty("mail.send.host", "");
	private static String senderUsername = Config.getProperty(
			"mail.send.user.name", "");
	private static String senderPassword = Config.getProperty(
			"mail.send.user.password", "");
	private static int location = Config.getInt("forward.location", 1);
	private String content;
	private static Session session;

	public MailSenderRunner(String content) {
		super();
		this.content = content;
	}

	private static Session newSession() {
		if (session == null) {
			Properties properties = System.getProperties();
			properties.put("mail.smtp.auth", "true");
			properties.setProperty("mail.smtp.host", senderHost);
			session = Session.getInstance(properties,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(senderUsername,
									senderPassword);
						}
					});

		}
		return session;
	}

	public static void start(String content) {
		Thread t = new Thread(new MailSenderRunner(content));
		t.start();
	}

	@Override
	public void run() {
		logger.info("开始发送邮件");
		for (String receiver : receiverList) {
			try {
				MimeMessage message = new MimeMessage(newSession());
				message.setFrom(new InternetAddress(senderUsername));
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(receiver));
				switch (location) {
				case 1:
					message.setSubject("广东转发服务出现异常,请及时处理");
					break;
				case 2:
					message.setSubject("广西转发服务出现异常,请及时处理");
				default:
					message.setSubject("转发服务出现异常,请及时处理");
					break;
				}
				message.setText(content);

				Transport.send(message);
				logger.info("发送邮件成功:[to:" + receiver + "]");

			} catch (Exception e) {
				logger.error("发送邮件出错", e);
			}
		}
		logger.info("发送邮件完毕");
	}

}
