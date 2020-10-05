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
import { ConfigurationBinder as cfgBinder, ProtectedRoute } from "@scm-manager/ui-components";
import GlobalJiraConfiguration from "./GlobalJiraConfiguration";
import LocalJiraConfiguration from "./LocalJiraConfiguration";
import RemoveCommentPage from "./RemoveCommentPage";
import { binder } from "@scm-manager/ui-extensions";
import React from "react";
import { Links } from "@scm-manager/ui-types";
import CommitMessageIssueKeyValidatorConfig from "./CommitMessageIssueKeyValidatorConfig";

type RouteProps = {
  authenticated: boolean;
  links: Links;
};

cfgBinder.bindGlobal("/jira", "scm-jira-plugin.global.nav-link", "jiraConfig", GlobalJiraConfiguration);
cfgBinder.bindRepositorySetting("/jira", "scm-jira-plugin.local.nav-link", "jiraConfig", LocalJiraConfiguration);

const JiraCommentRoute = ({ authenticated, links }: RouteProps) => {
  return (
    <>
      <ProtectedRoute
        path="/jira/resubmit/comment/:id/remove"
        component={() => <RemoveCommentPage link={links.jiraConfig ? links.jiraConfig.href : null} />}
        authenticated={authenticated}
      />
    </>
  );
};

binder.bind("main.route", JiraCommentRoute);
binder.bind("commitMessageChecker.validator.CommitMessageIssueKeyValidator", CommitMessageIssueKeyValidatorConfig);
