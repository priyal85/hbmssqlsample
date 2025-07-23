package com.example.hibernate.custome;
import java.io.Serializable;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.type.Type;

/**
 * Simple implementation of EntityPersister, by extending org.hibernate.persister.entity.AbstractEntityPersister and
 * generating an implementation of all unimplemented methods making the class concrete. This class needs to be extended
 * and is used in order to keep track of which methods that are overridden in the extending class and which methods can
 * use a generated implementation.
 */
public class ExternalEntityPersisterAdapter extends AbstractEntityPersister
{
  @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
  public ExternalEntityPersisterAdapter(final PersistentClass persistentClass,
                                        final EntityDataAccess cacheAccessStrategy,
                                        final NaturalIdDataAccess naturalIdRegionAccessStrategy,
                                        final PersisterCreationContext creationContext)
    throws HibernateException
  {
    super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
  }

  @Override
  public String getSubclassPropertyTableName(final int i)
  {
    return null;
  }

  @Override
  public String fromTableFragment(final String alias)
  {
    return null;
  }

  @Override
  public String getPropertyTableName(final String propertyName)
  {
    return null;
  }

  @Override
  public Type getDiscriminatorType()
  {
    return null;
  }

  @Override
  public Object getDiscriminatorValue()
  {
    return null;
  }

  @Override
  public String getSubclassForDiscriminatorValue(final Object value)
  {
    return null;
  }

  @Override
  public Serializable[] getPropertySpaces()
  {
    return null;
  }

  @Override
  public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias)
  {
    return null;
  }

  @Override
  public String getTableName()
  {
    return null;
  }

  @Override
  public String getDiscriminatorSQLValue()
  {
    return null;
  }

  @Override
  public String[] getConstraintOrderedTableNameClosure()
  {
    return null;
  }

  @Override
  public String[][] getContraintOrderedTableKeyColumnClosure()
  {
    return null;
  }

  @Override
  protected int[] getSubclassColumnTableNumberClosure()
  {
    return null;
  }

  @Override
  protected int[] getSubclassFormulaTableNumberClosure()
  {
    return null;
  }

  @Override
  public String getSubclassTableName(int j)
  {
    return null;
  }

  @Override
  protected String[] getSubclassTableKeyColumns(int j)
  {
    return null;
  }

  @Override
  protected boolean isClassOrSuperclassTable(int j)
  {
    return false;
  }

  @Override
  public int getSubclassTableSpan()
  {
    return 0;
  }

  @Override
  public int getTableSpan()
  {
    return 0;
  }

  @Override
  public boolean isTableCascadeDeleteEnabled(int j)
  {
    return false;
  }

  @Override
  public String getTableName(int j)
  {
    return null;
  }

  @Override
  public String[] getKeyColumns(int j)
  {
    return null;
  }

  @Override
  public boolean isPropertyOfTable(int property, int j)
  {
    return false;
  }

  @Override
  protected int[] getPropertyTableNumbersInSelect()
  {
    return null;
  }

  @Override
  protected int[] getPropertyTableNumbers()
  {
    return null;
  }

  @Override
  protected int getSubclassPropertyTableNumber(int i)
  {
    return 0;
  }

  @Override
  @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
  protected String filterFragment(String alias) throws MappingException
  {
    return null;
  }

  @Override
  protected String filterFragment(String alias, Set<String> treatAsDeclarations)
  {
    return null;
  }

  public String generateSnapshotSelectString() {
    return "";
  }
  public String generateSelectVersionString() {
    return "";
  }

  protected void createLoaders() {
    // No-op, this method should be overridden in the extending class
  }

}
