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
import { apiClient, Configuration } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";
import JiraConfigurationItems from "./JiraConfigurationItems";

type Props = WithTranslation & {
  initialConfiguration: Configuration;
  readOnly: boolean;
  onConfigurationChange: (p1: Configuration, p2: boolean) => void;
  includeGlobalConfigItem: boolean;
};

type State = {
  resubmitSend: boolean;
  resubmitError: boolean;
};

class JiraConfigurationForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      resubmitSend: false,
      resubmitError: false
    };
  }

  resubmitHandler = () => {
    const resubmitLink = this.props.initialConfiguration._links.resubmit.href;
    apiClient
      .post(resubmitLink, {})
      .then(() => {
        this.setState({
          resubmitSend: true,
          resubmitError: false
        });
      })
      .catch(() => {
        this.setState({
          resubmitError: true,
          resubmitSend: false
        });
      });
  };

  renderResubmitMessage = () => {
    const { resubmitSend, resubmitError } = this.state;
    if (resubmitSend) {
      return this.renderResubmitDiv("resubmitSuccess");
    } else if (resubmitError) {
      return this.renderResubmitDiv("resubmitFailed");
    } else {
      return null;
    }
  };

  renderResubmitDiv = (key: string) => {
    return (
      <div className="notification is-info">
        <button
          className="delete"
          onClick={() =>
            this.setState({
              resubmitError: false,
              resubmitSend: false
            })
          }
        />
        {this.props.t("scm-jira-plugin.form." + key)}
      </div>
    );
  };

  render() {
    const { readOnly, initialConfiguration, onConfigurationChange, includeGlobalConfigItem } = this.props;
    return (
      <>
        {this.renderResubmitMessage()}
        <JiraConfigurationItems
          initialConfiguration={initialConfiguration}
          readOnly={readOnly}
          includeGlobalConfigItem={includeGlobalConfigItem}
          onConfigurationChange={onConfigurationChange}
          resubmitHandler={this.resubmitHandler}
        />
      </>
    );
  }
}

export default withTranslation("plugins")(JiraConfigurationForm);
