---
title: Troubleshooting
---

When setting up Redmine and SCM-Manager, some errors can occur.
To make troubleshooting easier, this document lists common problems, their causes, and how to solve them.

- 401 HTTP Status Code
    
    If you encounter a 401 Unauthorized status code during communication between SCM-Manager and Redmine, there are two
    common causes:
        
    1. Incorrect Service Account Credentials
        
        The credentials for the Redmine service account are either missing or invalid in the SCM-Manager's Redmine
        plugin configuration. To fix this, please set up or correct the credentials.
    2. Disabled REST API
        
        The REST API is disabled in Redmine. To resolve the error, it must be enabled. The configuration section
        explains how to activate it.
- 403 HTTP Status Code
      
    This error occurs if the Redmine service account does not have the necessary permissions to edit the relevant issue.
    In this case, the service account must be granted the required permissions in Redmine.

You can use the Trace Monitor plugin to find out which status code you are receiving
