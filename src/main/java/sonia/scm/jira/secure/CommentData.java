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



package sonia.scm.jira.secure;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.jira.JiraConfiguration;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

//~--- JDK imports ------------------------------------------------------------

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class description
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "comment")
public class CommentData
{

  /**
   * Constructs ...
   *
   */
  CommentData() {}

  /**
   * A class containing all data needed for a comment on a jira issue.
   * @param id unique comment id
   * @param configuration jira configuration used at the time of comment try
   * @param author The author of the comment.
   * @param body The body of the comment.
   * @param created The date and time the comment was created.
   * @param issueId The issue the comment is referring to.
   * @param changeset
   * @param repository
   */
  public CommentData(String id, JiraConfiguration configuration, String author,
    String body, Calendar created, String issueId, Changeset changeset,
    Repository repository)
  {
    this.configuration = configuration;
    this.author = author;
    this.body = body;
    this.created = created;
    this.issueId = issueId;
    this.changeset = changeset;
    this.repository = repository;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getBody()
  {
    return body;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Changeset getChangeset()
  {
    return changeset;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public JiraConfiguration getConfiguration()
  {
    return configuration;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Calendar getCreated()
  {
    return created;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getId()
  {
    return id;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getIssueId()
  {
    return issueId;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public Repository getRepository()
  {
    return repository;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String author;

  /** Field description */
  private String body;

  /** Field description */
  private Changeset changeset;

  /** Field description */
  private JiraConfiguration configuration;

  /** Field description */
  private Calendar created;

  /** Field description */
  private String id;

  /** Field description */
  private String issueId;

  /** Field description */
  private Repository repository;
}
