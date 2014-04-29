package sonia.scm.jira.secure;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class InfoMailing {
	
	private String address;
	private String from;
	Session session;
	
	/**
	 * Create Class used to send a Mail.
	 * @param address Address to send the message to.
	 * @param host The used host.
	 * @param from Valid message used as sender in the generated mails.
	 */
	public InfoMailing(String address, String host, String from) {
		this.setAddress(address);
		
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		session = Session.getDefaultInstance(properties);
		
		this.from = from;
	}
	
	/**
	 * Send mail with the given message.
	 * @param htmlMessage The mail text in HTML format.
	 * @throws JiraMailingException
	 */
	public void sendInfoMail(String htmlMessage) throws JiraMailingException{
		//TODO: Send a mail to a given address
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(RecipientType.TO, new InternetAddress(address));
			message.setSubject("A Jira Comment could not be sent.");
			message.setContent(htmlMessage, "text/html");
		
			Transport.send(message);
		} catch (MessagingException e) {
			throw new JiraMailingException("The mail could not be sent.", e.getCause());
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Generates a HTML-message-text for a stopped comment.
	 * @param commentData The given data for the comment to inform about.
	 * @param savingError Inform about the saving status of this comment on the SCM Manager server.
	 * @return
	 */
	public String generateMailMessage(CommentData commentData, boolean savingError) {
		String htmlMessage = "";
		htmlMessage += "The following comment could not be sent to the jira server: <br/>";
		htmlMessage += "<br/> Author: " + commentData.getAuthor();
		htmlMessage += "<br/> IssueId: " + commentData.getIssueId();
		htmlMessage += "<br/> Body: </br>" + commentData.getBody();
		
		if(savingError) {
			htmlMessage += "<br/><br/><b> This comment could not be saved.</b>";
		}
		
		return htmlMessage;
	}
}