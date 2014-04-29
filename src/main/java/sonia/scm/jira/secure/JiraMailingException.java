package sonia.scm.jira.secure;

import sonia.scm.jira.JiraException;

/**
 * An exception in case the mail sending throws an exception.
 *
 */
public class JiraMailingException extends JiraException {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 5436707965615058240L;

	public JiraMailingException() {
		super();
	}
	
	public JiraMailingException(String message) {
		super(message);
	}

	public JiraMailingException(Throwable cause) {
		super(cause);
	}

	public JiraMailingException(String message, Throwable cause) {
		super(message, cause);
	}
}
