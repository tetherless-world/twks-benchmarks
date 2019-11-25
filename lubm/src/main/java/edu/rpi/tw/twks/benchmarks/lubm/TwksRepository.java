package edu.rpi.tw.twks.benchmarks.lubm;

public final class TwksRepository
    implements edu.lehigh.swat.bench.ubt.api.Repository {
  //  private RdfSchemaRepository repository_ = null;
//  private QueryResultListener qResultListener_ = new QueryResultListener();
  private String ontology_ = null;

  public TwksRepository() {
//    System.setProperty("org.xml.sax.driver",
//                       "org.apache.xerces.parsers.SAXParser");
  }

  @Override
  public void setOntology(final String ontology) {
    ontology_ = ontology;
  }

  @Override
  public void clear() {
//    if (repository_ == null) {
//      System.err.println("Repository is not open!");
//      return;
//    }
//
//    try {
//      repository_.clearRepository();
//    }
//    catch (SailUpdateException ex) {
//      ex.printStackTrace();
//      System.exit( -1);
//    }
  }

  @Override
  public void open(final String database) {
//    repository_ = new RdfSchemaRepository();
//
//    //initialize the repository
//    Hashtable params = new Hashtable();
//    params.put("jdbcDriver", "com.mysql.jdbc.Driver");
//    params.put("jdbcUrl", database);
//    params.put("user", "sesame");
//    params.put("password", "");
//    try {
//      repository_.initialize(params);
//    }
//    catch (SailInitializationException e) {
//      e.printStackTrace();
//      System.exit(-1);
//    }
  }

  @Override
  public void close() {
//    if (repository_ == null) return;
//    repository_.shutDown();
  }

  @Override
  public boolean load(final String dataPath) {
//    if (repository_ == null) {
//      System.err.println("Repository is not open!");
//      return false;
//    }
//
//    try {
//      SesameUpload upload_ = new SesameUpload(repository_);
//
//      //load ontology first
//      InputStream iStream = new URL(ontology_).openConnection().getInputStream();
//      System.out.println("Start loading ontology");
//      upload_.addRdfModel(iStream, ontology_,
//                          new DummyAdminListener(), RDFFormat.RDFXML, false);
//      System.out.println("End loading ontology");
//
//      File dir = new File(dataPath);
//      String[] list = dir.list();
//      if (list == null) {
//        System.err.println("Invalid data directory: " + dataPath);
//        return false;
//      }
//      for (int i = 0; i < list.length; i++) {
//        String file = dataPath + "/" + list[i];
//        iStream = new FileInputStream(file);
//        System.out.println("[" + (i + 1) + "]Start loading " + file);
//        upload_.addRdfModel(iStream, file /*base url*/, new DummyAdminListener(),
//                            RDFFormat.RDFXML, false);
//        System.out.println("End loading " + file);
//      }
//    }
//    catch (java.io.FileNotFoundException e) {
//      e.printStackTrace();
//      System.exit( -1);
//    }
//    catch (java.io.IOException e) {
//      e.printStackTrace();
//      System.exit( -1);
//    }
//    catch (UpdateException e) {
//      e.printStackTrace();
//      System.exit( -1);
//    }
//
//    return true;
    return true;
  }

  @Override
  public edu.lehigh.swat.bench.ubt.api.QueryResult issueQuery(final edu.lehigh.swat.bench.ubt.api.Query query) {
//    if (repository_ == null) {
//      System.err.println("Repository is not open!");
//      return null;
//    }
//
//    if (query == null) return null;
//    String q = query.getString();
//    if (q.length() <= 0) return null;
//
//    //use RQL engine
//    return issueRql(q);
//    //use SeRQL engine
//    //return issueSerql(q);
    return null;
  }

//  private edu.lehigh.swat.bench.ubt.api.QueryResult issueRql(String q) {
//    RqlEngine qEngine = new RqlEngine(repository_);
//    try {
//      Query rqlQuery = qEngine.parseQuery(q);
//      RqlOptimizer.optimize(rqlQuery);
//      qEngine.evaluateQuery(rqlQuery, qResultListener_);
//    }
//    catch (java.io.IOException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException e) {
//      e.printStackTrace();
//      return null;
//    }
//
//    return qResultListener_;
//  }
//
//  private edu.lehigh.swat.bench.ubt.api.QueryResult issueSerql(String q) {
//    SerqlEngine qEngine = new SerqlEngine(repository_);
//    try {
//      SfwQuery sfwQuery = (SfwQuery)qEngine.parseQuery(q);
//      qEngine.evaluateSelectQuery(sfwQuery, qResultListener_);
//    }
//    catch (java.io.IOException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException e) {
//      e.printStackTrace();
//      return null;
//    }
//
//    return qResultListener_;
//  }
}
