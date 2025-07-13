# Siedler von Catan

### VSCode Entwicklungsumgebung einrichten

Das Git-Repository klonen mit `git clone https://github.com/devjosh8/dhbw-java-siedler-von-catan` und anschließend diesen Ordner mit VS-Code öffnen.

## Builden und Ausführen

Zum builde mit dem Terminal in den Hauptpfad des Projektes gehen (dort wo die `pom.xml` liegt).
- Projekt bauen: `mvn package` (Wenn Maven nicht installiert ist, installieren. Für [Windows](https://maven.apache.org/), für MacOS am besten über `brew`)
- Projekt ausführen: `java -jar .\target\siedler_von_catan-1.0-SNAPSHOT-jar-with-dependencies.jar`. Die gebaute Jar-Datei befindet sich im `target` Ordner, der Name der Jar könnte abweichen.
Unter macOS `java -jar -XstartOnFirstThread target/siedler_von_catan-1.0-SNAPSHOT-jar-with-dependencies.jar`.

Die Jar wird bereits als "Fat-Jar" gebaut und kann somit ohne weitere installierte Abhängigkeiten ausgeführt werden (ausgenommen natürlich JVM).

**WICHTIG: Java Version 21 verwenden!**

### Hinweis für MacOS

Das Spiel ist für Windows gemacht. Das Auführen auf macOS führt zu UI-Bugs und funktioniert nicht richtig. Deshalb bitte nur unter Windows verwenden.



