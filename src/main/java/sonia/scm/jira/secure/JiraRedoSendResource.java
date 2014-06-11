package sonia.scm.jira.secure;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.CommentTemplateHandler;
import sonia.scm.jira.JiraGlobalContext;
import sonia.scm.jira.JiraIssueHandler;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.jira.JiraIssueRequestFactory;
import sonia.scm.repository.Repository;

import com.google.inject.Inject;
import com.google.inject.Provider;

@Path("plugins/jira/redo-send")
public class JiraRedoSendResource {

	private static final Logger logger = LoggerFactory.getLogger(JiraRedoSendResource.class);
	private  Provider<CommentTemplateHandler> templateHandlerProvider;
	private JiraGlobalContext context;
	private JiraIssueRequestFactory requestFactory;
	
	/**
	 * Create a new resource to resent a comment.
	 * Used to get the given parameters and selections from the application.
	 * @param context The given context with the used parameters.
	 * @param templateHandlerProvider The provider for the used handler.
	 * @param requestFactory The factory used to send an issue.
	 */
	@Inject
	public JiraRedoSendResource(JiraGlobalContext context, Provider<CommentTemplateHandler> templateHandlerProvider, JiraIssueRequestFactory requestFactory) {
		logger.debug("Global context: " + context.toString() + " Configuration: " + context.getConfiguration());
		logger.debug("TemplateHandlerProvider: " + templateHandlerProvider.toString());
		logger.debug("RequestFactory: " + requestFactory.toString());
		this.context = context;
		this.templateHandlerProvider = templateHandlerProvider;
		this.requestFactory = requestFactory;
	}

	/**
	 * Resent all comments that could not be sent at the last time.
	 * @return Response if the Comments could be executed or a returned error message.
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response redoSend()
	{
		logger.debug("Redo started by POST command");
		
		logger.debug("CommentTemplateHandler: " + templateHandlerProvider.get().toString());
		
		String savePath = context.getConfiguration().getSavePath();
		ReplayCommentsHandler replayCommentsHandler = new ReplayCommentsHandler(savePath, requestFactory, context, templateHandlerProvider);
		replayCommentsHandler.replay();

		logger.debug("Redo ended command");
		
		return Response.ok().build();
	}
}
