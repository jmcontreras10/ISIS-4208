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
**Note:** For "Tarea_4" no output path is required. a `.matt` / `_decompressed.<original_ext>` file will be created in the same folder of the input file

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
* ## Assignment 2 (Tarea_2)
  - ### Edmonds-Karp (1)
    * Input:
      `Input path for .txt file`
    ```txt
      N
      u1 v1 c_u1_v1
      u1 v2 c_u1_v1
    ```
    * Output:
      `.txt with: execution time, max flow, and flow`
  - ### Push-Relabel to Front (2)
    * Input:
      `Input path for .txt file`
    ```txt
      N
      u1 v1 c_u1_v1
      u1 v2 c_u1_v2
    ```
    * Output:
      `.txt with: execution time, max flow, and flow`

* ## Assignment 3: not present since was a Mathematical demonstration on graphs planarity

* ## Assignment 4 (Tarea_4)
  - ### Shannon-Fano codes Compressor based (1)
    * Input:
      `Input path for any text extension file (.txt, .csv, .html...)`
    ```txt
      KEBAB AB BAK
    ```
    * Output:
      `.sf or .hf files: custom file extensions for compression`
  - ### Huffman codes Compressor based (2)
    * Input:
      `Input path for any text extension file (.txt, .csv, .html...)`
    ```txt
      KEBAB AB BAK
    ```
    * Output:
      `.sf or .hf files: custom file extensions for compression`
  - ### .sf or .hf decompressor (3)
    * Input:
      `.sf or .hf file path to decompress. This file should have been compressed using previous algorithms on this assignament.`
    * Output:
      `Since the compression algorithms in this assignament saves the extension, the result file will have the original extension`
    ```txt
      KEBAB AB BAK
    ```
  
* ## Assignment 5 (Tarea_5)
  - ### Suffix Array Text Search (1)
    * Input:
      `Input path for a .txt file containing two lines: path to the text file and path to the queries file`
    ```txt
    path/to/text.txt
    path/to/queries.txt
    ```
    Where `text.txt` is any plain text file (may span multiple lines) and `queries.txt` has one query per line:
    ```txt
    ana
    na
    Banana
    ```
    * Optional: output path for results file
    * Output:
      `For each query, one line with the query followed by tab-separated positions where it appears in the text`
    ```txt
    ana	1	3
    na	2	4
    Banana	0
    ```

  - ### Performance Benchmark
    Runs all 12 experiment combinations (3 text sizes × 4 query sizes) and writes results to `outputs/Tarea_5/benchmark_results.csv`.

    ```bash
    # Build first
    gradlew.bat shadowJar           # Windows
    ./gradlew shadowJar             # macOS/Linux

    # Run benchmark
    java -cp build/libs/isis4208.jar isis4208.tarea_5.BenchmarkKt
    ```

    #### Experiment Results

    | Text size (chars) | Queries | Build (ms) | Search (ms) | Total (ms) |
    |------------------:|--------:|-----------:|------------:|-----------:|
    | 100,000           | 1,000   |            |             |            |
    | 100,000           | 10,000  |            |             |            |
    | 100,000           | 100,000 |            |             |            |
    | 100,000           | 1,000,000 |          |             |            |
    | 1,000,000         | 1,000   |            |             |            |
    | 1,000,000         | 10,000  |            |             |            |
    | 1,000,000         | 100,000 |            |             |            |
    | 1,000,000         | 1,000,000 |          |             |            |
    | 10,000,000        | 1,000   |            |             |            |
    | 10,000,000        | 10,000  |            |             |            |
    | 10,000,000        | 100,000 |            |             |            |
    | 10,000,000        | 1,000,000 |          |             |            |

    > Build time depends only on the text size (suffix array construction).
    > Search time scales with query count × query length × log(text size).
    > Full CSV exported to `outputs/Tarea_5/benchmark_results.csv` for Excel import.

## Generative AI Disclaimer

Generative AI was used under my supervision and correction only for the following cases in this project:
1. CLI, input reader and output writer generation
2. Unit tests and data generation for the default test cases in 'inputs' folder
