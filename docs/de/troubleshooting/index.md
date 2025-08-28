---
title: Fehlersuche
---

Beim Einrichten von Redmine und dem SCM-Manager kann es leicht zu Fehlern kommen.
Um Benutzern die Fehlersuche zu erleichtern, wird hier eine Liste von typischen und bekannten Fehlern,
deren Ursache und Behebung dokumentiert.

- 401 HTTP Statuscode
    
    Falls es bei der Kommunikation zwischen dem SCM-Manager und Redmine zu dem HTTP Status Code 401 kommt,
    dann gibt es zwei typische Fehlerursachen.
    1. Falsche Login-Daten für den Redmine-Service-Account
        
        Es könnte sein, dass diese nicht oder fehlerhaft in der Redmine-Plugin-Konfiguration im SCM-Manager hinterlegt wurden.
        In diesem Fall sollten diese hinterlegt bzw. korrigiert werden.
    
    2. Deaktivierte REST-API in Redmine
        
        Diese sollte aktiviert sein, um den 401 Statuscode zu vermeiden.
        Im Abschnitt [Konfiguration](../configuration) wird erläutert wie man die REST-API aktiviert.
- 403 HTTP Statuscode
    
    Dieser Fehler kann zustande kommen, falls der Redmine-Service-Account nicht die nötige Berechtigung hat,
    um das jeweilige Issue zu bearbeiten.
    In dem Fall müssen dem Redmine-Service-Account die benötigten Berechtigungen in Redmine gegeben werden.

Welcher Statuscode vorliegt, kann über das Trace-Monitor Plugin herausgefunden werden.
