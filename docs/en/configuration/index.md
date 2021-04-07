---
title: Configuration
---

The SCM-Redmine-Plugin can be configured globally and repository specific. The global configuration is used for all repositories which doesn't have an specific config. The repository specific configuration can be disabled in the global config.

### Configuration form
To connect SCM-Manager with Redmine an instance url including the context path is required.
Afterwards you may already configure how the Redmine issues can be modified or updated. 

#### Create comments
To create comments in Redmine, credentials are mandatory, which should belong to a technical Redmine user.
Furthermore, the REST interface of Redmine must be activated, the setting is located in Redmine under `Administration->Settings->API->Enable REST web service`.
For the comments, the text formatting can be set via a selection menu, this must match the formatting used in Redmine (`Administration->Settings->General->Text formatting`).

Comments are generated on the Redmine issue if the issue id is mentioned within a commit message. 

Example Commit message: "#492 Add awesome new feature".

This will create a comment with this commit message on Redmine issue 492.

#### Update issue status
To change the status of an issue via a commit message, an issue id and a Redmine status must be in one sentence.

Example Commit message: "Bug #42 closed".

The example sets the status of issue 42 to "Closed".
Of course, this assumes that the status "Closed" exists in the specified Redmine instance.

Using the "Redmine status mapping" you can define key words that can be used instead of the Redmine status.
These keywords can be specified in the form of a comma separated list.
For example, for the status "Closed" you could specify the following keywords: "closes, closing".
The commit message "Closes Bug #42" would also set the ticket 42 to the status "Closed".

![Redmine configuration](assets/config.png)
