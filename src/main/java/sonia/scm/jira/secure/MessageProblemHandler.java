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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.codemonkey.simplejavamail.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.soap.RemoteComment;
import sonia.scm.mail.api.MailSendBatchException;
import sonia.scm.mail.api.MailService;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.security.KeyGenerator;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.Calendar;

import javax.mail.Message;

/**
 * A class to handle problems of message sending.
 *
 */
@Singleton
public class MessageProblemHandler
{

  /** Field description */
  private static final String STORE = "jira.problem";

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(MessageProblemHandler.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Creates class to handle problems with message to a corresponding comment.
   * @param mailService mail service
   * @param keyGenerator generate comment ids
   * @param dataStoreFactory data store factory
   */
  @Inject
  public MessageProblemHandler(MailService mailService,
    KeyGenerator keyGenerator, DataStoreFactory dataStoreFactory)
  {
    this.mailService = mailService;
    this.keyGenerator = keyGenerator;
    this.store = dataStoreFactory.getStore(CommentData.class, STORE);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   */
  public void deleteComment(String id)
  {
    logger.debug("remove comment with id {}", id);
    store.remove(id);
  }

  /**
   * Handles a problem with a comment that could not be sent to the Jira-Server.
   *
   * @param configuration jira configuration
   * @param issueId IssueId of the comment in Jira.
   * @param remoteComment Packaged Information about the corresponding comment(author, comment-body, author-role-level, date of creation).
   * @param changeset changeset containing the issue key
   * @param repository changed repository
   */
  public void handleMessageProblem(JiraConfiguration configuration,
    String issueId, RemoteComment remoteComment, Changeset changeset,
    Repository repository)
  {
    handleMessageProblem(configuration, issueId, remoteComment.getAuthor(),
      remoteComment.getBody(), remoteComment.getCreated(), changeset,
      repository);
  }

  /**
   * Handles a problem with a comment that could not be sent to the Jira-Server.
   *
   * @param configuration jira configuration
   * @param issueId IssueId of the comment in Jira.
   * @param author Author of the comment used in Jira.
   * @param body Body of the Jira comment.
   * @param created Date the jira comment was created.
   * @param changeset changeset containing the issue key
   * @param repository changed repository
   */
  public void handleMessageProblem(JiraConfiguration configuration,
    String issueId, String author, String body, Calendar created,
    Changeset changeset, Repository repository)
  {
    CommentData commentData = new CommentData(keyGenerator.createKey(),
                                configuration, author, body, created, issueId,
                                changeset, repository);

    saveComment(commentData);
    sendMail(configuration.getMailAddress(), commentData);
  }

  /**
   * Send the error mail.
   *
   *
   * @param address
   * @param commentData
   */
  public void sendMail(String address, CommentData commentData)
  {
    logger.debug("send mail for comment {} of {}", commentData.getId(),
      commentData.getIssueId());

    if (mailService.isConfigured())
    {
      Email mail = new Email();

      mail.addRecipient(null, address, Message.RecipientType.TO);
      mail.setSubject("A Jira comment could not be sent");
      mail.setTextHTML(generateMailMessage(commentData));
      sendMail(mail);
    }
    else
    {
      logger.warn("mail service is not configured");
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public Iterable<CommentData> getAllComments()
  {
    return store.getAll().values();
  }

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   */
  public CommentData getComment(String id)
  {
    return store.get(id);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Generates a HTML-message-text for a stopped comment.
   *
   * @param commentData The given data for the comment to inform about.
   *
   * @return html mail content
   */
  private String generateMailMessage(CommentData commentData)
  {
    StringBuilder c = new StringBuilder();

    c.append("The following comment could not be sent to the jira server:");
    c.append("<br/>");
    c.append("<br/> Author: ").append(commentData.getAuthor());
    c.append("<br/> IssueId: ").append(commentData.getIssueId());
    c.append("<br/> Body: <br/>").append(commentData.getBody());

    return c.toString();
  }

  /**
   * Save the given comment on the Server.
   *
   * @param commentData
   */
  private void saveComment(CommentData commentData)
  {
    logger.debug("save comment {}", commentData);

    store.put(commentData.getId(), commentData);
  }

  /**
   * Method description
   *
   *
   * @param email
   */
  private void sendMail(Email email)
  {
    try
    {
      mailService.send(email);
      logger.debug("send mail completed.");
    }
    catch (MailSendBatchException ex)
    {
      logger.error("could not send mail", ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final KeyGenerator keyGenerator;

  /** Field description */
  private final MailService mailService;

  /** Field description */
  private final DataStore<CommentData> store;
}
