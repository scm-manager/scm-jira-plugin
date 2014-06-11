package sonia.scm.jira.secure;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

import sonia.scm.jira.CommentTemplateHandler;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.jira.JiraHandler;
import sonia.scm.jira.JiraIssueHandler;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.jira.JiraIssueRequestFactory;

public class ReplayCommentsHandler {
	
	Logger logger = LoggerFactory.getLogger(ReplayCommentsHandler.class);

	private ExtractComment extractComment;
	private String savePath;
	private JiraIssueHandler jiraIssueHandler;
	private JiraIssueRequestFactory requestFactory;
	private JiraGlobalContext context;
	private Provider<CommentTemplateHandler> templateHandlerProvider;
	
	/**
	 * Class to replay all comments saved in the given folder.
	 * These comments could not be sent due to network problems or other exceptions.
	 * @throws JAXBException 
	 * 
	 */
	public ReplayCommentsHandler(String savePath, JiraIssueRequestFactory requestFactory, JiraGlobalContext context, Provider<CommentTemplateHandler> templateHandlerProvider) {
		extractComment = new ExtractComment();
		
		if(!savePath.endsWith("/")) {
			this.savePath = savePath + "/";
		} else {
			this.savePath = savePath;
		}
		
		this.requestFactory = requestFactory;
		this.context = context;
		this.templateHandlerProvider = templateHandlerProvider;
	}
	
	/**
	 * Extract all XML-Files in the given path.
	 * Resent these comments to jira.
	 */
	public void replay() {
		List<CommentData> commentsListed = extractComment.getAllComments(savePath);
		
		// send comments to jira
		for(CommentData commentData : commentsListed) {
			addComment(commentData);
		}
	}

	/**
	 * Add the given comment to jira and delete the XML-File.
	 * @param commentData The data to use for resent.
	 */
	private void addComment(CommentData commentData) {
		
		JiraIssueRequest request = requestFactory.createRequest(context.getConfiguration(), commentData.getRepository());
		logger.debug("JiraIssueRequest: " + request);
		logger.debug(commentData.getRepository().getProperty(JiraConfiguration.PROPERTY_JIRA_URL));
		JiraIssueHandler jiraIssueHandler = new JiraIssueHandler(templateHandlerProvider.get(), request);
		
		jiraIssueHandler.handleIssue(commentData.getIssueId(), commentData.getChangeset());
		
		String deletePath = SaveComment.getFileName(commentData, savePath);
		boolean deleted = extractComment.deleteCommentFile(deletePath);
		if(deleted) {
			logger.debug("The corresponding file was deleted after resending (" + deletePath + ")");
		} else {
			logger.debug("The corresponding file could not be deleted after resending deleted (" + deletePath + ")");
		}
	}
}
