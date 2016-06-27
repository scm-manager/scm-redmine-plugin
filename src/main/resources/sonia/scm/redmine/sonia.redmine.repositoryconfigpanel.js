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

Sonia.redmine.ConfigPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {
  
  initComponent: function(){
    
    var config = {
      title: Sonia.redmine.I18n.formTitleText,
      items: [{
        name: 'redmineUrl',
        fieldLabel: Sonia.redmine.I18n.urlText,
        property: 'redmine.url',
        vtype: 'urlsimple',
        helpText: Sonia.redmine.I18n.urlHelpText
      },{
        id: 'redmineUpdateIssues',
        name: 'redmineUpdateIssues',
        xtype: 'checkbox',
        fieldLabel: Sonia.redmine.I18n.updateIssuesText,
        property: 'redmine.update-issues',
        helpText: Sonia.redmine.I18n.updateIssuesHelpText,
        listeners: {
            check: {
                fn: this.toggleUpdateIssues,
                scope: this
            }
        }
      },{
        id: 'redmineAutoClose',
        name: 'redmineAutoClose',
        xtype: 'checkbox',
        fieldLabel: Sonia.redmine.I18n.autoCloseText,
        property: 'redmine.auto-close',
        helpText: Sonia.redmine.I18n.autoCloseHelpText
      },{
        id: 'redmineUsernameTransformer',
        name: 'redmineUsernameTransformer',
        fieldLabel: Sonia.redmine.I18n.usernameTransformerText,
        property: 'redmine.auto-close-username-transformer',
        helpText: Sonia.redmine.I18n.usernameTransformerHelpText,
        value: '{0}'
      }]
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.redmine.ConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  loadExtraProperties: function(item){
    var cmp = Ext.getCmp('redmineUpdateIssues');
    this.toggleUpdateIssues(cmp);
  },
  
  toggleUpdateIssues: function(checkbox){
    var cmps = [
      Ext.getCmp( 'redmineAutoClose' ),
      Ext.getCmp( 'redmineUsernameTransformer' )
    ];
    
    Sonia.redmine.toggleCmps(cmps, checkbox);
  }
  
});

// register xtype
Ext.reg("redmineConfigPanel", Sonia.redmine.ConfigPanel);


// register panel
Sonia.repository.openListeners.push(function(repository, panels){
  if (Sonia.repository.isOwner(repository)){
    panels.push({
      xtype: 'redmineConfigPanel',
      item: repository
    });
  }
});
