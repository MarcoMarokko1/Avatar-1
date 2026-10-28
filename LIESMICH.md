# Die vier Elemente – Forge 1.20.1

Ein kleiner, eigenständiger Prototyp, inspiriert von Elementbändigung. Er enthält vier Foki, keine vollständige Avatar-Welt, Charaktere oder Animationen.

## Spielen

- Forge für Minecraft Java **1.20.1**, Version **47.3.0 oder neuer** aus der 47er-Reihe, installieren.
- Die gebaute Datei `elemente-1.0.0.jar` in den `mods`-Ordner legen und das Forge-Profil starten. Im Multiplayer brauchen Server und Clients die Mod.
- Im Kreativmenü gibt es den Reiter „Die vier Elemente“. Alternativ im Überlebensmodus einen Stock mit dem jeweiligen Material im Craftingfeld kombinieren: Wassereimer, Bruchstein, Lohenstaub bzw. Feder. Der leere Eimer bleibt beim Herstellen erhalten.
- Fokus in die Hand nehmen und **Rechtsklick**: Elementangriff in Blickrichtung (Reichweite ca. 12 Blöcke, durch Wände blockiert). **Schleichen + Rechtsklick**: Spezialfähigkeit. Ein Fokus hat nach einem normalen Angriff 1,5 Sekunden und nach einer Spezialfähigkeit 8 Sekunden Abklingzeit.
- Wasser: verlangsamt ein Ziel; Spezialfähigkeit heilt zwei Herzen und löscht Feuer.
- Erde: stärkerer Treffer mit Rückstoß; Spezialfähigkeit gibt kurz Resistenz II.
- Feuer: setzt ein getroffenes Ziel in Brand; Spezialfähigkeit gibt kurz Feuerresistenz.
- Luft: stößt ein Ziel zurück; Spezialfähigkeit lässt dich nach vorne und oben springen und gibt kurz langsamen Fall.
- Befehle bei aktivierten Cheats: `/give @s elemente:wasser_fokus`, `/give @s elemente:erde_fokus`, `/give @s elemente:feuer_fokus`, `/give @s elemente:luft_fokus`.

## Aus Quellcode bauen

JDK 17 und Internetzugang sind notwendig. ZIP entpacken, im Projektordner `gradlew.bat build` (Windows) oder `./gradlew build` (Linux/macOS) ausführen. Die fertige JAR liegt unter `build/libs/elemente-1.0.0.jar`. Der erste Build lädt Forge, Minecraft-Entwicklungsdateien und Gradle herunter und kann dauern. Die Projekt-ZIP selbst gehört nicht in den mods-Ordner.
