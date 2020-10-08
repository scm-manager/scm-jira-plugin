---
title: Commit Nachricht Validator
---
Wenn das [SCM-Commit-Message-Checker-Plugin](https://scm-manager.org/plugins/scm-commit-message-checker-plugin/) installiert wurde, 
liefert das Jira Plugin einen eigenen Validator für Commit Nachrichten. Dieser Validator kann bei der Commit Nachricht Validierung konfiguriert werden.

# Validator Konfiguration
Der Jira Ticket Schlüssel Validator überprüft sämtliche neuen Commits auf gültige Ticket Schlüssel. 
Die Ticket Schlüssel müssen mit der Konfiguration im Jira Plugin übereinstimmen.
Dieser Validator kann auch nur auf einen definierten Satz von Branches angewandt werden. Wird kein Branch angegeben, werden alle Branches validiert.
Jeder neue Commit, welcher nicht einen gültigen Ticket Schlüssel enthält, wird vom SCM-Manager abgelehnt.

Beispiel: Wenn Sie in der Jira Plugin Konfiguration den Projekt-Filter auf "SCM" setzen, werden nur Ticket Schlüssel welche dem entsprechend (z. B. "SCM-4584") als gültig anerkannt.

![Issue Key Validator](assets/validator.png)
