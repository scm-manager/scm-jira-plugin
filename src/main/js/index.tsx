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

import { ConfigurationBinder as cfgBinder} from "@scm-manager/ui-components";
import GlobalJiraConfiguration from "./GlobalJiraConfiguration";
import LocalJiraConfiguration from "./LocalJiraConfiguration";
import { binder } from "@scm-manager/ui-extensions";
import React from "react";
import JiraCommitMessageIssueKeyValidatorConfig from "./JiraCommitMessageIssueKeyValidatorConfig";

cfgBinder.bindGlobal("/jira", "scm-jira-plugin.global.nav-link", "jiraConfig", GlobalJiraConfiguration);
cfgBinder.bindRepositorySetting("/jira", "scm-jira-plugin.local.nav-link", "jiraConfig", LocalJiraConfiguration);

binder.bind("commitMessageChecker.validator.JiraCommitMessageIssueKeyValidator", JiraCommitMessageIssueKeyValidatorConfig);
