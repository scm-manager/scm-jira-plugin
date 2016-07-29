/***
 * Copyright (c) 2015, Sebastian Sdorra
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
 * https://bitbucket.org/sdorra/scm-manager
 * 
 */

package sonia.scm.jira;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import java.util.Set;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;

/**
 * The {@link JiraHookProcessor} searches for jira issue keys and calls the {@link JiraIssueHandler}, for every found
 * issue key. The {@link JiraHookProcessor} is called from the {@link JiraIssuePostReceiveHook} after each successful
 * push to a repository.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class JiraHookProcessor {

  private static final Logger logger = LoggerFactory.getLogger(JiraHookProcessor.class);
  
  private final JiraGlobalContext context;

  /**
   * Creates a new {@link JiraHookProcessor}.
   * 
   * @param context global jira context
   */
  @Inject
  public JiraHookProcessor(JiraGlobalContext context) {
    this.context = context;
  }
  
  /**
   * Process a received {@link Changeset} and calls the {@link JiraIssueHandler}, if a issue key could be found.
   * 
   * @param issueHandler issue handler
   * @param request issue request
   * @param changeset received changeset
   */
  public void process(JiraIssueHandler issueHandler, JiraIssueRequest request, Changeset changeset){
    Iterable<String> issueIds = extractIssueIds(changeset);

    for (String issueId : issueIds)
    {
      if (issueHandler != null)
      {
        if (!context.isHandled(request.getRepository(), changeset))
        {
          issueHandler.handleIssue(issueId, changeset);
        }
        else if (logger.isDebugEnabled())
        {
          logger.debug("changeset {} of repository {} is already handled, ignoring action for {}",
            changeset.getId(), request.getRepository().getId(), issueId);
        }
      }
    }

    if (!context.isHandled(request.getRepository(), changeset))
    {
      context.markAsHandled(request.getRepository(), changeset);
    }
  }
  
  private Iterable<String> extractIssueIds(Changeset changeset) {
    Set<String> issueIds = Sets.newLinkedHashSet();
    String description = Strings.nullToEmpty(changeset.getDescription());

    Matcher m = JiraChangesetPreProcessor.KEY_PATTERN.matcher(description);
    while (m.find())
    {
      String issueId = m.group();
      logger.trace("found issue id {} in commit message: {}", issueId, description);
      issueIds.add(issueId);
    }

    if (logger.isTraceEnabled())
    {
      logger.trace("found {} issue ids in commit message: {}", issueIds.size(), description);
    }

    return issueIds;
  }
}
