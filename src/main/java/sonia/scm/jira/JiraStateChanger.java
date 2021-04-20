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

import sonia.scm.issuetracker.spi.StateChanger;
import sonia.scm.jira.rest.RestApi;
import sonia.scm.jira.rest.RestTransition;
import sonia.scm.jira.rest.RestTransitions;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

public class JiraStateChanger implements StateChanger {

  private final RestApi restApi;

  public JiraStateChanger(RestApi restApi, JiraConfiguration configuration) {
    this.restApi = restApi;
  }

  @Override
  public void changeState(String issueKey, String keyWord) throws IOException {
    String transitionId = findTransition(issueKey, keyWord);
    restApi.changeState(issueKey, transitionId);
  }

  private String findTransition(String issueKey, String keyWord) throws IOException {
    return restApi.getTransitions(issueKey)
      .getTransitions()
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
    return restApi.getTransitions(issueKey)
      .getTransitions()
      .stream()
      .map(RestTransition::getName)
      .collect(Collectors.toSet());
  }
}
