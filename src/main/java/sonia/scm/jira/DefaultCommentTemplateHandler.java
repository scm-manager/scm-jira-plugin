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



package sonia.scm.jira;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.issuetracker.IssueRequest;
import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.template.Template;
import sonia.scm.template.TemplateEngine;
import sonia.scm.template.TemplateEngineFactory;
import sonia.scm.user.User;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.StringWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * The DefaultCommentTemplateHandler uses the default {@link TemplateEngine} to 
 * render the jira comments.
 *
 * @author Sebastian Sdorra
 */
public class DefaultCommentTemplateHandler implements CommentTemplateHandler
{
  private static final Logger logger = LoggerFactory.getLogger(DefaultCommentTemplateHandler.class);

  private static final String UNIX_LINE_SEPARATOR = "\n";
  private static final String LINE_SEPARATOR = System.getProperty("line.separator", UNIX_LINE_SEPARATOR);

  /** env var for changeset */
  private static final String ENV_CHANGESET = "changeset";

  /** Field description */
  private static final String ENV_DESCRIPTION_LINE = "descriptionLine";

  /** env var for diff url */
  private static final String ENV_DIFFURL = "diffUrl";

  /** env var for repository */
  private static final String ENV_REPOSITORY = "repository";

  /** env var for repository url */
  private static final String ENV_REPOSITORYURL = "repositoryUrl";

  private static final String ENV_COMMENTWRAP_PRE = "commentWrapPre";
  private static final String ENV_COMMENTWRAP_POST = "commentWrapPost";

  private static final String ENV_BRANCHES = "branches";
  private static final String ENV_BOOKMARKS = "bookmarks";

  private static final String ENV_AUTHOR = "author";
  private static final String ENV_COMMITTER = "committer";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param templateEngineFactory
   * @param linkHandler
   */
  @Inject
  public DefaultCommentTemplateHandler(
    TemplateEngineFactory templateEngineFactory, LinkHandler linkHandler)
  {
    this.templateEngineFactory = templateEngineFactory;
    this.linkHandler = linkHandler;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> createBaseEnvironment(JiraIssueRequest request,
    Changeset changeset)
  {
    Map<String, Object> env = Maps.newHashMap();

    Repository repository = request.getRepository();

    env.put(ENV_REPOSITORY, repository);
    env.put(ENV_CHANGESET, changeset);
    // Mustache is pretty annoying, in that it escapes HTML.  Thus any lovely line-feeds in the changeset
    // description get eaten, and don't show up in Jira.  Thus, we split the description by the line separator,
    // and make mustache put each line on its own line.
    env.put(ENV_DESCRIPTION_LINE, splitIntoLines(changeset));
    String author = changeset.getAuthor().getName();
    env.put(ENV_AUTHOR, author);
    Optional<String> committer = request.getCommitter();
    if (committer.isPresent() && !author.equals(committer.get())) {
      env.put(ENV_COMMITTER, committer.get());
    }
    IssueRequest issueRequest = new IssueRequest(repository, changeset, Collections.emptyList(), null);
    env.put(ENV_DIFFURL, linkHandler.getDiffUrl(issueRequest));
    env.put(ENV_REPOSITORYURL, linkHandler.getRepositoryUrl(issueRequest));
    env.put(ENV_BRANCHES, changeset.getBranches()); // TODO:  Mercurial has empty branches for "default" ...
    env.put(ENV_BOOKMARKS, changeset.getProperty("hg.bookmarks"));

    final JiraConfiguration configuration = request.getConfiguration();
    final boolean commentMonospace = configuration.getCommentMonospace();
    final String commentWrap = Strings.nullToEmpty(configuration.getCommentWrap());

    final String commentWrapPre = (commentMonospace ? "{{" : "") + commentWrap;
    final String commentWrapPost = commentWrap + (commentMonospace ? "}}" : "");

    env.put(ENV_COMMENTWRAP_PRE, commentWrapPre);
    env.put(ENV_COMMENTWRAP_POST, commentWrapPost);

    return env;
  }

  private List<String> splitIntoLines(Changeset changeset) {
    List<String> lines = splitDescriptionWith(changeset, getSystemLineSeparator());
    if (descriptionMayHaveOtherLineSeparatorThanConfigured(lines)) {
      return splitDescriptionWith(changeset, UNIX_LINE_SEPARATOR);
    }
    return lines;
  }

  private List<String> splitDescriptionWith(Changeset changeset, String separator) {
    return Arrays.asList(changeset.getDescription().split(separator));
  }

  private boolean descriptionMayHaveOtherLineSeparatorThanConfigured(List<String> lines) {
    return lines.size() == 1 && !UNIX_LINE_SEPARATOR.equals(getSystemLineSeparator());
  }

  String getSystemLineSeparator() {
    return LINE_SEPARATOR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String render(CommentTemplate tpl, Object environment)
    throws IOException
  {
    TemplateEngine engine = templateEngineFactory.getDefaultEngine();
    Template template = engine.getTemplate(tpl.getResource());

    StringWriter writer = new StringWriter();

    template.execute(writer, environment);

    return writer.toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** link handler */
  private final LinkHandler linkHandler;

  /** template engine factory */
  private final TemplateEngineFactory templateEngineFactory;
}
