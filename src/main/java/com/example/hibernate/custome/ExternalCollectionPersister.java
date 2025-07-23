package com.example.hibernate.custome;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.type.Type;

/**
 * Collection persister for collection of external entities that are referenced from an non external entity. This includes
 * both one-to-many and many-to-many.
 *
 * @author lars.borg
 *
 */
public class ExternalCollectionPersister extends BasicCollectionPersister
{
  private final String sqlSelectString;
  private boolean embeddable = false;

  public ExternalCollectionPersister(final Collection collection,
                                     final CollectionDataAccess cacheAccessStrategy,
                                     final PersisterCreationContext creationContext) throws MappingException, CacheException
  {
    super(collection, cacheAccessStrategy, creationContext);
    sqlSelectString = generateSelectString();

    if (collection.getElement().getType().isComponentType())
    {
      embeddable = true;
    }
    else if (!collection.getElement().getType().isEntityType())
    {
      throw new IllegalArgumentException("Not an entity: " + collection.getElement().getType());
    }
  }

  @Override
  public void postInstantiate() throws MappingException
  {
    // super.postInstantiate() only sets an initializer, which we don't use since we have our own initialize(...) method.
  }

  @Override
  public void initialize(final Serializable key, final SharedSessionContractImplementor session) throws HibernateException
  {
    final ExternalCollectionInitializer initializer = new ExternalCollectionInitializer(this);
    initializer.initialize(key, session);
  }

  //This method is called from the collections that is being initialized.
  @Override
  public Object readElement(final ResultSet rs,
                            final Object owner,
                            final String[] aliases,
                            final SharedSessionContractImplementor session) throws HibernateException, SQLException
  {
    if (embeddable)
    {
      return getElementType().nullSafeGet(rs, aliases, session, owner);
    }

    final EntityPersister externalPersister = getElementPersister();
    final Type identifierType = externalPersister.getIdentifierType();
    final String entityName = externalPersister.getEntityName();
    final Object externalId = identifierType.nullSafeGet(rs, getElementColumnAliases("")[0], session, owner);

    return ((Session) session).load(entityName, (Serializable) externalId);
  }

  protected String generateSelectString()
  {
    final SimpleSelect simpleSelect = new SimpleSelect(getDialect()).setTableName(getTableName())
      .addCondition(getKeyColumnNames(), "=?").addColumn(getKeyColumnNames()[0], getKeyColumnAliases("")[0]);

    for (int i = 0; i < getElementColumnNames().length; i++)
    {
      simpleSelect.addColumn(getElementColumnNames()[i], getElementColumnAliases("")[i]);
    }

    if (this.hasIndex())
    {
      for (int i = 0; i < getIndexColumnNames().length; i++)
      {
        simpleSelect.addColumn(getIndexColumnNames()[i], getIndexColumnAliases("")[i]);
      }
    }

    return simpleSelect.toStatementString();
  }

  public String getSqlSelectString()
  {
    return sqlSelectString;
  }
}

