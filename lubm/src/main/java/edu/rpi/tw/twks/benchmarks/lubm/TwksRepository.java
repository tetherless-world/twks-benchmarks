package edu.rpi.tw.twks.benchmarks.lubm;

import com.google.common.collect.ImmutableList;
import edu.rpi.tw.twks.abc.MemTwks;
import edu.rpi.tw.twks.abc.MemTwksConfiguration;
import edu.rpi.tw.twks.api.Twks;
import edu.rpi.tw.twks.api.TwksTransaction;
import edu.rpi.tw.twks.nanopub.MalformedNanopublicationException;
import edu.rpi.tw.twks.nanopub.Nanopublication;
import edu.rpi.tw.twks.nanopub.NanopublicationParser;
import edu.rpi.tw.twks.uri.Uri;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public final class TwksRepository
    implements edu.lehigh.swat.bench.ubt.api.Repository {
    private final static Logger logger = LoggerFactory.getLogger(TwksRepository.class);
    private @Nullable
    Twks twks;
  //  private RdfSchemaRepository repository_ = null;
//  private QueryResultListener qResultListener_ = new QueryResultListener();
  private String ontology = null;

  @Override
  public void setOntology(final String ontology) {
      this.ontology = ontology;
  }

    private void checkOpen() {
        if (twks == null) {
            throw new IllegalStateException("repository is not open");
        }
    }

    @Override
  public void clear() {
        checkOpen();
        twks.deleteNanopublications();
  }

  @Override
  public final void open(final String database) {
      checkState(twks == null);
      twks = new MemTwks(MemTwksConfiguration.builder().build());
  }

  @Override
  public final void close() {
      if (twks == null) {
          return;
      }
      twks = null;
  }

  @Override
  public final boolean load(final String dataPath) {
      checkOpen();

      final ImmutableList.Builder<Nanopublication> nanopublicationsBuilder = ImmutableList.builder();

      // Load ontology
      try {
          nanopublicationsBuilder.add(NanopublicationParser.builder().setSource(Uri.parse(ontology)).build().parseOne());
      } catch (final MalformedNanopublicationException e) {
          logger.error("error loading ontology {}", ontology, e);
          return false;
      }

      final File dataDirectoryPath = new File(dataPath);
      final File[] dataFilePaths = dataDirectoryPath.listFiles();
      if (dataFilePaths == null) {
          logger.error("unable to list data directory {}", dataPath);
          return false;
      }

      for (final File dataFilePath : dataFilePaths) {
          if (!dataFilePath.getName().endsWith(".owl")) {
              logger.debug("ignoring non-data file {}", dataFilePath);
              continue;
          }
          try {
              nanopublicationsBuilder.add(NanopublicationParser.builder().setSource(dataFilePath).build().parseOne());
              logger.info("loaded data file {}", dataFilePath);
          } catch (final MalformedNanopublicationException e) {
              logger.error("error loading data file {}", dataFilePath, e);
              return false;
          }
      }

      return true;
  }

  @Override
  public final edu.lehigh.swat.bench.ubt.api.QueryResult issueQuery(final edu.lehigh.swat.bench.ubt.api.Query query) {
      checkOpen();

      final Query parsedQuery = QueryFactory.create(query.getString());
      try (final TwksTransaction twksTransaction = twks.beginTransaction(ReadWrite.READ)) {
          try (final QueryExecution queryExecution = twksTransaction.queryAssertions(parsedQuery)) {
              throw new UnsupportedOperationException();
          }
      }
  }
}
