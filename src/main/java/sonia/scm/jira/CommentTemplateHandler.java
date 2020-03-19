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


package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.repository.Changeset;

import java.io.IOException;
import java.util.Map;

/**
 * The CommentTemplateHandler renders templates.
 *
 * @author Sebastian Sdorra
 */
public interface CommentTemplateHandler
{

  /**
   * Returns the rendered template.
   *
   * @param model model which is used within the template
   *
   * @return rendered template
   *
   * @throws IOException
   */
  String render(Object model) throws IOException;

  /**
   * Creates a base environment for the rendering of a {@link CommentTemplate}.
   *
   * @param request jira issue request
   * @param changeset changeset
   *
   * @return
   */
  Map<String, Object> createBaseEnvironment(JiraIssueRequest request, Changeset changeset);
}
