package edu.rpi.tw.twks.benchmarks.lubm;

/*
* @author Yuanbo Guo
*/

/* for sesame0.9
import nl.aidministrator.rdf.query.TableQueryResultListener;
import nl.aidministrator.rdf.model.Value;
*/
import org.openrdf.sesame.query.TableQueryResultListener;
import org.openrdf.sesame.query.QueryErrorType;
import org.openrdf.model.Value;

public class QueryResultListener implements TableQueryResultListener, edu.lehigh.swat.bench.ubt.api.QueryResult {
  protected long num_ = 0; //total # of tuples
  private long count_ = 0;

  public QueryResultListener() {
  }

  //////////////////////////////////////////////////////////////////////////
  //TableQueryResultListener interface implementation

  public void endTableQueryResult() {
    count_ = num_;
  }

  public void endTuple() {
  }

  public void reportError(java.lang.String msg) {
    System.out.println(msg);
  }

  public void startTableQueryResult() {
    num_ = 0;
  }

  public void startTableQueryResult(java.lang.String[] columnHeaders) {
    num_ = 0;
  }

  public void startTuple() {
    num_++;
  }

  public void tupleValue(Value value) {
    //System.out.println(value.toString());
  }

  /**
   * For Sesame1.0 and afterward.
   * @param errType
   * @param msg
   */
  public void error(QueryErrorType errType, java.lang.String msg) {
    System.err.println(msg);
    System.exit(-1);
  }

//////////////////////////////////////////////////////////////////////////
//ubt.api.QueryResult interface implementation

  public boolean next() {
    return --count_ >= 0;
  }

  public long getNum() {
    return num_;
  }
}
