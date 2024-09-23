/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.jira;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.jira.rest.RestTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraStateChangerTest {

  @Mock
  private RestApi restApi;

  private JiraConfiguration configuration;

  private JiraStateChanger stateChanger;

  @BeforeEach
  void setUp() {
    configuration = new JiraConfiguration();
    configuration.setAutoCloseWords(Collections.emptyMap());
    stateChanger = new JiraStateChanger(restApi, configuration);
  }

  @Test
  void shouldReturnTransitionNames() throws IOException {
    transitions("SCM-42", "start", "done");

    Iterable<String> keyWords = stateChanger.getKeyWords("SCM-42");
    assertThat(keyWords).containsOnly("start", "done");
  }

  @Test
  void shouldReturnMappingNames() throws IOException {
    transitions("SCM-21");
    configuration.setAutoCloseWords(ImmutableMap.of("start", "begin", "end", "done"));

    Iterable<String> keyWords = stateChanger.getKeyWords("SCM-21");
    assertThat(keyWords).containsOnly("start", "end");
  }

  @Test
  void shouldReturnMappingsAndTransitions() throws IOException {
    transitions("SCM-21", "start", "done");
    configuration.setAutoCloseWords(ImmutableMap.of("begin", "start", "end", "done"));

    Iterable<String> keyWords = stateChanger.getKeyWords("SCM-21");
    assertThat(keyWords).containsOnly("start", "begin", "done", "end");
  }

  @Test
  void shouldReturnMultipleMappingKeyWord() throws IOException {
    transitions("SCM-21" );
    configuration.setAutoCloseWords(ImmutableMap.of("fix,fixes , closes ", "done"));

    Iterable<String> keyWords = stateChanger.getKeyWords("SCM-21");
    assertThat(keyWords).containsOnly("fix", "fixes", "closes");
  }

  @Test
  void shouldTriggerStateChange() throws IOException {
    transitions("SCM-42", "start", "done");

    stateChanger.changeState("SCM-42", "done");

    verify(restApi).changeState("SCM-42", "t-1");
  }

  @Test
  void shouldTriggerStateChangeWithMappedKeyWord() throws IOException {
    transitions("SCM-42", "start", "done");
    configuration.setAutoCloseWords(ImmutableMap.of("fix,fixes", "done"));

    stateChanger.changeState("SCM-42", "fixes");

    verify(restApi).changeState("SCM-42", "t-1");
  }

  @Test
  void shouldThrowExceptionIfKeyWordCouldNotBeFound() throws IOException {
    transitions("SCM-1");
    assertThrows(JiraException.class, () -> stateChanger.changeState("SCM-1", "throw"));
  }

  public void transitions(String issue, String... names) throws IOException {
    List<RestTransition> transitions = new ArrayList<>();
    for (int i=0; i<names.length; i++) {
      transitions.add(new RestTransition("t-" + i, names[i]));
    }

    when(restApi.getTransitions(issue)).thenReturn(transitions);
  }

}
