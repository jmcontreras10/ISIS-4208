# ISIS-4208 — DSA Assignments (Kotlin + Gradle)

This repository contains solutions for course assignments implemented in **Kotlin** and built with **Gradle**.  
Programs are executed as a runnable **JAR** using `java -jar` (cross-platform: Windows/macOS/Linux).

---

## Requirements

- **Java (JDK) 17+** installed and available in your `PATH`.
- Not Java 27 supported!. Preferred to use Java 21.

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
build/libs/isis4208.jar
```

Run

General usage:

```bash
java -jar build/libs/isis4208.jar <assignment> <problem> <input_path> [output_path]
```

Example (Tarea_x, Problema y), from root folder:

```bash
java -jar build/libs/isis4208.jar Tarea_x y inputs/p1.json
```

Some problems are returning a complex data as result. Those allow you to optionally add an output path.
By default, this will be saved under /outputs folder.

# Problems and algorithms inventory:
Here you will find solutions for:
* ## Assignment 1 (Tarea_1)
  - ### Problem 1 (1)
    * Input:
    `Input path for .json file`
    ```json
      {
        "users": [
          { "id": x1, "friends": [y1,y2,...] },
          ...
        ]
      }
    ```
      * Output:
    `Standart output boolean: if for the given data, the six degree theory fulfils.`
  - ### Problem 5 (5)
      * Input:
      `Input path for .csv file`
      `Optional: output path`
    ```csv
    from,to,cost
    x1,x2,x3
    y1,y2,y3
    ```
      *   Output:
      `.csv file and standart output metadata: upgrades, total cost, output path`
    ```csv
    from,to,cost
    x1,x2,x3
    y1,y2,y3
    ```

## Generative AI Disclaimer

Generative AI was used under my supervision and correction only for the following cases in this project:
1. CLI, input reader and output writer generation
2. Unit tests and data generation for the default test cases in 'inputs' folder