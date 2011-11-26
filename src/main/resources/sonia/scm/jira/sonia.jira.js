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

Ext.ns('Sonia.jira');

Sonia.jira.ConfigPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {
  
  formTitleText: 'Jira',
  urlText: 'Url',
  projectKeysText: 'Project Keys',
  
  urlHelpText: 'Url of Jira installation (with contextpath).',
  projectKeysHelpText: 'Comma separated project keys e.g.: SCM, JENKINS.',
  
  autoCloseText: 'Enable Auto-Close',
  autoCloseHelpText: 'Enables the auto close function. SCM-Manager searches for \n\
                      issue keys and auto close words in commit messages. If \n\
                      both found in a message SCM-Manager closes the issue in \n\
                      the jira server. <strong>Note:</strong> It is necessary \n\
                      that users have the same password in SCM-Manager and Jira.',
  
  updateIssuesText: 'Update Jira Issues',
  updateIssuesHelpText: 'Enable the automatic update function. SCM-Manager searches for\n\
                         issue keys in commit messages. If a issue id is found SCM-Manager\n\
                         updates the issue with a comment. <strong>Note:</strong> It \n\
                         is necessary that users have the same password in SCM-Manager \n\
                         and Jira.',
  
  autoCloseWordsText: 'Auto-Close Words',
  autoCloseWordsHelpText: 'Comma separated list of words to enable the auto close function. \n\
                           Each commit message of a changeset is being searched for these words.',
  
  usernameTransformerText: 'Transform Username',
  usernameTransformerHelpText: 'Pattern to create a username for the jira server.</br>\n\
                                {0} - name of the current user</br>\n\
                                {1} - mail address of the current user</br>\n\
                                {2} - display name of the current user',
  
  initComponent: function(){
    
    var config = {
      title: this.formTitleText,
      items: [{
        name: 'jiraUrl',
        fieldLabel: this.urlText,
        property: 'jira.url',
        vtype: 'url',
        helpText: this.urlHelpText
      },{
        name: 'jiraProjectKeys',
        fieldLabel: this.projectKeysText,
        property: 'jira.project-keys',
        helpText: this.projectKeysHelpText
      },{
        name: 'jiraUpdateIssues',
        xtype: 'checkbox',
        fieldLabel: this.updateIssuesText,
        property: 'jira.update-issues',
        helpText: this.updateIssuesHelpText
      },{
        name: 'jiraAutoClose',
        xtype: 'checkbox',
        fieldLabel: this.autoCloseText,
        property: 'jira.auto-close',
        helpText: this.autoCloseHelpText
      },{
        name: 'jiraAutoCloseWords',
        fieldLabel: this.autoCloseWordsText,
        property: 'jira.auto-close-words',
        helpText: this.autoCloseWordsHelpText,
        value: 'fixed, fix, closed, close, resolved, resolve'
      },{
        name: 'usernameTransformerText',
        fieldLabel: this.usernameTransformerText,
        property: 'jira.auto-close-username-transformer',
        helpText: this.usernameTransformerHelpText,
        value: '{0}'
      }]
    }
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.jira.ConfigPanel.superclass.initComponent.apply(this, arguments);
  }
  
});

// register xtype
Ext.reg("jiraConfigPanel", Sonia.jira.ConfigPanel);

// register panel
Sonia.repository.openListeners.push(function(repository, panels){
  if (Sonia.repository.isOwner(repository)){
    panels.push({
      xtype: 'jiraConfigPanel',
      item: repository
    });
  }
});

