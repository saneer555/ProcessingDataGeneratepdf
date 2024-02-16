package insurenceMain.MailUtils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtls {
	
	@Autowired
	private JavaMailSender mailSender;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailUtls.class);
	
	public boolean isMailSent(String to,String subject,String body,File file) {
		   boolean isMailSent = false;
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			helper.addAttachment("Eligble-Notice", file);
			mailSender.send(mimeMessage);
			isMailSent = true;
		} catch (MessagingException e) {
			logger.error("Exception occurred while sending email", e);
			e.printStackTrace();
		}
		return isMailSent;
	}

}
