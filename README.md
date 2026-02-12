
Dieses Projekt wurde im Rahmen des Moduls Software Engineering III  entwickelt. Ziel des Projekts war es, die Funktionsweise von Netzwerkarchitekturen (Client-Server, Broadcast) zu verstehen und schließlich das Broker-Architekturmuster zur Entkopplung von Systemkomponenten zu implementieren.

# Projektübersicht
Die Simulation basiert auf einem bereitgestellten Netzwerk-Framework in Kotlin, das die Kommunikation zwischen verschiedenen Server-Instanzen über virtuelle Adressen ermöglicht.

Das Kernstück ist die Umsetzung eines Brokers, der als zentraler Vermittler dient. Dadurch müssen die einzelnen Geschäftskomponenten (Bestellung, Bezahlung, Versand) die Netzwerkadressen der jeweils anderen Teilnehmer nicht kennen.

# Architektur-Highlights:
Lose Kopplung: Komponenten kommunizieren ausschließlich über den Broker.

Location Transparency: Ein Dienst muss nur wissen, wie er den Broker erreicht, nicht wo die anderen Dienste liegen.

Event-gesteuerter Workflow: Simulation eines E-Commerce-Prozesses von der Bestellung bis zum Mail-Versand.

# 🛠 Komponenten des Systems
Das System simuliert einen vollständigen Bestellprozess, aufgeteilt in folgende Micro-Services (Server):

Broker: Die zentrale Registrierungs- und Vermittlungsstelle.

Order Component (Bestellung): Nimmt Warenlisten und Kontodaten entgegen und koordiniert den Prozess.

Payment Component (Bezahlung): Prüft und verarbeitet die Transaktion.

Shipping Component (Versand): Übernimmt die Logistik nach erfolgreicher Zahlung.

IMAP-Server (Simulation): Gibt Benachrichtigungen (E-Mails) über den Status auf der Konsole aus.

# 💻 Technischer Stack
Sprache: Kotlin

Paradigma: Objektorientierte Programmierung & Message Passing

Tools: Gradle, Git

Konzepte: Broker Pattern, Client/Server-Infrastruktur, Serialisierung (simuliert via sendData).

📖 Aufgabenstellung (Workshop-Kontext)
Das Projekt wurde schrittweise in vier Phasen entwickelt:

A1: Aufbau einer einfachen Client/Server-Verbindung.

A2: Implementierung von IDs und Broadcast-Funktionen.

A3: Entwicklung eines Chat-Servers mit spezifischem Routing.

A4 (Hauptteil): Vollständige Umsetzung der Broker-Architektur zur Orchestrierung eines Bestellvorgangs, bei dem die Komponenten strikt voneinander isoliert sind.


