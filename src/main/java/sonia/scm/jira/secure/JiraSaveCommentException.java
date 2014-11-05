package sonia.scm.jira.secure;

import sonia.scm.jira.JiraException;

/**
 * Saving the Comment went wrong.
 *
 */
public class JiraSaveCommentException extends JiraException {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 7281873172693210860L;

	public JiraSaveCommentException() {
		super();
	}
	
	public JiraSaveCommentException(String message) {
		super(message);
	}
	
	public JiraSaveCommentException(Throwable cause) {
		super(cause);
	}
	
	public JiraSaveCommentException(String message, Throwable cause) {
		super(message, cause);
	}
}