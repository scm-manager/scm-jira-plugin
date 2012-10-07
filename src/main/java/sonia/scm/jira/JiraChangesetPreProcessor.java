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

import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPreProcessor;

//~--- JDK imports ------------------------------------------------------------

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Sebastian Sdorra
 */
public class JiraChangesetPreProcessor implements ChangesetPreProcessor
{

  /** Field description */
  private static final Pattern KEY_PATTERN =
    Pattern.compile("\\b([a-zA-Z\\-]+-\\d+)");

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param keyReplacementPattern
   * @param projectKeys
   */
  public JiraChangesetPreProcessor(String keyReplacementPattern)
  {
    this.keyReplacementPattern = keyReplacementPattern;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param changeset
   */
  @Override
  public void process(Changeset changeset)
  {
    String description = changeset.getDescription();

    if (!Strings.isNullOrEmpty(description))
    {
      StringBuffer sb = new StringBuffer();
      Matcher m = KEY_PATTERN.matcher(description);

      while (m.find())
      {
        m.appendReplacement(sb, keyReplacementPattern);

        if (issueHandler != null)
        {
          issueHandler.handleIssue(m.group(), changeset);
        }
      }

      m.appendTail(sb);
      description = sb.toString();
    }

    changeset.setDescription(description);
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param issueHandler
   */
  public void setJiraIssueHandler(JiraIssueHandler issueHandler)
  {
    this.issueHandler = issueHandler;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private JiraIssueHandler issueHandler;

  /** Field description */
  private String keyReplacementPattern;
}
