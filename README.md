if not already present, be sure to have the libraries:

- elki-bundle-0.8.0.jar
- jade.jar

compile with:

```
javac -cp "libs/*" -d bin -sourcepath src src/com/sdai/smartfarm/Main.java
```

use ```export``` or ```source``` to define environment variables if you're on Linux

launch with:

```
java -cp "bin:libs/*" com.sdai.smartfarm.Main
```

enjoy