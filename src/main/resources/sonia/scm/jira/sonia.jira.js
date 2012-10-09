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
  
  autoCloseText: 'Enable Auto-Close',
  autoCloseHelpText: 'Enables the auto close function. SCM-Manager searches for \n\
                      issue keys and auto close words in commit messages. If \n\
                      both found in a message SCM-Manager closes the issue in \n\
                      the jira server. <strong>Note:</strong> It is necessary \n\
                      that users have the same name and password in SCM-Manager and Jira.',
  autoCloseDefaultValues: 'fixed, fix, closed, close, resolved, resolve',
  
  updateIssuesText: 'Update Jira Issues',
  updateIssuesHelpText: 'Enable the automatic update function. SCM-Manager searches for\n\
                         issue keys in commit messages. If a issue id is found SCM-Manager\n\
                         updates the issue with a comment. <strong>Note:</strong> It \n\
                         is necessary that users have the same name and password in SCM-Manager \n\
                         and Jira.',
  
  autoCloseWordsText: 'Auto-Close Words',
  autoCloseWordsHelpText: 'Comma separated list of words to enable the auto close function. \n\
                           Each commit message of a changeset is being searched for these words.'
  
}

Sonia.jira.toggleFields = function(cmps, scope){
  Ext.each(cmps, function(cmp){
      cmp.setReadOnly(!this.checked);
    if (!this.checked ){
      cmp.addClass('x-item-disabled');
    } else {
      cmp.removeClass('x-item-disabled');
    }
  }, scope);
}