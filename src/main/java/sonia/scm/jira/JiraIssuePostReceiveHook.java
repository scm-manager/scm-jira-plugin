/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.EagerSingleton;
import sonia.scm.event.Subscriber;
import sonia.scm.jira.resubmit.MessageProblemHandler;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.HookChangesetBuilder;
import sonia.scm.repository.api.HookContext;
import sonia.scm.repository.api.HookFeature;
import sonia.scm.util.IOUtil;

/**
 * Post receive repository hook, which updates jira issue if an issue key is 
 * found in the description of changeset.
 *
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
@Subscriber(async = true)
public final class JiraIssuePostReceiveHook
{

  /** the logger for JiraIssuePostReceiveHook */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraIssuePostReceiveHook.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraIssuePostReceiveHook.
   *
   *
   * @param context jira global context
   * @param requestFactory jira request factory
   * @param templateHandlerProvider comment template handler
   * @param messageProblemHandler message problem handler
   */
  @Inject
  public JiraIssuePostReceiveHook(JiraGlobalContext context,
    JiraIssueRequestFactory requestFactory,
    Provider<CommentTemplateHandler> templateHandlerProvider,
    MessageProblemHandler messageProblemHandler)
  {
    this.context = context;
    this.requestFactory = requestFactory;
    this.templateHandlerProvider = templateHandlerProvider;
    this.changesetPreProcessorFactory =
      new JiraChangesetPreProcessorFactory(context);
    this.messageProblemHandler = messageProblemHandler;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * This method is called whenever new data is pushed to a repository. The 
   * method delegates to {@link JiraChangesetPreProcessor} if valid jira 
   * configuration is available.
   * 
   * @param event post receive repository hook event
   */
  @Subscribe
  public void onEvent(PostReceiveRepositoryHookEvent event)
  {
    Repository repository = event.getRepository();

    if (repository != null)
    {
      JiraConfiguration configuration =
        JiraConfigurationResolver.resolve(context, repository);

      if (configuration.isValid())
      {

        if (configuration.isUpdateIssuesEnabled())
        {
          handleIssueEvent(event, repository, configuration);
        }
        else if (logger.isTraceEnabled())
        {
          logger.trace("jira update issues is disabled");
        }

      }
      else if (logger.isDebugEnabled())
      {
        logger.debug("no valid jira configuration found");
      }
    }
    else if (logger.isErrorEnabled())
    {
      logger.error("receive repository hook without repository");
    }
  }

  private void handleIssueEvent(PostReceiveRepositoryHookEvent event,
    Repository repository, JiraConfiguration configuration)
  {
    Iterable<Changeset> changesets = getChangesetsFromEvent(event);

    if (changesets != null)
    {
      JiraChangesetPreProcessor jcpp =
        changesetPreProcessorFactory.createPreProcessor(repository);
      JiraIssueRequest request = null;

      try
      {
        //J-
        request = requestFactory.createRequest(configuration, repository);
        
        jcpp.setJiraIssueHandler(
          new JiraIssueHandler(
            messageProblemHandler, 
            templateHandlerProvider.get(),
            request
          )
        );
        //J+

        for (Changeset c : changesets)
        {
          jcpp.process(c);
        }
      }
      finally
      {
        IOUtil.close(request);
      }
    }
    else if (logger.isWarnEnabled())
    {
      logger.warn("receive repository hook without changesets");
    }
  }

  //~--- get methods ----------------------------------------------------------

  private Iterable<Changeset> getChangesetsFromEvent(
    PostReceiveRepositoryHookEvent event)
  {
    Iterable<Changeset> changesets = null;

    if (event.isContextAvailable())
    {
      HookContext hookCtx = event.getContext();

      if (hookCtx.isFeatureSupported(HookFeature.CHANGESET_PROVIDER))
      {
        HookChangesetBuilder builder = hookCtx.getChangesetProvider();

        changesets = builder.setDisablePreProcessors(true).getChangesets();
      }
      else
      {
        logger.warn("hook context does not support changeset provider");
      }
    }
    else
    {
      logger.warn("event does not support hook context");
    }

    if (changesets == null)
    {
      logger.warn("fall back to old getChangesets method");
      changesets = event.getChangesets();
    }

    return changesets;
  }

  //~--- fields ---------------------------------------------------------------

  /** changeset pre processor factory */
  private final JiraChangesetPreProcessorFactory changesetPreProcessorFactory;

  /** global jira context */
  private final JiraGlobalContext context;

  /** message problem handler */
  private final MessageProblemHandler messageProblemHandler;

  /** jira request factory */
  private final JiraIssueRequestFactory requestFactory;

  /** comment template handler */
  private final Provider<CommentTemplateHandler> templateHandlerProvider;
}
