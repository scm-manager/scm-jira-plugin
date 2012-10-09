/* *
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
        name: 'update-issues',
        xtype: 'checkbox',
        fieldLabel: Sonia.jira.I18n.updateIssuesText,
        helpText: Sonia.jira.I18n.updateIssuesHelpText,
        listeners: {
          check: this.toggleUpdateIssues
        }
      },{
        id: 'autoClose',
        name: 'auto-close',
        xtype: 'checkbox',
        fieldLabel: Sonia.jira.I18n.autoCloseText,
        helpText: Sonia.jira.I18n.autoCloseHelpText
      },{
        id: 'autoCloseWords',
        name: 'auto-close-words',
        fieldLabel: this.autoCloseWordsText,
        helpText: Sonia.jira.I18n.autoCloseWordsHelpText,
        value: Sonia.jira.I18n.autoCloseDefaultValues
      }]
    }

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.jira.GlobalConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  toggleUpdateIssues: function(){
    var cmps = [
      Ext.getCmp( 'autoClose' ),
      Ext.getCmp( 'autoCloseWords' )
    ];
    
    Sonia.jira.toggleFields(cmps);
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