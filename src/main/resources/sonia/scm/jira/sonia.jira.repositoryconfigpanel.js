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
Ext.ns('Sonia.jira');

Sonia.jira.RepositoryConfigPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {
  
  initComponent: function(){
    var config = {
      title: Sonia.jira.I18n.formTitleText,
      items: [{
        name: 'jiraUrl',
        fieldLabel: Sonia.jira.I18n.urlText,
        property: 'jira.url',
        vtype: 'url',
        helpText: Sonia.jira.I18n.urlHelpText
      },{
        id: 'jiraUpdateIssues',
        name: 'jiraUpdateIssues',
        xtype: 'checkbox',
        fieldLabel: Sonia.jira.I18n.updateIssuesText,
        property: 'jira.update-issues',
        helpText: Sonia.jira.I18n.updateIssuesHelpText,
        listeners: {
          check: {
            fn: this.toggleUpdateIssues,
            scope: this
          }
        }
      },{
        id: 'jiraAutoClose',
        name: 'jiraAutoClose',
        xtype: 'checkbox',
        fieldLabel: Sonia.jira.I18n.autoCloseText,
        property: 'jira.auto-close',
        helpText: Sonia.jira.I18n.autoCloseHelpText,
        listeners : {
          check : {
            fn: this.toggleAutoClose,
            scope: this
          }
        }
      },{
        id: 'jiraAutoCloseWords',
        name: 'jiraAutoCloseWords',
        property: 'jira.auto-close-words',
        fieldLabel: Sonia.jira.I18n.autoCloseWordsText,
        helpText: Sonia.jira.I18n.autoCloseWordsHelpText,
        value: Sonia.jira.I18n.autoCloseDefaultValues
      },{
        id: 'jiraRoleLevel',
        name: 'role-level',
        property: 'jira.role-level',
        fieldLabel: Sonia.jira.I18n.roleLevelText,
        helpText: Sonia.jira.I18n.roleLevelHelpText
      },{
        id: 'jiraCommentPrefix',
        name: 'comment-prefix',
        property: 'jira.comment-prefix',
        fieldLabel: Sonia.jira.I18n.commentPrefixText,
        helpText: Sonia.jira.I18n.commentPrefixHelpText
      },{
        id: 'jiraUsername',
        name: 'jiraUsername',
        fieldLabel: Sonia.jira.I18n.usernameText,
        property: 'jira.username',
        helpText: Sonia.jira.I18n.usernameHelpText
      },{
        id: 'jiraPassword',
        name: 'jiraPassword',
        fieldLabel: Sonia.jira.I18n.passwordText,
        property: 'jira.password',
        inputType: 'password',
        helpText: Sonia.jira.I18n.passwordHelpText
      }]
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.jira.RepositoryConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  loadExtraProperties: function(item){
    var cmp = Ext.getCmp('jiraUpdateIssues');
    this.toggleUpdateIssues(cmp);
  },
  
  toggleUpdateIssues: function(checkbox){
    var autoclose = Ext.getCmp('jiraAutoClose');
    
    var cmps = [
      autoclose,
      Ext.getCmp('jiraRoleLevel'), 
      Ext.getCmp('jiraUsername'), 
      Ext.getCmp('jiraPassword') 
    ];
    
    Sonia.jira.toggleFields(cmps, checkbox);

    if ( ! checkbox.getValue()){
      autoclose.setValue(false);
    }
    this.toggleAutoClose(autoclose);
  },

  toggleAutoClose : function(checkbox) {
    var cmps = [ 
      Ext.getCmp('jiraAutoCloseWords') 
    ];
    Sonia.jira.toggleFields(cmps, checkbox);
  }
  
});

// register xtype
Ext.reg("jiraRepositoryConfigPanel", Sonia.jira.RepositoryConfigPanel);

// register panel
Sonia.repository.openListeners.push(function(repository, panels){
  if (Sonia.repository.isOwner(repository)){
    panels.push({
      xtype: 'jiraRepositoryConfigPanel',
      item: repository
    });
  }
});

