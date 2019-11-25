#!/bin/bash

cd "$(dirname "$0")"

mvn package

java -cp ../target/twks-benchmarks-lubm.jar edu.lehigh.swat.bench.ubt.Test load config.kb config.query
