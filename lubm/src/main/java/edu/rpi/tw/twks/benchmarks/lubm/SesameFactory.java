package edu.rpi.tw.twks.benchmarks.lubm;

/*
* @author Yuanbo Guo
*/

public class SesameFactory extends edu.lehigh.swat.bench.ubt.api.RepositoryFactory {

  public SesameFactory() {
  }

  public edu.lehigh.swat.bench.ubt.api.Repository create() {
    return new sesamewrapper.Sesame();
  }
}
