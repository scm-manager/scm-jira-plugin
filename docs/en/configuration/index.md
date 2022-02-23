---
title: Configuration
---

As usual in SCM Manager 2, there is a global and a repository specific configuration for the Jira plugin.
The global configuration applies to all repositories that do not have a specific configuration stored.
The only difference between the configurations is that the repository-specific configuration can be deactivated in the global configuration.

### Configuration form
To connect the SCM-Manager to a Jira instance. the Jira instance url including the context path is required.
If the filter field is left empty, SCM Manager will find issue ids from all Jira projects.
This can be restricted via the filter field, where a comma-separated list of Jira project abbreviations can be entered.

#### Create comments
To create comments in Jira, credentials are required, which should belong to a technical Jira user.
This user needs sufficient permissions to create comments on existing issues.

The comments are created on the Jira issue as soon as the issue id is mentioned within a commit message, a pull request,
or a pull request comment.

Example Commit Message: "SCM-42 Add awesome new feature".

This will generate a comment with this commit message on the Jira issue SCM-42.

#### Issue status transitions
To change the status of an issue via a commit message or a pull request,
an issue id can be used with a Jira status transition in the same sentence.

Example commit message: "Bug SCM-42 is done".

The example sets the status of issue SCM-42 to "Done".
Of course, this assumes that there is a "Done" status transition on issue SCM-42.

The "Status Modification Words" can be used to define words that can be used instead of the Jira status transition names.
These keywords can be specified in the form of a comma-separated list.
For example, for the status "Done" you could specify the following keywords: "closes, closing".
Thus, the text "Closes Bug SCM-42" would also set the issues SCM-42 to the status "Done".

If the status should only be updated due to pull requests and not by commits, the additional option "Disable issue
state changes by commits" can be selected.

> **Important:** The configured Jira user needs permissions to change the status of issues.

![Jira Configuration](assets/config.png)
