package sonia.scm.jira;

public class JiraExceptionTokenized extends JiraException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8655325351055471005L;
	
	private String token = null;
	
	public JiraExceptionTokenized(String token) {
		this.token = token;
	}

	public JiraExceptionTokenized(String message, String token) {
		super(message);
		this.token = token;
	}

	public JiraExceptionTokenized(Throwable cause, String token) {
		super(cause);
		this.token = token;
	}

	public JiraExceptionTokenized(String message, Throwable cause, String token) {
		super(message, cause);
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
