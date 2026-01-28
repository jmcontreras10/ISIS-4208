# ISIS-4208 — DSA Assignments (Kotlin + Gradle)

This repository contains solutions for course assignments implemented in **Kotlin** and built with **Gradle**.  
Programs are executed as a runnable **JAR** using `java -jar` (cross-platform: Windows/macOS/Linux).

---

## Requirements

- **Java (JDK) 17+** installed and available in your `PATH`.

Verify:

```bash
java -version
```

## Build (Generate runnable JAR)

From the project root folder:

macOS / Linux
```bash
./gradlew shadowJar
```

Windows
```bash
gradlew.bat shadowJar
```

This generates the runnable JAR at:

```bash
build/libs/dsa.jar
```

Run

General usage:

```bash
java -jar build/libs/dsa.jar <assignment> <problem> <input_path>
```

Example (Tarea_x, Problema y), from root folder:

```bash
java -jar build/libs/dsa.jar Tarea_x y inputs/p1.json
```