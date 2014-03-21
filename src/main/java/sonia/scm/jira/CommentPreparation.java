package sonia.scm.jira;

import java.text.MessageFormat;
import java.util.regex.Matcher;

import sonia.scm.repository.EscapeUtil;

import com.google.common.base.Strings;

public class CommentPreparation {
	
	private String jiraUrl;
	
	public CommentPreparation(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}
	
	public String prepareComment(String issueId, Comment comment)
	{	
		String body = Strings.nullToEmpty(comment.getBody());
	    
		return removeIssueLink(issueId, body);
	}
	
	/**
	 * Remove issue self reference link.
	 * {@see https://bitbucket.org/sdorra/scm-manager/issue/337/jira-comment-contains-unneccessary-link}.
	 *
	 * TODO: The preprocessor order on hooks should be fixed in the core.
	 *
	 *
	 * @param issueId
	 * @param body
	 *
	 * @return
	 */
	private String removeIssueLink(String issueId, String body)
	{
		//J-
		String link = MessageFormat.format(
				JiraChangesetPreProcessorFactory.REPLACEMENT_LINK, jiraUrl).replaceAll(Matcher.quoteReplacement("$0"), issueId);
		//J+

		body = body.replaceAll(link, issueId);
		body = body.replaceAll(EscapeUtil.escape(link), issueId);

		return body;
	}
}
