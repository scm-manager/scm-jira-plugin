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



package sonia.scm.jira.resubmit;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.codemonkey.simplejavamail.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.mail.api.MailSendBatchException;
import sonia.scm.mail.api.MailService;
import sonia.scm.repository.Changeset;
import sonia.scm.security.KeyGenerator;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

import javax.mail.Message;
import sonia.scm.jira.JiraIssueRequest;

/**
 * A class to handle problems of message sending.
 *
 */
@Singleton
public class MessageProblemHandler
{

  /** name of the data store */
  private static final String STORE = "jira.problem";

  /** logger for MessageProblemHandler */
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
   * Deletes the comment data with the given id from the store.
   *
   *
   * @param id comment data id
   */
  public void deleteComment(String id)
  {
    logger.debug("remove comment with id {}", id);
    store.remove(id);
  }


  /**
   * Handles a problem with a comment that could not be sent to the Jira-Server.
   *
   * @param request jira issue request
   * @param issueId IssueId of the comment in Jira.
   * @param body Body of the Jira comment.
   * @param changeset changeset containing the issue key
   */
  public void handleMessageProblem(JiraIssueRequest request,
    String issueId, String body,
    Changeset changeset)
  {
    //J-
    CommentData commentData = new CommentData(
      keyGenerator.createKey(),
      request.getRepository().getId(), 
      changeset.getId(), 
      issueId, 
      request.getAuthor(), 
      body, 
      request.getCreation()
    );
    //J+

    saveComment(commentData);
    sendMail(request.getConfiguration().getMailAddress(), commentData);
  }

  /**
   * Send the error mail.
   *
   *
   * @param address recipient address
   * @param commentData comment data
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
   * Returns all stored comments as sorted immutable list.
   *
   *
   * @return all stored comments
   */
  public List<CommentData> getAllComments()
  {
    return sort(store.getAll().values());
  }

  /**
   * Get all comments filtered by repository id as sorted immutable list.
   *
   *
   * @param repositoryId repository id
   *
   * @return all comments from the repository
   */
  public List<CommentData> getAllCommentsByRepository(String repositoryId)
  {
    //J-
    return sort(
      Iterables.filter(
        store.getAll().values(), 
        new RepositoryPredicate(repositoryId)
      )
    );
    //J+
  }

  /**
   * Returns the stored comment data, with the given id or {@code null} if no
   * such comment exists.
   *
   *
   * @param id id of stored comment.
   *
   * @return comment data or {@code null}
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
    c.append("<br/>Author: ").append(commentData.getAuthor());
    c.append("<br/> IssueId: ").append(commentData.getIssueId());
    c.append("<br/> Body: <br/>");
    c.append("<pre>").append(commentData.getBody()).append("</pre>");

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
   * Sends the email.
   *
   *
   * @param email email to send
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

  private List<CommentData> sort(Iterable<CommentData> commentData)
  {
    return Ordering.natural().immutableSortedCopy(commentData);
  }

  //~--- inner classes --------------------------------------------------------

  private static class RepositoryPredicate implements Predicate<CommentData>
  {
    private RepositoryPredicate(String repositoryId)
    {
      this.repositoryId = repositoryId;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Returns {@code true}, if the repository id is equals with the one from
     * the stored comment.
     *
     *
     * @param input
     * @return {@code true} if the repository ids are equal
     */
    @Override
    public boolean apply(CommentData input)
    {
      return repositoryId.equals(input.getRepositoryId());
    }

    //~--- fields -------------------------------------------------------------

    /** repository id */
    private final String repositoryId;
  }


  //~--- fields ---------------------------------------------------------------

  /** key generator */
  private final KeyGenerator keyGenerator;

  /** mail service */
  private final MailService mailService;

  /** data store */
  private final DataStore<CommentData> store;
}
