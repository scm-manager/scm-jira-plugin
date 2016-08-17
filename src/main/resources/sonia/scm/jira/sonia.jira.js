/*
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
Ext.ns("Sonia.jira");

Sonia.jira.I18n = {
  
  titleText: 'Jira Configuration',
  formTitleText: 'Jira',
  
  urlText: 'Url',
  urlHelpText: 'Url of Jira installation (with contextpath).',
  
  repositoryConfigurationText: 'Do not allow repository configuration',
  repositoryConfigurationHelpText: 'Do not allow repository owners to configure jira instances. \n\
    You have to restart your application server after changing this value.',
  
  autoCloseText: 'Enable Status Modification',
  autoCloseHelpText: 'Enable the status modification function. SCM-Manager searches for \n\
                      issue keys and status modification words in commit messages. If \n\
                      both are found in a message SCM-Manager changes the status of the issue. \n\
                      <b>Note:</b> If username and password are different \n\
                      in SCM-Manager and Jira, it is necessary to configure \n\
                      the username and password below.',

  autoCloseDefaultValues: 'fix=done, reopen=reopen and start progress, start=start progress',
  
  updateIssuesText: 'Update Jira Issues',
  updateIssuesHelpText: 'Enable the automatic update function. SCM-Manager searches for\n\
                         issue keys in commit messages. If a issue id is found SCM-Manager\n\
                         updates the issue with a comment. <b>Note:</b> If username and \n\
                         password are different in SCM-Manager and Jira, it is necessary \n\
                         to configure the username and password below.',
  
  autoCloseWordsText: 'Status Modification Words',
  autoCloseWordsHelpText: 'Comma separated list of terms to enable the status modification function. \n\
                           Add the transitions name (e.g. start progress, stop progress, ...) or configure a mapping \n\
                           (e.g. start = start progress, beginn = start progress, stop = stop progress,...). The \n\
                           transition names depend on your jira workflow. \n\
                           Each commit message of a changeset gets searched for these words.',
          
  roleLevelText: 'Role visibility',
  roleLevelHelpText: 'Defines for which Project Role the comments are visible. <b>Note:</b> The name must be a valid jira role name.',
  
  commentPrefixText: 'Comment prefix',
  commentPrefixHelpText: 'The comment prefix is created at the beginning of every comment in Jira created by SCM-Manager. The default prefix is [SCM].',
  
  filterText: 'Project Filter',
  filterHelpText: 'Filters for jira project key. Multiple filters seperated by ",". e.g.: SCM,TST,ASD',
  
  usernameText: 'Username',
  usernameHelpText: 'Jira username for connection. Leave this field empty to create the connection\n\
                     with the credentials of the user which is logged in to SCM-Manager.',
  
  passwordText: 'Password',
  passwordHelpText: 'Jira password for connection.',
  
  resubmissionText: 'Enable resubmission',
  resubmissionHelpText: 'By enabling this option SCM-Manager stores the comments for resubmission in case the Jira server is not available. An information about the failed submission will be sent to the e-mail configured below.',
    
  mailText: 'E-Mail',
  mailHelpText: 'The mail address to send a message to if a jira comment fails.',
  
  resubmitText: 'Resubmit stored comments',
  resubmitHelpText: 'Resubmit all stored Jira comments.',
  
  resubmitWaitText: 'Resubmit comments ...',
  resubmitFailureTitleText: 'Resubmit failed',
  resubmitFailureDescriptionText: 'Unknown error occurred durring comment resubmission. SCM-Manager returned error code {0}.',
  
  restApiEnabledText: 'Rest API v2',
  restApiEnabledHelpText: 'Use Jira Rest API v2 to communicate with Jira.',


  commentWrapText: 'Wrap Comment With',
  commentWrapHelpText: 'Text to place around the changeset description in the Jira comment. The default is no wrapping.  Examples:  {quote} or {noformat}.  See https://jira.atlassian.com/secure/WikiRendererHelpAction.jspa?section=all',

  commentMonospaceText: 'Monospace',
  commentMonospaceHelpText: 'Should the comment include the mono-space wrapper?  {{ }}.  Note:  Jira handling for this is not consistant.'
};

Sonia.jira.toggleFields = function(cmps, scope){
  Ext.each(cmps, function(cmp){
    var checked = this.getValue();
    // If cmp is a checkbox, use enable/disable.
    if (cmp.getXType() === "checkbox") {
      if (!checked) {
        cmp.disable();
      } else {
        cmp.enable();
      }
    } else {
      // Add/remove CSS class which indicates disabling.
      if (!checked ) {
        if ( ! cmp.readOnly ){
          cmp.addClass('x-item-disabled');
        }
      } else {
        cmp.removeClass('x-item-disabled');
      }
      
      cmp.setReadOnly(!checked);
    }
  }, scope);
};

Sonia.jira.resubmit = function(el, scope, url){
  var tid = setTimeout(function(){
    el.mask(Sonia.jira.I18n.resubmitWaitText);
  }, 100);
  Ext.Ajax.request({
    url: restUrl + url,
    method: 'POST',
    jsonData: '',
    scope: scope,
    disableCaching: true,
    success: function(){
      // clear loading mask timeout
      clearTimeout(tid);
      // remove loading mask
      el.unmask();
    },
    failure: function(result){
      // clear loading mask timeout
      clearTimeout(tid);
      // remove loading mask
      el.unmask();
      
      // display error message
      main.handleFailure(
        result.status,
        Sonia.jira.I18n.resubmitFailureTitleText,
        Sonia.jira.I18n.resubmitFailureDescriptionText
      );
    }
  });
};