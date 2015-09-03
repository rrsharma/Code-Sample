# Code-Sample
Community Detection Algorithm using Label Propagation (Multithreading with Java)

I have implemented the algorithm mentioned in the paper "Near linear time algorithm to detect community structures in large-scale networks", by Usha Nandini Raghavan, Reka Albert, Soundar Kumara.
I have also added the modification suggested by Kishore Kothapalli, Sriram V. Pemmaraju, and Vivek Sardeshmukh, in the paper "On the Analysis of a Label Propagation Algorithm for Community Detection"; which prevents epidemic spread of communities.



PRE-COMPILE :
Save Enron email dataset in a .txt file and change the input file name in main of LP.java. 

COMPILE: 
javac findDominantLabel.java
javac LP.java

RUN:
java LP
