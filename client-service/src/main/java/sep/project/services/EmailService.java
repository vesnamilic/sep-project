package sep.project.services;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender javaMailSender;

	public void sendEmail(String emailAddress, String subject, String messageText) {
		
		MimeMessage mail = javaMailSender.createMimeMessage();

		try {          		
	        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
	        
	        helper.setTo(emailAddress);
	        helper.setSubject(subject);
	        helper.setText(messageText, true);
	        	        
            javaMailSender.send(mail);
            
   		}
		catch(Exception e) {
		}
	}
}
