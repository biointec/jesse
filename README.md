
# Jesse

Tree-based algorithm to calculate graphlet densities of nodes in a graph using equations.

## Usage
Command-line use: `java -jar jesse-1.0.0.jar`

In the command line, Jesse will then ask for the maximal graphlet size and needed files to count the orbits (either the included standard files or newly generated files), a graph file and a file to save the results. 

Graph input files need to have an edge on each line, consisting of the names of the two nodes it connects, separated by a tab.

The results file contains the graphlet degrees of each node of the graph. Each line starts with a node's name, followed by its graphlet degrees in order of ascending orbit number, separated by tabs. For instance:

A 4 0 3 3

would mean node 'A' touches orbit 0 (a single edge) 4 times, orbit 1 (an end node of a 3-node path) 0 times, and both orbit 2 (the middle node of a 3-node path) and 3 (the triangle) 3 times.


## Compiling
Compiling the jar-file is done using Maven:

```
$ mvn clean install
```

The resulting jar can be found in the `target` directory.

