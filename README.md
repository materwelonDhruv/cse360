## Team
- Riley Anderson
- Mike Hawes
- Dhruv Jain
- Tyler Woodburn
- Atharva Vimal Zaveri

---
## What this project is
A Java 21 + JavaFX desktop application. It uses Maven to manage dependencies like:
- JavaFX (GUI)
- H2 (in‑memory / file database)
- Lucene (search)
- Reflections (classpath scanning)
- SLF4J (logging)
- FuzzyWuzzy (string similarity)
- BouncyCastle (password hashing)
- JUnit 5 (tests)

The main entry point you should run is: `App.java` (which calls the JavaFX launcher in `application/InstantiateApp`).

---
## Requirements
Install these first:
1. Java 21 (JDK 21). Check with: `java -version`
2. Maven 3.9+ (`mvn -version`)
3. Git (to clone)

JavaFX libraries are pulled automatically through Maven profiles based on your OS:
- macOS ARM (Apple Silicon): profile `mac-aarch64` auto-activates
- Windows x64: profile `win-x64` auto-activates
No manual JavaFX SDK download needed unless you want to run without Maven.

---
## Get the code
Download as ZIP and unzip (recommended) OR
```
git clone <repo-url>
cd cse360
```
    
---
## Project layout (simplified)
```
pom.xml            Maven build file
src/main/java      Source code
  App.java         The class you run
  application/...  App context, routing, JavaFX startup
src/test/java      (Tests – currently skipped by default)
javadoc/           Generated API docs (HTML) (Outdated)
target/            Build output (generated)
```

---
## Import into IntelliJ IDEA
1. Open IntelliJ > "Open" > select the project root folder (where `pom.xml` lives).
2. IntelliJ will detect Maven and index dependencies.
3. Make sure Project SDK is set to JDK 21 (File > Project Structure > SDKs).
4. Open `src/main/java/App.java`.
5. Right‑click > Run 'App.main()'.

If JavaFX fails to launch: invalidate caches, then re-run; or ensure no old JDK path is set.

---
## Import into Eclipse (Maven / m2e)
1. File > Import > Maven > Existing Maven Projects.
2. Choose the project root (folder with `pom.xml`). Finish.
3. Ensure Eclipse is using a JDK 21 execution environment (Preferences > Java > Installed JREs).
4. Wait for Maven dependencies to download.
5. Find `App.java` (package root) > Right‑click > Run As > Java Application.

If you see JavaFX class not found errors:
- Confirm the correct OS profile activated (it should automatically).
- Do Project > Update Maven Project.
- Make sure build path uses JDK 21, not a JRE container.

---
## Enabling tests
Tests are skipped by default via the property `<skipTests>true</skipTests>`.

Please right click the test files and run each individually.

---

## Clean build if things break
```
mvn clean
rm -rf ~/.m2/repository/org/openjfx   # (optional) force redownload JavaFX
mvn package
```