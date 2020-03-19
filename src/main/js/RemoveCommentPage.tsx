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
import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import { withRouter } from "react-router-dom";
import { apiClient, Notification, Page } from "@scm-manager/ui-components";

type Props = WithTranslation & {
  id: string;
  link: string;
  match: any;
};

type State = {
  loading: boolean;
  success: boolean;
  error?: Error;
};

class RemoveCommentPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true,
      success: false
    };
  }

  componentDidMount() {
    const id = this.props.match.params.id;
    const { link } = this.props;

    if (!link) {
      this.setState({
        loading: false,
        success: false
      });
    } else if (this.state.loading) {
      apiClient
        .get(link)
        .then(response => response.json())
        .then(result => {
          const removeLink = result._links.removeComment.href;
          apiClient
            .delete(removeLink.replace("{id}", id))
            .then(() =>
              this.setState({
                loading: false,
                success: true
              })
            )
            .catch(error =>
              this.setState({
                error
              })
            );
        })
        .catch(error =>
          this.setState({
            error
          })
        );
    }
  }

  render() {
    const { t } = this.props;
    const { loading, success } = this.state;

    const notification = success ? (
      <Notification type="success">{t("scm-jira-plugin.remove.success")}</Notification>
    ) : (
      <Notification type="warning">{t("scm-jira-plugin.remove.failure")}</Notification>
    );

    return (
      <Page loading={loading} title={t("scm-jira-plugin.remove.title")}>
        {loading ? null : notification}
      </Page>
    );
  }
}

export default withRouter(withTranslation("plugins")(RemoveCommentPage));
