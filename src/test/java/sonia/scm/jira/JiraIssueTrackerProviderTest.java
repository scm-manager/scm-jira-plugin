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

package sonia.scm.jira;

import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.issuetracker.api.IssueTracker;
import sonia.scm.issuetracker.spi.IssueTrackerBuilder;
import sonia.scm.jira.config.JiraConfigurationResolver;
import sonia.scm.jira.config.JiraConfigurationStore;
import sonia.scm.jira.config.JiraGlobalConfiguration;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import javax.inject.Provider;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SubjectAware(value = "trillian", permissions = "configuration:write:jira")
@ExtendWith({MockitoExtension.class, ShiroExtension.class})
class JiraIssueTrackerProviderTest {

  @Mock
  private IssueTrackerBuilder builder;

  private JiraConfigurationStore configStore;

  @Mock
  private Provider<AdvancedHttpClient> httpClientProvider;

  @Mock
  private IssueTracker issueTracker;

  private JiraIssueTrackerProvider issueTrackerProvider;

  private final Repository repository = RepositoryTestData.createHeartOfGold();

  @BeforeEach
  void setUpConfiguration() {
    configStore = new JiraConfigurationStore(new InMemoryConfigurationStoreFactory());
    issueTrackerProvider = new JiraIssueTrackerProvider(
      new JiraConfigurationResolver(configStore),
      httpClientProvider
    );
  }

  @Test
  void shouldNotCreateIssueTrackerWithoutValidConfiguration() {
    Optional<IssueTracker> tracker = issueTrackerProvider.create(builder, repository);
    assertThat(tracker).isEmpty();
  }

  @Test
  void shouldProvideReadOnlyIssueTracker() {
    JiraGlobalConfiguration configuration = new JiraGlobalConfiguration();
    configuration.setUrl("https://issues.hitchhiker.com");
    configStore.setGlobalConfiguration(configuration);

    IssueTrackerBuilder.ReadStage readStage = mock(IssueTrackerBuilder.ReadStage.class);
    when(builder.start(any(), any(), any())).thenReturn(readStage);
    when(readStage.build()).thenReturn(issueTracker);

    Optional<IssueTracker> tracker = issueTrackerProvider.create(builder, repository);
    assertThat(tracker).isPresent();

    verify(readStage).build();
  }

  @Test
  void shouldProvideCommentingIssueTracker() {
    JiraGlobalConfiguration configuration = new JiraGlobalConfiguration();
    configuration.setUrl("https://issues.hitchhiker.com");
    configuration.setUpdateIssues(true);
    configStore.setGlobalConfiguration(configuration);

    IssueTrackerBuilder.ReadStage readStage = mock(IssueTrackerBuilder.ReadStage.class);
    when(builder.start(any(), any(), any())).thenReturn(readStage);
    IssueTrackerBuilder.CommentingStage commentingStage = mock(IssueTrackerBuilder.CommentingStage.class);
    when(readStage.commenting(any(), any())).thenReturn(commentingStage);
    IssueTrackerBuilder.ChangeStateStage changeStateStage = mock(IssueTrackerBuilder.ChangeStateStage.class);
    when(commentingStage.template(any())).thenReturn(changeStateStage);
    when(changeStateStage.build()).thenReturn(issueTracker);

    Optional<IssueTracker> tracker = issueTrackerProvider.create(builder, repository);
    assertThat(tracker).isPresent();

    verify(changeStateStage).build();
  }

  @Test
  void shouldProvideStateChangingIssueTracker() {
    JiraGlobalConfiguration configuration = new JiraGlobalConfiguration();
    configuration.setUrl("https://issues.hitchhiker.com");
    configuration.setUpdateIssues(true);
    configuration.setAutoClose(true);
    configStore.setGlobalConfiguration(configuration);

    IssueTrackerBuilder.ReadStage readStage = mock(IssueTrackerBuilder.ReadStage.class);
    when(builder.start(any(), any(), any())).thenReturn(readStage);
    IssueTrackerBuilder.CommentingStage commentingStage = mock(IssueTrackerBuilder.CommentingStage.class);
    when(readStage.commenting(any(), any())).thenReturn(commentingStage);
    IssueTrackerBuilder.ChangeStateStage changeStateStage = mock(IssueTrackerBuilder.ChangeStateStage.class);
    when(commentingStage.template(any())).thenReturn(changeStateStage);
    IssueTrackerBuilder.ChangeStateRenderStage changeStateRenderStage = mock(IssueTrackerBuilder.ChangeStateRenderStage.class);
    when(changeStateStage.stateChanging(any())).thenReturn(changeStateRenderStage);
    IssueTrackerBuilder.FinalStage finalStage = mock(IssueTrackerBuilder.FinalStage.class);
    when(changeStateRenderStage.template(any())).thenReturn(finalStage);
    when(finalStage.build()).thenReturn(issueTracker);

    Optional<IssueTracker> tracker = issueTrackerProvider.create(builder, repository);
    assertThat(tracker).isPresent();

    verify(finalStage).build();
  }


}
