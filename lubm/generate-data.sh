#!/bin/bash

cd "$(dirname "$0")"

mvn package

mkdir -p data
cd data

# https://jena.apache.org/documentation/sdb/query_performance.html
java -cp ../target/twks-benchmarks-lubm.jar edu.lehigh.swat.bench.uba.Generator -univ 1 -onto http://swat.cse.lehigh.edu/onto/univ-bench.owl
