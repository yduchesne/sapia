package org.sapia.beeq.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumUserType<E extends Enum<E>> implements UserType,
    ParameterizedType {

  public static final String PARAM_CLASS_NAME = "class";

  private Class<E> clazz = null;

  private String _clazzName = null;

  private E[] _enums = null;

  private static final int[] SQL_TYPES = new int[] { Types.INTEGER };

  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  public void setParameterValues(Properties parameters) {
    _clazzName = parameters.getProperty(EnumUserType.PARAM_CLASS_NAME);

    if (_clazzName == null) {
      throw new HibernateException("Missing param '"
          + EnumUserType.PARAM_CLASS_NAME + "' in " + this.getClass());
    }

    try {
      clazz = (Class<E>) Class.forName(_clazzName);
    } catch (ClassNotFoundException e) {
      throw new HibernateException(
          "Unable to configure EnumUserType for class " + _clazzName, e);
    }
    
    _enums = clazz.getEnumConstants();
  }

  public Class returnedClass() {

    return clazz;
  }

  public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
      throws HibernateException, SQLException {

    E result = null;
    Integer value = (Integer) resultSet.getObject(names[0]);

    if (value != null) {
      result = _enums[value.intValue()];
    } else {
      result = null;
    }

    return result;
  }

  public void nullSafeSet(PreparedStatement preparedStatement, Object value,
      int index) throws HibernateException, SQLException {

    if (value == null) {
      preparedStatement.setNull(index, Types.INTEGER);
    } else if (value instanceof Enum) {
      preparedStatement.setInt(index, ((Enum)value).ordinal());
    } else {
      throw new HibernateException("The value received is not an Enum but a: "
          + value.getClass().toString());
    }

  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public boolean isMutable() {
    return false;
  }

  public Object assemble(Serializable cached, Object owner)
      throws HibernateException {
    if (cached == null) {
      return null;
    }
    return (E) _enums[((Integer) cached).intValue()];
  }

  public Serializable disassemble(Object value) throws HibernateException {
    if (value == null) {
      return null;
    } else if (value instanceof Enum) {

      try {
        return (Serializable) new Integer(((Enum)value).ordinal());
      } catch (Exception e) {
        throw new HibernateException("Unable to disassemble the enum : "
            + value.getClass().toString());
      }
    } else {
      throw new HibernateException("The value received is not a enum but a: "
          + value.getClass().toString());
    }
  }

  public Object replace(Object original, Object target, Object owner)
      throws HibernateException {
    return original;
  }

  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y)
      return true;
    if (null == x || null == y)
      return false;
    return x.equals(y);
  }
}
