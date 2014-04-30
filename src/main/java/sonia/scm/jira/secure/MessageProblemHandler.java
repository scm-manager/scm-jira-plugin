package sonia.scm.jira.secure;


import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.soap.RemoteComment;

/**
 * A class to handle problems of message sending.
 *
 */
public class MessageProblemHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageProblemHandler.class);
	
	private SaveComment saveComment;
	private InfoMailing infoMailing;
	private CommentData commentData;
	
	private String savePath;
	
	private boolean savingError;
	
	/**
	 * Creates class to handle problems with message to a corresponding comment.
	 * @param address An address to send the error message to.
	 * @param host A host used for the mail sending.
	 * @param from The message inserted as the sender in the mail.
	 */
	public MessageProblemHandler(String address, String host, String from, String savePath) {
		saveComment = new SaveComment();
		infoMailing = new InfoMailing(address, host, from);
		this.savePath = savePath;
	}
	
	/**
	 * Handles a problem with a comment that could not be sent to the Jira-Server.
	 * @param token A token used to log in.
	 * @param issueId IssueId of the comment in Jira.
	 * @param remoteComment Packaged Information about the corresponding comment(author, comment-body, author-role-level, date of creation).
	 * @param jiraUrl URL of the Jira-Server.
	 */
	public void handleMessageProblem(String token, String issueId, RemoteComment remoteComment, String jiraUrl) {
		savingError = false;
		commentData = new CommentData(remoteComment.getAuthor(), remoteComment.getBody(), remoteComment.getCreated(), issueId, remoteComment.getRoleLevel(), token, jiraUrl);
		
		saveComment();
		sendMail();
	}
	
	/**
	 * Handles a problem with a comment that could not be sent to the Jira-Server.
	 * @param token A token used to log in.
	 * @param issueId IssueId of the comment in Jira.
	 * @param roleLevel Role Level of the user in Jira.
	 * @param author Author of the comment used in Jira.
	 * @param body Body of the Jira comment.
	 * @param created Date the jira comment was created.
	 * @param jiraUrl URL of the Jira-Server.
	 */
	public void handleMessageProblem(String token, String issueId, String roleLevel, String author, String body, Calendar created, String jiraUrl) {
		savingError = false;
		commentData = new CommentData(author, body, created, issueId, roleLevel, token, jiraUrl);
		
		saveComment();
		sendMail();
	}
	
	/**
	 * Save the given comment on the Server.
	 */
	private void saveComment() {
		logger.debug("Save comment started.");
		try {
			saveComment.save(commentData, savePath);
		} catch (JiraSaveCommentException e) {
			logger.error(e.getMessage(), e);
			savingError = true;
		}
		logger.debug("Save comment completed. " + saveComment.getFileName(commentData, savePath));
	}
	
	/**
	 * Send the error mail.
	 */
	private void sendMail() {
		logger.debug("Send mail started.");
		String message = infoMailing.generateMailMessage(commentData, savingError);
		
		try {
			infoMailing.sendInfoMail(message);
		} catch (JiraMailingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("Send mail completed.");
	}
}
