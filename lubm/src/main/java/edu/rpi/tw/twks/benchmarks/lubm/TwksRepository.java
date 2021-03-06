package edu.rpi.tw.twks.benchmarks.lubm;

import com.google.common.collect.ImmutableList;
import edu.lehigh.swat.bench.ubt.api.QueryResult;
import edu.rpi.tw.twks.api.Twks;
import edu.rpi.tw.twks.api.TwksTransaction;
import edu.rpi.tw.twks.factory.TwksFactory;
import edu.rpi.tw.twks.factory.TwksFactoryConfiguration;
import edu.rpi.tw.twks.nanopub.MalformedNanopublicationException;
import edu.rpi.tw.twks.nanopub.Nanopublication;
import edu.rpi.tw.twks.nanopub.NanopublicationBuilder;
import edu.rpi.tw.twks.nanopub.NanopublicationParser;
import edu.rpi.tw.twks.uri.Uri;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
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
      twks = TwksFactory.getInstance().createTwks(TwksFactoryConfiguration.builder().setFromSystemProperties().build());
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

      final Model assertionsModel = ModelFactory.createDefaultModel();

      final ImmutableList.Builder<Nanopublication> nanopublicationsBuilder = ImmutableList.builder();

      // Load ontology
      final Nanopublication ontologyNanopublication;
      try {
          ontologyNanopublication = NanopublicationParser.builder().setLang(Lang.RDFXML).setSource(Uri.parse(ontology)).build().parseOne();
          assertionsModel.add(ontologyNanopublication.getAssertion().getModel());
          nanopublicationsBuilder.add(ontologyNanopublication);
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
              final Nanopublication nanopublication = NanopublicationParser.builder().setLang(Lang.RDFXML).setSource(dataFilePath).build().parseOne();
              assertionsModel.add(nanopublication.getAssertion().getModel());
              nanopublicationsBuilder.add(nanopublication);
              logger.debug("loaded data file {}", dataFilePath);
          } catch (final MalformedNanopublicationException e) {
              logger.error("error loading data file {}", dataFilePath, e);
              return false;
          }
      }

      {
          final Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
          reasoner.bindSchema(ontologyNanopublication.getAssertion().getModel());
          final InfModel assertionsInfModel = ModelFactory.createInfModel(reasoner, assertionsModel);

          final NanopublicationBuilder inferredNanopublicationBuilder = Nanopublication.builder();
          inferredNanopublicationBuilder.getAssertionBuilder().getModel().add(assertionsInfModel.getDeductionsModel());
          final Nanopublication inferredNanopublication;
          try {
              inferredNanopublication = inferredNanopublicationBuilder.build();
          } catch (final MalformedNanopublicationException e) {
              throw new IllegalStateException(e);
          }

          nanopublicationsBuilder.add(inferredNanopublication);
      }

      final ImmutableList<Nanopublication> nanopublications = nanopublicationsBuilder.build();
      twks.postNanopublications(nanopublications);

      return true;
  }

  @Override
  public final edu.lehigh.swat.bench.ubt.api.QueryResult issueQuery(final edu.lehigh.swat.bench.ubt.api.Query query) {
      checkOpen();

      final Query parsedQuery = QueryFactory.create(query.getString());

      final long solutionCountFinal;
      try (final TwksTransaction twksTransaction = twks.beginTransaction(ReadWrite.READ)) {
          try (final QueryExecution queryExecution = twksTransaction.queryAssertions(parsedQuery)) {
              final ResultSet resultSet = queryExecution.execSelect();
              long solutionCount = 0;
              while (resultSet.hasNext()) {
                  resultSet.next();
                  solutionCount++;
              }
              solutionCountFinal = solutionCount;
          }
      }
      return new QueryResult() {
          @Override
          public long getNum() {
              return solutionCountFinal;
          }

          @Override
          public boolean next() {
              return --remainingSolutionCount >= 0;
          }

          private long remainingSolutionCount = solutionCountFinal;
      };
  }
}
