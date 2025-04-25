# Siedler von Catan

### VSCode Entwicklungsumgebung einrichten

Das Git-Repository klonen mit `git clone https://github.com/devjosh8/dhbw-java-siedler-von-catan` und anschließend diesen Ordner mit VS-Code öffnen.

## Builden und Ausführen

Zum builde mit dem Terminal in den Hauptpfad des Projektes gehen (dort wo die `pom.xml` liegt).
- Projekt bauen: `mvn package` (Wenn Maven nicht installiert ist, installieren. Für [Windows](https://maven.apache.org/), für MacOS am besten über `brew`)
- Projekt ausführen: `java -jar .\target\siedler_von_catan-1.0-SNAPSHOT-jar-with-dependencies.jar`. Die gebaute Jar-Datei befindet sich im `target` Ordner, der Name der Jar könnte abweichen.

Die Jar wird bereits als "Fat-Jar" gebaut und kann somit ohne weitere installierte Abhängigkeiten ausgeführt werden (ausgenommen natürlich JVM).

## Verwendete Abhängigkeiten

Wir verwenden [LibGDX](https://libgdx.com/wiki/) als Hauptlibrary für das Spiel. Dazu am besten Tutorials oder Demos angucken, das meiste ist eigentlich selbsterklärend.
Mit der Zeit werden weitere Abhängigkeiten hinzukommen, zum Beispiel für die Verwaltung von Text TTF und dergleichen.

## Clean Code

Bitte an [Java Clean Code](https://www.baeldung.com/java-clean-code) halten. Auch hier ist das meiste selbsterklärend. Je mehr `final` desto besser! (bei Java kann das anscheinend Performance-technisch schon ein bisschen ausmachen!)

### Kommentare
Der Spruch "guter Code muss nicht kommentiert werden" ist dumm. Bitte komplexere Methoden kommentieren, die vom Name her nicht **sofort** selbsterklärend sind. Auch gerne hin und wieder im Code kommentieren, 
wenn gerade ein komplexerer Ablauf stattfindet. Solange die Kommentare helfen den Code besser zu verstehen oder einordnen zu können, sind Kommentare gerne gewollt!

Bitte aber nicht zu Kommentaren oder "Doc-Strings" gezwungen fühlen, wobei hier auch für komplexere Methoden gilt, dass diese Abhilfe beim Verständnis schaffen können und die Docstrings in Java auch dazu gut sind,
Argumente einer Funktion zu erläutern. Mehr Information hier: https://www.oracle.com/de/technical-resources/articles/java/javadoc-tool.html (Ein bisschen runterscrollen: "@param"-Verwendung ist super, weil dadurch Argumente
besser erklärt werden können)

### Commit Guidelines

Bitte an Commit Guidelines halten, damit am Ende alles geil aussieht: [Commit Guidelines](https://www.conventionalcommits.org/en/v1.0.0/#summary)



