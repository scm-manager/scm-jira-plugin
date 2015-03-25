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

//~--- JDK imports ------------------------------------------------------------

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A CommentData instance is stored, if comment could not added to the jira 
 * server. The CommentData class stores all informations which are needed to
 * execute the comment request again.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "comment")
public class CommentData
{

  /**
   * Constructor is only visible for JAXB.
   *
   */
  CommentData() {}

  /**
   * A class containing all data needed for a comment on a jira issue.
   *
   * @param id unique comment id
   * @param repositoryId id of the repository
   * @param changesetId id of the changeset
   * @param issueId The issue the comment is referring to.
   * @param author The author of the comment.
   * @param body The body of the comment.
   * @param created The date and time the comment was created.
   */
  public CommentData(String id, String repositoryId, String changesetId,
    String issueId, String author, String body, Calendar created)
  {
    this.id = id;
    this.repositoryId = repositoryId;
    this.changesetId = changesetId;
    this.issueId = issueId;
    this.author = author;
    this.body = body;
    this.created = created;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns comment author,
   *
   *
   * @return comment author
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * Returns content of comment.
   *
   *
   * @return content
   */
  public String getBody()
  {
    return body;
  }

  /**
   * Returns changeset id.
   *
   *
   * @return changeset id
   */
  public String getChangesetId()
  {
    return changesetId;
  }

  /**
   * Returns comment creation date.
   *
   *
   * @return creation date
   */
  public Calendar getCreated()
  {
    return created;
  }

  /**
   * Returns id of comment.
   *
   *
   * @return id
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns id of issue.
   *
   *
   * @return id of issue
   */
  public String getIssueId()
  {
    return issueId;
  }

  /**
   * Returns repository id.
   *
   *
   * @return repository id
   */
  public String getRepositoryId()
  {
    return repositoryId;
  }

  //~--- fields ---------------------------------------------------------------

  /** comment author */
  private String author;

  /** content of comment */
  private String body;

  /** changeset id */
  private String changesetId;

  /** creation date */
  private Calendar created;

  /** comment id */
  private String id;

  /** issue id */
  private String issueId;

  /** repository id */
  private String repositoryId;
}
