## Individual Project for the Symbolic and Distributed AI course, University of Genova, IT 
## Author: Gabriele Dellepere
### Teacher: Viviana Mascardi
### Project delivered on February 2025

if not already present, be sure to have the libraries in your libs/ folder:

- elki-bundle-0.8.0.jar
- jade.jar

you will also need to add in the root directory 3 png images for the agents:
- drone.png
- robot.png
- tractor.png

if you're a lazy person here's a link from the gitignored files I used
https://drive.google.com/file/d/1d9uI3_FDBT9DP6ljGf9aqDR1u9Sap9xc/view?usp=drive_link

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
