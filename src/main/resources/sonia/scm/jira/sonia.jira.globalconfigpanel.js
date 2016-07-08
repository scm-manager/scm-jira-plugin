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

Sonia.jira.GlobalConfigPanel = Ext.extend(Sonia.config.ConfigForm, {

  initComponent: function(){

    var config = {
      title : Sonia.jira.I18n.titleText,
      items : [{
        xtype : 'textfield',
        fieldLabel : Sonia.jira.I18n.urlText,
        name : 'url',
        vtype: 'url',
        allowBlank : true,
        helpText: Sonia.jira.I18n.urlHelpText
      },{
        xtype: 'checkbox',
        fieldLabel : Sonia.jira.I18n.repositoryConfigurationText,
        name: 'disable-repository-configuration',
        inputValue: 'true',
        helpText: Sonia.jira.I18n.repositoryConfigurationHelpText
      },{
        id: 'updateIssues',
        name: 'update-issues',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.jira.I18n.updateIssuesText,
        helpText: Sonia.jira.I18n.updateIssuesHelpText,
        listeners: {
          check : {
            scope: this,
            fn: this.toggleUpdateIssues
          }
        }
      },{
        id: 'autoClose',
        name: 'auto-close',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.jira.I18n.autoCloseText,
        helpText: Sonia.jira.I18n.autoCloseHelpText,
        listeners : {
          check : {
            scope: this,
            fn: this.toggleAutoClose
          }
        }
      },{
        id: 'autoCloseWords',
        name: 'auto-close-words',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.autoCloseWordsText,
        helpText: Sonia.jira.I18n.autoCloseWordsHelpText,
        value: Sonia.jira.I18n.autoCloseDefaultValues
      },{
        id: 'roleLevel',
        name: 'role-level',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.roleLevelText,
        helpText: Sonia.jira.I18n.roleLevelHelpText
      },{
        id: 'commentPrefix',
        name: 'comment-prefix',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.commentPrefixText,
        helpText: Sonia.jira.I18n.commentPrefixHelpText
      },{
        id: 'username',
        name: 'username',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.usernameText,
        helpText: Sonia.jira.I18n.usernameHelpText
      },{
        id: 'password',
        name: 'password',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.passwordText,
        inputType: 'password',
        helpText: Sonia.jira.I18n.passwordHelpText
      },{
        id: 'resubmission',
        name: 'resubmission',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.jira.I18n.resubmissionText,
        helpText: Sonia.jira.I18n.resubmissionHelpText,
        listeners: {
          check: {
            fn: this.toggleResubmission,
            scope: this
          }
        }
      },{
        id: 'rest-api-enabled',
        name: 'rest-api-enabled',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.jira.I18n.restApiEnabledText,
        helpText: Sonia.jira.I18n.restApiEnabledHelpText
      },{
        id: 'mail',
        name: 'mail-error-address',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.mailText,
        helpText: Sonia.jira.I18n.mailHelpText
      },{
        id: 'commentWrap',
        name: 'comment-wrap',
        xtype: 'textfield',
        fieldLabel: Sonia.jira.I18n.commentWrapText,
        helpText: Sonia.jira.I18n.commentWrapHelpText
      },{
        name: 'comment-monospace',
        inputValue: 'true',
        xtype: 'checkbox',
        fieldLabel: Sonia.jira.I18n.commentMonospaceText,
        helpText: Sonia.jira.I18n.commentMonospaceHelpText
      },{
        id: 'resubmit',
        name: 'resubmitButton',
        xtype: 'button',
        handler: function(){
          Sonia.jira.resubmit(this.el, this, 'plugins/jira/resubmit/all.json');
        },
        scope: this,
        text: Sonia.jira.I18n.resubmitText,
        fieldLabel: Sonia.jira.I18n.resubmitText, 
        helpText: Sonia.jira.I18n.resubmitHelpText
      }]
    };

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.jira.GlobalConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  toggleUpdateIssues: function(checkbox){
    var autoclose = Ext.getCmp('autoClose');
    var resubmission = Ext.getCmp('resubmission');
    
    var cmps = [ 
      autoclose,
      Ext.getCmp('roleLevel'),
      Ext.getCmp('commentPrefix'),
      Ext.getCmp('username'), 
      Ext.getCmp('password'),
      resubmission
    ];
    Sonia.jira.toggleFields(cmps, checkbox);

    if ( ! checkbox.getValue()){
      autoclose.setValue(false);
      resubmission.setValue(false);
    }
    this.toggleAutoClose(autoclose);
    this.toggleResubmission(resubmission);
  },
  
  toggleAutoClose : function(checkbox) {
    var cmps = [ 
      Ext.getCmp('autoCloseWords') 
    ];
    Sonia.jira.toggleFields(cmps, checkbox);
  },
  
  toggleResubmission: function(checkbox){
    var cmps = [ 
      Ext.getCmp('mail')
    ];
    Sonia.jira.toggleFields(cmps, checkbox);    
  },

  onSubmit: function(values){
    this.el.mask(this.submitText);
    Ext.Ajax.request({
      url: restUrl + 'plugins/jira/global-config.json',
      method: 'POST',
      jsonData: values,
      scope: this,
      disableCaching: true,
      success: function(response){
        this.el.unmask();
      },
      failure: function(){
        this.el.unmask();
      }
    });
  },

  onLoad: function(el){
    var tid = setTimeout( function(){
      el.mask(this.loadingText);
    }, 100);
    Ext.Ajax.request({
      url: restUrl + 'plugins/jira/global-config.json',
      method: 'GET',
      scope: this,
      disableCaching: true,
      success: function(response){
        var obj = Ext.decode(response.responseText);
        this.load(obj);
        
        // toggle fields
        var cmp = Ext.getCmp('updateIssues');
        this.toggleUpdateIssues(cmp);
        
        clearTimeout(tid);
        el.unmask();
      },
      failure: function(){
        el.unmask();
        clearTimeout(tid);
        alert('failure');
      }
    });
  }

});

// register xtype
Ext.reg("jiraGlobalConfigPanel", Sonia.jira.GlobalConfigPanel);

// regist config panel
registerGeneralConfigPanel({
  id: 'jiraGlobalConfigPanel',
  xtype: 'jiraGlobalConfigPanel'
});