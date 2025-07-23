package com.example.hibernate.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.LongType;
import org.hibernate.usertype.EnhancedUserType;

public abstract class AbstractLongIdType<T> implements EnhancedUserType, Serializable {
  private static final long serialVersionUID = 3043727613531815732L;
  private static final LongType LONG_TYPE;
  private static final int[] SQL_TYPES;

  protected abstract Long getId(T var1);

  protected abstract T createId(Long var1);

  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable)value;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    return Objects.equals(x, y);
  }

  public int hashCode(Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

  public boolean isMutable() {
    return false;
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  public String objectToSQLString(Object value) {
    throw new UnsupportedOperationException();
  }

  public String toXMLString(Object value) {
    return value != null ? value.toString() : null;
  }

  public Object fromXMLString(String xmlValue) {
    return this.createId(Long.parseLong(xmlValue));
  }

  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
    Object value = LONG_TYPE.nullSafeGet(rs, names[0], session, owner);
    return value != null ? this.createId((Long)value) : null;
  }

  public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
    LONG_TYPE.nullSafeSet(st, this.getId((T) value), index, session);
  }

  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  static {
    LongType longType = null;

    try {
      longType = (LongType)Class.forName("org.hibernate.type.StandardBasicTypes").getField("LONG").get((Object)null);
    } catch (Throwable var6) {
      try {
        longType = (LongType)Class.forName("org.hibernate.Hibernate").getField("LONG").get((Object)null);
      } catch (Throwable var5) {
        try {
          longType = (LongType)Class.forName("org.hibernate.type.LongType").getField("INSTANCE").get((Object)null);
        } catch (Throwable var4) {
        }
      }
    }

    if (longType == null) {
      throw new IllegalStateException("Unable to find LongType");
    } else {
      LONG_TYPE = longType;
      SQL_TYPES = new int[]{-5};
    }
  }
}
