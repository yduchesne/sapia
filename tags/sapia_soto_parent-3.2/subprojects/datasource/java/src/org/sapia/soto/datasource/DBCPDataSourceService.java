package org.sapia.soto.datasource;

import org.apache.commons.dbcp.BasicDataSource;

import org.sapia.soto.Debug;
import org.sapia.soto.util.Param;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class DBCPDataSourceService extends BasicDataSource implements
    DataSourceService {
  private List _connProps = new ArrayList(3);

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    Param p;

    for(int i = 0; i < _connProps.size(); i++) {
      p = (Param) _connProps.get(i);

      if((p.getName() != null) && (p.getValue() != null)) {
        super.addConnectionProperty(p.getName(), p.getValue().toString());
      }
    }
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try {
      super.close();
    } catch(SQLException e) {
      if(Debug.DEBUG) {
        e.printStackTrace();
      }
    }
  }

  public Param createConnectionProperty() {
    Param p = new Param();
    _connProps.add(p);

    return p;
  }
}
