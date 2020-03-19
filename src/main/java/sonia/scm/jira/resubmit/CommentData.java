/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package sonia.scm.jira.resubmit;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;

//~--- JDK imports ------------------------------------------------------------

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import sonia.scm.jira.Comments;
import sonia.scm.user.User;

/**
 * A CommentData instance is stored, if comment could not added to the jira
 * server. The CommentData class stores all informations which are needed to
 * execute the comment request again.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "comment")
public class CommentData implements Comparable<CommentData>
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
   * @param body The body of the comment.
   * @param created The date and time the comment was created.
   */
  public CommentData(String id, String repositoryId, String changesetId,
    String issueId, User committer, String body, Calendar created)
  {
    this.id = id;
    this.repositoryId = repositoryId;
    this.changesetId = changesetId;
    this.issueId = issueId;
    this.committer = committer;
    this.body = body;
    this.created = created;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(CommentData o)
  {
    return created.compareTo(o.created);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("id", id)
                  .add("repositoryid", repositoryId)
                  .add("changesetId", changesetId)
                  .add("issueId", issueId)
                  .add("committer", committer)
                  .add("body", body)
                  .add("created", Comments.format(created))
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  public User getCommitter() {
    return committer;
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
  private User committer;

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
