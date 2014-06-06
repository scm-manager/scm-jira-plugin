package sonia.scm.jira.secure;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "comment")
public class CommentData {
	
	private String jiraUrl;
	private String author;
	private String body;
	private Calendar created;
	private String issueId;
	private String roleLevel;
	private String token; //TODO: Needed?
	private Changeset changeset;
	private Repository repository;
	
	public CommentData() {
	}
	
	/**
	 * A class containing all data needed for a comment on a jira issue.
	 * @param author The author of the comment.
	 * @param body The body of the comment.
	 * @param created The date and time the comment was created.
	 * @param issueId The issue the comment is referring to.
	 * @param roleLevel The roleLevel of the comment author.
	 * @param token The authentication token of the author.
	 */
	public CommentData(String author, String body, Calendar created, String issueId, String roleLevel, String token, String jiraUrl, Changeset changeset, Repository repository) {
		this.author = author;
		this.body = body;
		this.created = created;
		this.issueId = issueId;
		this.roleLevel = roleLevel;
		this.token = token;
		this.jiraUrl = jiraUrl;
		this.setChangeset(changeset);
		this.setRepository(repository);
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getBody() {
		return body;
	}
	
	public Calendar getCreated() {
		return created;
	}
	
	public String getIssueId() {
		return issueId;
	}
	
	public String getRoleLevel() {
		return roleLevel;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setCreated(Calendar created) {
		this.created = created;
	}
	
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	
	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String getJiraUrl() {
		return jiraUrl;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}

	public Changeset getChangeset() {
		return changeset;
	}

	public void setChangeset(Changeset changeset) {
		this.changeset = changeset;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}