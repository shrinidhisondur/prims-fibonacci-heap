prims-fibonacci-heap
====================

Java implementation of Prim's algorithm using a Fibonacci Heap.

The input is a file that contains an adjacency list graph or alternatively it also supports generating a random graph to run on.

How to compile: javac -d . *.java


1. Random mode: 
How to run: java .ADS.MST –r n d 

a)Runs in a random connected graph with n vertices and d% of density. 
 
b)It produces a random graph that is connected with the input density and number of nodes.
It first runs Prim's algorithm without fibonacci heap using direct edge comparisons. 
It then runs Prim's using Fibnacci heap. Output is a time comparison of both the schemes.

2. File mode:
How to run: java .ADS.MST -s file-name : read the input from a file ‘file-name’ for simple scheme
                 .ADS.MST -f file-name : read the input from a file ‘file-name’ for fibonacci scheme
                 
Sample file is attached.
The format of the file is thus:
n m // The number of vertices and edges respectively in the first line 
v1 v2 c1 // the edge (v1, v2) with cost c1 
v2 v3 c2 // the edge (v2, v3) with cost c2 
