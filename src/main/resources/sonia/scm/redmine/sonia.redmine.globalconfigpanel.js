/* *
 * Copyright (c) 2015, Sebastian Sdorra
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
 * https://bitbucket.org/sdorra/scm-manager
 * 
 */

Ext.ns('Sonia.redmine');

Sonia.redmine.GlobalConfigPanel = Ext.extend(Sonia.config.ConfigForm, {

  initComponent: function(){

    var config = {
      title: Sonia.redmine.I18n.formTitleText,
      items: [{
        name: 'url',
        xtype : 'textfield',
        fieldLabel: Sonia.redmine.I18n.urlText,
        vtype: 'urlsimple',
        helpText: Sonia.redmine.I18n.urlHelpText
      },{
        id: 'redmineGlobalUpdateIssues',
        name: 'updateIssues',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.redmine.I18n.updateIssuesText,
        helpText: Sonia.redmine.I18n.updateIssuesHelpText,
        listeners: {
          check: {
            fn: this.toggleUpdateIssues,
            scope: this
          }
        }
      },{
        id: 'redmineGlobalAutoClose',
        name: 'autoClose',
        xtype: 'checkbox',
        inputValue: 'true',
        fieldLabel: Sonia.redmine.I18n.autoCloseText,
        helpText: Sonia.redmine.I18n.autoCloseHelpText,
        listeners: {
          check: {
            fn: this.toggleAutoClose,
            scope: this
          }
        }
      },{
        id: 'redmineGlobalAutoCloseWords',
        name: 'autoCloseWords',
        xtype : 'textfield',
        fieldLabel: Sonia.redmine.I18n.autoCloseWordsText,
        helpText: Sonia.redmine.I18n.autoCloseWordsHelpText,
        value: 'fixed, fix, closed, close, resolved, resolve'
      },{
        id: 'redmineGlobalUsernameTransformer',
        name: 'usernameTransformPattern',
        xtype : 'textfield',
        fieldLabel: Sonia.redmine.I18n.usernameTransformerText,
        helpText: Sonia.redmine.I18n.usernameTransformerHelpText,
        value: '{0}'
      },{
        xtype: 'checkbox',
        fieldLabel : Sonia.redmine.I18n.repositoryConfigurationText,
        name: 'disableRepositoryConfiguration',
        inputValue: 'true',
        helpText: Sonia.redmine.I18n.repositoryConfigurationHelpText
      }]
  };

  Ext.apply(this, Ext.apply(this.initialConfig, config));
  Sonia.redmine.GlobalConfigPanel.superclass.initComponent.apply(this, arguments);
},

onSubmit: function(values){
  this.el.mask(this.submitText);
  Ext.Ajax.request({
    url: restUrl + 'plugins/redmine/global-config.json',
    method: 'POST',
    jsonData: values,
    scope: this,
    disableCaching: true,
    success: function(){
      this.el.unmask();
    },
    failure: function(){
      this.el.unmask();
      Ext.MessageBox.show({
        title: Sonia.redmine.I18n.errorBoxTitle,
        msg: Sonia.redmine.I18n.errorOnSubmitText,
        buttons: Ext.MessageBox.OK,
        icon: Ext.MessageBox.ERROR
      });
    }
  });
},

onLoad: function(el){
  var tid = setTimeout( function(){
    el.mask(this.loadingText);
  }, 100);
  Ext.Ajax.request({
    url: restUrl + 'plugins/redmine/global-config.json',
    method: 'GET',
    scope: this,
    disableCaching: true,
    success: function(response){
      var obj = Ext.decode(response.responseText);
      this.load(obj);

      var cmp = Ext.getCmp('redmineGlobalUpdateIssues');
      this.toggleUpdateIssues(cmp);

      clearTimeout(tid);
      el.unmask();
    },
    failure: function(){
      el.unmask();
      clearTimeout(tid);
      Ext.MessageBox.show({
        title: Sonia.redmine.I18n.errorBoxTitle,
        msg: Sonia.redmine.I18n.errorOnLoadText,
        buttons: Ext.MessageBox.OK,
        icon: Ext.MessageBox.ERROR
      });
    }
  });
},
  
toggleUpdateIssues: function(checkbox){
  var autoClose = Ext.getCmp( 'redmineGlobalAutoClose' );
  var cmps = [
    autoClose,
    Ext.getCmp( 'redmineGlobalUsernameTransformer' )
  ];

  Sonia.redmine.toggleCmps(cmps, checkbox);
  this.toggleAutoClose(autoClose);
},
  
toggleAutoClose: function(checkbox){
  var cmps = [
    Ext.getCmp( 'redmineGlobalAutoCloseWords' )
  ];
  Sonia.redmine.toggleCmps(cmps, checkbox);
}

});

// register xtype
Ext.reg("redmineGlobalConfigPanel", Sonia.redmine.GlobalConfigPanel);

// regist config panel
registerGeneralConfigPanel({
  id: 'redmineGlobalConfigPanel',
  xtype: 'redmineGlobalConfigPanel'
});
