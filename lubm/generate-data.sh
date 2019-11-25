#!/bin/bash

cd "$(dirname "$0")"

mvn package

mkdir -p data
cd data

# https://jena.apache.org/documentation/sdb/query_performance.html
UNIV=1
java -cp ../target/twks-benchmarks-lubm.jar edu.lehigh.swat.bench.uba.Generator -univ $UNIV -onto http://swat.cse.lehigh.edu/onto/univ-bench.owl
