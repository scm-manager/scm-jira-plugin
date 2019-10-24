import { ConfigurationBinder as cfgBinder, ProtectedRoute } from "@scm-manager/ui-components";
import GlobalJiraConfiguration from "./GlobalJiraConfiguration";
import LocalJiraConfiguration from "./LocalJiraConfiguration";
import RemoveCommentPage from "./RemoveCommentPage";
import { binder } from "@scm-manager/ui-extensions";
import React from "react";
import { Links } from "@scm-manager/ui-types";

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
