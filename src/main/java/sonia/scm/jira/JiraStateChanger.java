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

import com.google.common.base.Splitter;
import sonia.scm.issuetracker.spi.StateChanger;
import sonia.scm.jira.config.JiraConfiguration;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.jira.rest.RestTransition;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class JiraStateChanger implements StateChanger {

  private final RestApi restApi;
  private final JiraConfiguration configuration;

  public JiraStateChanger(RestApi restApi, JiraConfiguration configuration) {
    this.restApi = restApi;
    this.configuration = configuration;
  }

  @Override
  public void changeState(String issueKey, String keyWord) throws IOException {
    String transitionId = findTransition(issueKey, createMapping().getOrDefault(keyWord, keyWord));
    restApi.changeState(issueKey, transitionId);
  }

  private String findTransition(String issueKey, String keyWord) throws IOException {
    return restApi.getTransitions(issueKey)
      .stream()
      .filter(transition -> keyWord.equalsIgnoreCase(transition.getName()))
      .map(RestTransition::getId)
      .findFirst()
      .orElseThrow(
        () -> new JiraException("could not find transition with name " + keyWord + " at issue " + issueKey)
      );
  }

  @Override
  public Iterable<String> getKeyWords(String issueKey) throws IOException {
    Set<String> keyWords = new HashSet<>(createMapping().keySet());

    restApi.getTransitions(issueKey)
      .stream()
      .map(RestTransition::getName)
      .map(name -> name.toLowerCase(Locale.ENGLISH))
      .forEach(keyWords::add);

    return keyWords;
  }

  @Override
  public boolean isStateChangeActivatedForCommits() {
    return !configuration.isDisableStateChangeByCommit();
  }

  private Map<String,String> createMapping() {
    Map<String, String> mapping = new HashMap<>();
    for (Map.Entry<String,String> e : configuration.getAutoCloseWords().entrySet()) {
      for (String s : Splitter.on(',').omitEmptyStrings().trimResults().splitToList(e.getKey())) {
        mapping.put(s.toLowerCase(Locale.ENGLISH), e.getValue().toLowerCase(Locale.ENGLISH));
      }
    }
    return mapping;
  }
}
