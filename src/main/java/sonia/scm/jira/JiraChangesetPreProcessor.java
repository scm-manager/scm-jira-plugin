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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPreProcessor;

//~--- JDK imports ------------------------------------------------------------

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The JiraChangesetPreProcessor searches for issue keys in the description of
 * changesets and replaces it with a link to the issue.
 *
 * @author Sebastian Sdorra
 */
public class JiraChangesetPreProcessor implements ChangesetPreProcessor
{

  /**
   * the logger for JiraChangesetPreProcessor
   */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraChangesetPreProcessor.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraChangesetPreProcessor
   *
     * @param keyPattern
   * @param keyReplacementPattern key replacement pattern
   */
  public JiraChangesetPreProcessor(Pattern keyPattern, String keyReplacementPattern)
  {
    this.keyPattern = keyPattern;
    this.keyReplacementPattern = keyReplacementPattern;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Checks the changeset description for issue keys and and replaces the keys,
   * with a link.
   *
   * @param changeset changeset
   */
  @Override
  public void process(Changeset changeset)
  {
    StringBuffer sb = new StringBuffer();
    Matcher m = matcher(changeset.getDescription());

    while (m.find())
    {        
      m.appendReplacement(sb, keyReplacementPattern);
    }

    m.appendTail(sb);
    changeset.setDescription(sb.toString());   
  }

  @VisibleForTesting
  Matcher matcher(String value)
  {
    return keyPattern.matcher(Strings.nullToEmpty(value));
  }
  
  //~--- fields ---------------------------------------------------------------

  /** regex issue key pattern */
  private final Pattern keyPattern;
  
  /** key replacement pattern */
  private final String keyReplacementPattern;
}
