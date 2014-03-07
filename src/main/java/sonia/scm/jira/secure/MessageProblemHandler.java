package sonia.scm.jira.secure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.soap.RemoteComment;

public class MessageProblemHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageProblemHandler.class);
	
	private SaveComment saveComment;
	private InfoMailing infoMailing;
	private CommentData commentData;
	
	private boolean savingError;
	
	public MessageProblemHandler(String address, String host) {
		saveComment = new SaveComment();
		infoMailing = new InfoMailing(address, host);
	}
	
	public void handleMessageProblem(String token, String issueId, RemoteComment remoteComment, String jiraUrl) {
		savingError = false;
		commentData = new CommentData(remoteComment.getAuthor(), remoteComment.getBody(), remoteComment.getCreated(), issueId, remoteComment.getRoleLevel(), token, jiraUrl);
		
		saveComment();
		sendMail();
	}
	
	private void saveComment() {
		try {
			saveComment.save(commentData);
		} catch (JiraSaveCommentException e) {
			logger.error(e.getMessage(), e);
			savingError = true;
		}
	}
	
	private void sendMail() {
		String message = infoMailing.generateMailMessage(commentData, savingError);
		
		try {
			infoMailing.sendInfoMail(message);
		} catch (JiraMailingException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
