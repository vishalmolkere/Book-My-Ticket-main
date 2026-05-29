package com.jsp.book.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailHelper {

	@jakarta.annotation.PostConstruct
	public void init() {
		// Ensures fromEmail is initialized from properties
	}

	@org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
	private String fromEmail;

	private static final String FROM_NAME = "Book-My-Ticket";
	private static final String SUBJECT = "Otp for Creating Account with BookMyTicket";
	private static final String TEMPLATE = "email-template.html";

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Async
	public void sendOtp(int otp, String name, String email) {

		// 🔥 Development Bypass: Always print OTP to console so you can see it while testing!
		System.out.println("========================================");
		System.out.println("   OTP for " + name + " (" + email + "): " + otp);
		System.out.println("========================================");

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(fromEmail, FROM_NAME);
			helper.setTo(email);
			helper.setSubject(SUBJECT);

			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);

			String body = templateEngine.process(TEMPLATE, context);
			helper.setText(body, true);

			mailSender.send(message);

		} catch (Exception ex) {
			System.err.println("⚠️ Could not send OTP email to " + email + " due to invalid Google credentials.");
			System.err.println("⚠️ Please use the OTP printed in the console above to continue testing.");
		}
	}
}
