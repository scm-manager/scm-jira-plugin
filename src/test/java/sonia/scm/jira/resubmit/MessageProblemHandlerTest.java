/*
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.jira.JiraConfiguration;
import sonia.scm.jira.JiraIssueRequest;
import sonia.scm.mail.api.MailSendBatchException;
import sonia.scm.mail.api.MailService;
import sonia.scm.mail.api.MailTemplateType;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Person;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.security.KeyGenerator;
import sonia.scm.security.UUIDKeyGenerator;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryDataStore;
import sonia.scm.store.InMemoryDataStoreFactory;
import sonia.scm.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sonia.scm.jira.resubmit.Topics.TOPIC;

@ExtendWith(MockitoExtension.class)
class MessageProblemHandlerTest {

  private ScmConfiguration configuration = new ScmConfiguration();
  private final KeyGenerator keyGenerator = new UUIDKeyGenerator();
  private final DataStore<CommentData> dataStore = new InMemoryDataStore<>();
  private final DataStoreFactory dataStoreFactory = new InMemoryDataStoreFactory((InMemoryDataStore) dataStore);

  @Mock
  private MailService mailService;

  @Mock
  private MailService.EnvelopeBuilder envelopeBuilder;
  @Mock
  private MailService.SubjectBuilder subjectBuilder;
  @Mock
  private MailService.TemplateBuilder templateBuilder;
  @Mock
  private MailService.MailBuilder mailBuilder;

  @Mock
  private JiraIssueRequest request;

  private MessageProblemHandler messageProblemHandler;

  @BeforeEach
  void initProblemHandler() {
    messageProblemHandler = new MessageProblemHandler(configuration, mailService, keyGenerator, dataStoreFactory);
  }

  @Test
  void shouldSaveComment() {
    Repository repository = RepositoryTestData.createHeartOfGold();
    JiraConfiguration jiraConfiguration = new JiraConfiguration();
    jiraConfiguration.setMailAddress("trillian@hitchhiker.com");
    Person changesetAuthor = new Person("Trillian");
    User committer = new User("Zaphod");

    when(request.getRepository()).thenReturn(repository);
    when(request.getConfiguration()).thenReturn(jiraConfiguration);
    when(request.getCommitter()).thenReturn(Optional.of(committer));
    when(mailService.isConfigured()).thenReturn(false);

    messageProblemHandler.handleMessageProblem(request, "1", "alert comment", new Changeset("42", 1L, changesetAuthor));

    assertThat(dataStore.getAll().size()).isEqualTo(1);
    CommentData storedCommentData = dataStore.getAll().values().iterator().next();
    assertThat(storedCommentData.getIssueId()).isEqualTo("1");
    assertThat(storedCommentData.getRepositoryId()).isEqualTo(repository.getId());
    assertThat(storedCommentData.getChangesetId()).isEqualTo("42");
    assertThat(storedCommentData.getCommitter()).isEqualTo(committer);
  }

  @Test
  void shouldNotSendMail() {
    verify(mailService, never()).emailTemplateBuilder();
  }

  @Test
  void shouldSendMail() throws MailSendBatchException {
    Repository repository = RepositoryTestData.createHeartOfGold();
    JiraConfiguration jiraConfiguration = new JiraConfiguration();
    jiraConfiguration.setMailAddress("trillian@hitchhiker.com");
    Person changesetAuthor = new Person("Trillian");
    User committer = new User("Zaphod");
    configuration.setBaseUrl("hitchhiker.com/");

    when(request.getRepository()).thenReturn(repository);
    when(request.getConfiguration()).thenReturn(jiraConfiguration);
    when(request.getCommitter()).thenReturn(Optional.of(committer));

    mockMailBuilder();

    messageProblemHandler.handleMessageProblem(request, "1", "alert comment", new Changeset("42", 1L, changesetAuthor));

    verify(mailBuilder).send();
  }

  private void mockMailBuilder() {
    when(mailService.isConfigured()).thenReturn(true);
    when(mailService.emailTemplateBuilder()).thenReturn(envelopeBuilder);
    when(envelopeBuilder.onTopic(TOPIC)).thenReturn(envelopeBuilder);
    when(envelopeBuilder.toAddress(anyString())).thenReturn(envelopeBuilder);
    when(envelopeBuilder.withSubject(anyString())).thenReturn(subjectBuilder);
    when(subjectBuilder.withTemplate(anyString(), any(MailTemplateType.class))).thenReturn(templateBuilder);
    when(templateBuilder.andModel(any())).thenReturn(mailBuilder);
  }
}
