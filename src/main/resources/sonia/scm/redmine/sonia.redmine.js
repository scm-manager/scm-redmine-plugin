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

Ext.ns('Sonia.redmine');

Sonia.redmine.ConfigPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {
  
  formTitleText: 'redmine',
  urlText: 'Url',
  urlHelpText: 'Url of redmine installation (with contextpath).',
  
  autoCloseText: 'Enable Auto-Close',
  autoCloseHelpText: 'Enables the auto close function. SCM-Manager searches for \n\
                      issue keys and auto close words in commit messages. If \n\
                      both found in a message SCM-Manager closes the issue in \n\
                      the redmine server. <strong>Note:</strong> It is necessary \n\
                      that users have the same password in SCM-Manager and redmine.',
  
  updateIssuesText: 'Update redmine Issues',
  updateIssuesHelpText: 'Enable the automatic update function. SCM-Manager searches for\n\
                         issue keys in commit messages. If a issue id is found SCM-Manager\n\
                         updates the issue with a comment. <strong>Note:</strong> It \n\
                         is necessary that users have the same password in SCM-Manager \n\
                         and redmine.',
  
  autoCloseWordsText: 'Auto-Close Words',
  autoCloseWordsHelpText: 'Comma separated list of words to enable the auto close function. \n\
                           Each commit message of a changeset is being searched for these words.',
  
  usernameTransformerText: 'Transform Username',
  usernameTransformerHelpText: 'Pattern to create a username for the redmine server.</br>\n\
                                {0} - name of the current user</br>\n\
                                {1} - mail address of the current user</br>\n\
                                {2} - display name of the current user',
  
  initComponent: function(){
    
    var config = {
      title: this.formTitleText,
      items: [{
        name: 'redmineUrl',
        fieldLabel: this.urlText,
        property: 'redmine.url',
        vtype: 'urlsimple',
        helpText: this.urlHelpText
      },{
        id: 'redmineUpdateIssues',
        name: 'redmineUpdateIssues',
        xtype: 'checkbox',
        fieldLabel: this.updateIssuesText,
        property: 'redmine.update-issues',
        helpText: this.updateIssuesHelpText
      },{
        id: 'redmineAutoClose',
        name: 'redmineAutoClose',
        xtype: 'checkbox',
        fieldLabel: this.autoCloseText,
        property: 'redmine.auto-close',
        helpText: this.autoCloseHelpText,
        listeners: {
            check: this.toggleUpdateIssues
          }
      },{
        id: 'redmineAutoCloseWords',
        name: 'redmineAutoCloseWords',
        fieldLabel: this.autoCloseWordsText,
        property: 'redmine.auto-close-words',
        helpText: this.autoCloseWordsHelpText,
        value: 'fixed, fix, closed, close, resolved, resolve'
      },{
        id: 'redmineUsernameTransformer',
        name: 'redmineUsernameTransformer',
        fieldLabel: this.usernameTransformerText,
        property: 'redmine.auto-close-username-transformer',
        helpText: this.usernameTransformerHelpText,
        value: '{0}'
      }]
    }
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.redmine.ConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  loadExtraProperties: function(item){
    var cmp = Ext.getCmp('redmineAutoClose');
    this.toggleUpdateIssues.call(cmp);
  },
  
  toggleUpdateIssues: function(){
    var cmps = [
      Ext.getCmp( 'redmineAutoClose' ),
      Ext.getCmp( 'redmineAutoCloseWords' ),
      Ext.getCmp( 'redmineUsernameTransformer' )
    ];
    
    Ext.each(cmps, function(cmp){
      cmp.setReadOnly(!this.checked);
      if ( ! this.checked ){
        cmp.addClass('x-item-disabled');
      } else {
        cmp.removeClass('x-item-disabled');
      }
    }, this);
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

// custom Vtype for vtype:'IPAddress'
Ext.apply(Ext.form.VTypes, {
    urlsimple:  function(v) {
		return /^[a-z]+:\/\//i.test(v);
    },
    urlsimpleText: 'Must be an url or ip address (including protocol prefix) for example http://www.your-server.com'
});