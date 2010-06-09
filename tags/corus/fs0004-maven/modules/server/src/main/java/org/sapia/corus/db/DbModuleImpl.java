package org.sapia.corus.db;

import java.io.File;

import org.sapia.corus.admin.exceptions.core.IORuntimeException;
import org.sapia.corus.admin.services.db.DbMap;
import org.sapia.corus.admin.services.db.DbModule;
import org.sapia.corus.annotations.Bind;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.ubik.net.TCPAddress;


/**
 * This class implements the {@link DbModule} interface.
 * 
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=DbModule.class)
public class DbModuleImpl extends ModuleHelper implements DbModule{
  private File   _dbDir;
  private JdbmDb _db;

  /**
   * Constructor for DbModuleImpl.
   */
  public DbModuleImpl() {
    super();
  }
  
  public void setDbDir(String dbDir){
    _dbDir = new File(dbDir);
  }

  @Override
  public void init() throws Exception {
    if (_dbDir != null) {
      String aFilename = new StringBuffer(_dbDir.getAbsolutePath()).
              append(File.separator).append(CorusRuntime.getCorus().getDomain()).
              append("_").append(((TCPAddress)CorusRuntime.getTransport().getServerAddress()).getPort()).
              toString();
      _dbDir = new File(aFilename);

    } else {
      String aFilename = new StringBuffer(CorusRuntime.getCorusHome()).
              append(File.separator).append("db").
              append(File.separator).append(CorusRuntime.getCorus().getDomain()).
              append("_").append(((TCPAddress)CorusRuntime.getTransport().getServerAddress()).getPort()).
              toString();
      _dbDir = new File(aFilename);
    }
    
    if (!_dbDir.exists()) {
      if (!_dbDir.mkdirs()) {
        throw new IllegalStateException("Could not make directory: " + _dbDir.getAbsolutePath());
      }
    }
    
    try{
      _db = JdbmDb.open(_dbDir.getAbsolutePath() + File.separator + File.separator + "database");
    }catch(Exception e){
      throw new IllegalStateException("Could not open database", e);
    }
  }

  public void dispose() {
    if (_db != null) {
      try{
        _db.close();
      }catch(RuntimeException e){}
    }
  }

  /*////////////////////////////////////////////////////////////////////
                        Module INTERFACE METHOD
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.admin.Module#getRoleName()
   */
  public String getRoleName() {
    return DbModule.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                        DbModule INTERFACE METHODS  
  ////////////////////////////////////////////////////////////////////*/

  @Override
  public <K, V> DbMap<K, V> getDbMap(Class<K> keyType, Class<V> valueType, String name){
    try {
      return new CacheDbMap<K, V>(_db.getDbMap(keyType, valueType, name));
    } catch (java.io.IOException e) {
      throw new IORuntimeException(e);
    } 
  }
}
