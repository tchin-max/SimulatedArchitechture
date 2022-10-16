# Projekt zum Ausprobieren unterschiedlicher Architekturmuster für die Vorlesung SE III

## Nutzung des Repositories

Es gibt unterschiedliche Tags und Branches in dem Repository, so dass Sie einfach zwischen den unterschiedlichen Beispielen wecheln können.

### Branches
- **main**: Der Hauptbranch, dessen initialer Commit erst einmal nur ein sehr einfaches Kotlin Projekt beinhaltet, mit dieser Readme und einer passenden .gitignore.
- **ServerTypes**: Hier finden Sie einen einfachen EchoServer, und einen ChatServer sowie einen passenden Client. Je nach gewähltem Commit ist mehr oder weniger Funktionalität vorhanden.

### Tags
- **SSCSExample**: Simple Simulated Client/Server Example. In diesem Commit existiert ein Server und zwei Clients, die über Strings Nachrichten schicken können. Hierbei können die Clients noch nicht ohne weiteres miteinander komunizieren.

## Erstellen eines Checkouts in InteliJ
Wählen Sie in InteliJ aus, dass Sie ein neues Projekt erstellen wollen. Hierbei nutzen Sie den Menüpunkt "New -> Projekt from Version Controll..." und geben Sie die URL dieses Repositories ein. Sie erhalten dann ein Projekt, dass im main branch ausgechecked ist, und direkt nutzbar sein sollte.
