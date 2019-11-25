package edu.rpi.tw.twks.benchmarks.lubm;

/*
* @author Yuanbo Guo
*/

import edu.rpi.tw.twks.benchmarks.lubm.QueryResultListener;
import org.openrdf.sesame.admin.RdfAdmin;
import org.openrdf.sesame.admin.DummyAdminListener;
import org.openrdf.sesame.admin.SesameUpload;
import org.openrdf.sesame.sailimpl.rdbms.RdfSchemaRepository;
import org.openrdf.sesame.query.TableQueryResultListener;
import org.openrdf.sesame.query.rql.RqlOptimizer;
import org.openrdf.sesame.query.rql.RqlEngine;
import org.openrdf.sesame.query.rql.model.Query;
import org.openrdf.sesame.query.serql.SerqlEngine;
import org.openrdf.sesame.query.serql.model.SfwQuery;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.admin.UpdateException;
import org.openrdf.sesame.query.MalformedQueryException;
import org.openrdf.sesame.constants.RDFFormat;

import java.io.*;
import java.util.*;
import java.net.*;

public class Sesame
    implements edu.lehigh.swat.bench.ubt.api.Repository {
  private RdfSchemaRepository repository_ = null;
  private QueryResultListener qResultListener_ = new QueryResultListener();
  private String ontology_ = null;

  public Sesame() {
    System.setProperty("org.xml.sax.driver",
                       "org.apache.xerces.parsers.SAXParser");
  }

  public void setOntology(String ontology) {
    ontology_ = ontology;
  }

  public void clear() {
    if (repository_ == null) {
      System.err.println("Repository is not open!");
      return;
    }

    try {
      repository_.clearRepository();
    }
    catch (SailUpdateException ex) {
      ex.printStackTrace();
      System.exit( -1);
    }
  }

  public void open(String database) {
    repository_ = new RdfSchemaRepository();

    //initialize the repository
    Hashtable params = new Hashtable();
    params.put("jdbcDriver", "com.mysql.jdbc.Driver");
    params.put("jdbcUrl", database);
    params.put("user", "sesame");
    params.put("password", "");
    try {
      repository_.initialize(params);
    }
    catch (SailInitializationException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void close() {
    if (repository_ == null) return;
    repository_.shutDown();
  }

  public boolean load(String dataPath) {
    if (repository_ == null) {
      System.err.println("Repository is not open!");
      return false;
    }

    try {
      SesameUpload upload_ = new SesameUpload(repository_);

      //load ontology first
      InputStream iStream = new URL(ontology_).openConnection().getInputStream();
      System.out.println("Start loading ontology");
      upload_.addRdfModel(iStream, ontology_,
                          new DummyAdminListener(), RDFFormat.RDFXML, false);
      System.out.println("End loading ontology");

      File dir = new File(dataPath);
      String[] list = dir.list();
      if (list == null) {
        System.err.println("Invalid data directory: " + dataPath);
        return false;
      }
      for (int i = 0; i < list.length; i++) {
        String file = dataPath + "/" + list[i];
        iStream = new FileInputStream(file);
        System.out.println("[" + (i + 1) + "]Start loading " + file);
        upload_.addRdfModel(iStream, file /*base url*/, new DummyAdminListener(),
                            RDFFormat.RDFXML, false);
        System.out.println("End loading " + file);
      }
    }
    catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
      System.exit( -1);
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit( -1);
    }
    catch (UpdateException e) {
      e.printStackTrace();
      System.exit( -1);
    }

    return true;
  }

  public edu.lehigh.swat.bench.ubt.api.QueryResult issueQuery(edu.lehigh.swat.bench.ubt.api.Query query) {
    if (repository_ == null) {
      System.err.println("Repository is not open!");
      return null;
    }

    if (query == null) return null;
    String q = query.getString();
    if (q.length() <= 0) return null;

    //use RQL engine
    return issueRql(q);
    //use SeRQL engine
    //return issueSerql(q);
  }

  private edu.lehigh.swat.bench.ubt.api.QueryResult issueRql(String q) {
    RqlEngine qEngine = new RqlEngine(repository_);
    try {
      Query rqlQuery = qEngine.parseQuery(q);
      RqlOptimizer.optimize(rqlQuery);
      qEngine.evaluateQuery(rqlQuery, qResultListener_);
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
      return null;
    }
    catch (MalformedQueryException e) {
      e.printStackTrace();
      return null;
    }

    return qResultListener_;
  }

  private edu.lehigh.swat.bench.ubt.api.QueryResult issueSerql(String q) {
    SerqlEngine qEngine = new SerqlEngine(repository_);
    try {
      SfwQuery sfwQuery = (SfwQuery)qEngine.parseQuery(q);
      qEngine.evaluateSelectQuery(sfwQuery, qResultListener_);
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
      return null;
    }
    catch (MalformedQueryException e) {
      e.printStackTrace();
      return null;
    }

    return qResultListener_;
  }
}
