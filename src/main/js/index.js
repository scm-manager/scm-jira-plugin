// @flow

import {ConfigurationBinder as cfgBinder} from "@scm-manager/ui-components"
import GlobalJiraConfiguration from "./GlobalJiraConfiguration";
import LocalJiraConfiguration from "./LocalJiraConfiguration";

cfgBinder.bindGlobal("/jira", "scm-jira-plugin.global.nav-link", "jiraConfig", GlobalJiraConfiguration);
cfgBinder.bindRepository("/jira", "scm-jira-plugin.local.nav-link", "jiraConfig", LocalJiraConfiguration);
