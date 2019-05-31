//@flow
import React from "react";
import {translate} from "react-i18next";
import {withRouter} from "react-router-dom";
import {apiClient, Notification, Page} from "@scm-manager/ui-components";

type Props = {
  id: string,
  link: string,
  t: string => string,
  match: any
}

type State = {
  loading: boolean,
  success: boolean,
  error?: Error
}

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
      this.setState({loading: false, success: false})
    } else if (this.state.loading) {
      apiClient
        .get(link)
        .then(response => response.json())
        .then(result => {
          const removeLink = result._links.removeComment.href;
          apiClient
            .delete(removeLink.replace("{id}", id))
            .then(() => this.setState({loading: false, success: true}))
            .catch(error => this.setState({error}));
          }
        )
        .catch(error => this.setState({error}));
    }
  }

  render() {
    const {t} = this.props;
    const {loading, success} = this.state;

    const notification = success ?
      <Notification type="success">
        {t("scm-jira-plugin.remove.success")}
      </Notification>
      : <Notification type="warning">
        {t("scm-jira-plugin.remove.failure")}
      </Notification>;

    return <Page loading={loading} title={t("scm-jira-plugin.remove.title")}>
      {loading? null: notification}
    </Page>;
  }
}

export default withRouter(translate("plugins")(RemoveCommentPage));
