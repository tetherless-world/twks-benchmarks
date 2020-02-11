package edu.rpi.tw.twks.benchmarks.lubm;

public final class TwksRepositoryFactory extends edu.lehigh.swat.bench.ubt.api.RepositoryFactory {
  @Override
  public edu.lehigh.swat.bench.ubt.api.Repository create() {
    return new TwksRepository();
  }
}
