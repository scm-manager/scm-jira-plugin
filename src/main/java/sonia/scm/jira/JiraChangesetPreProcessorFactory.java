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

import com.google.common.base.Strings;
import com.google.inject.Inject;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.ChangesetPreProcessorFactory;
import sonia.scm.repository.Repository;
import sonia.scm.util.HttpUtil;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.text.MessageFormat;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class JiraChangesetPreProcessorFactory
  implements ChangesetPreProcessorFactory
{

  /** Field description */
  public static final String PROPERTY_JIRA_URL = "jira.url";

  /** Field description */
  public static final String REPLACEMENT_LINK =
    "<a target=\"_blank\" href=\"{0}/browse/$0\">$0</a>";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param context
   */
  @Inject
  public JiraChangesetPreProcessorFactory(JiraGlobalContext context)
  {
    this.context = context;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  @Override
  public JiraChangesetPreProcessor createPreProcessor(Repository repository)
  {
    JiraChangesetPreProcessor cpp = null;
    String jiraUrl = repository.getProperty(PROPERTY_JIRA_URL);

    if (Strings.isNullOrEmpty(jiraUrl))
    {
      jiraUrl = context.getConfiguration().getUrl();
    }

    if (!Strings.isNullOrEmpty(jiraUrl))
    {
      jiraUrl = HttpUtil.getUriWithoutEndSeperator(jiraUrl);

      String replacementPattern = MessageFormat.format(REPLACEMENT_LINK,
                                    jiraUrl);

      cpp = new JiraChangesetPreProcessor(replacementPattern);
    }

    return cpp;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private JiraGlobalContext context;
}
