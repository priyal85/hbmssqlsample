package com.example.hibernate.custome;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.GeneratedCollectionAliases;
import org.hibernate.loader.Loader;
import org.hibernate.loader.MultipleBagFetchException;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.BagType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalCollectionInitializer implements CollectionInitializer
{
  private static final Logger log = LoggerFactory.getLogger(ExternalCollectionInitializer.class);
  private final SessionFactoryImplementor factory;
  private final ExternalCollectionPersister collectionPersister;
  private CollectionAliases[] collectionAliases;
  private final SimpleLoader delegate;

  public ExternalCollectionInitializer(final ExternalCollectionPersister entityPersisters)
  {
    this.collectionPersister = entityPersisters;
    this.factory = collectionPersister.getFactory();
    this.delegate = new SimpleLoader(this.factory);

    postInstantiate();
  }

  @Override
  public void initialize(final Serializable id, final SharedSessionContractImplementor session) throws HibernateException
  {
    loadCollection(session, id, getKeyType());
  }

  @SuppressWarnings("deprecation")
  public final void loadCollection(final SharedSessionContractImplementor session,
                                   final Serializable id,
                                   final Type type) throws HibernateException
  {
    if (log.isDebugEnabled())
    {
      log.debug("loading collection: " +
                MessageHelper.collectionInfoString(getCollectionPersister(), id, getFactory()));
    }

    final Serializable[] ids = new Serializable[] { id };
    final QueryParameters qp = new QueryParameters(new Type[] { type }, ids, ids);

    try
    {
      doQueryAndInitializeNonLazyCollections(session,
                                             qp);
    }
    catch ( SQLException sqle ) {
      throw factory.getSQLExceptionHelper().convert(
        sqle,
        "could not initialize a collection batch: " +
        MessageHelper.collectionInfoString(getCollectionPersisters()[0], ids, getFactory() ),
        getCollectionPersister().getSqlSelectString()
      );
    }

    log.debug("done loading collection");
  }

  private Object doQueryAndInitializeNonLazyCollections(final SharedSessionContractImplementor session,
                                                        final QueryParameters qp) throws SQLException
  {
    //TODO handles the read only
    final PersistenceContext persistenceContext = session.getPersistenceContext();
    final boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();

    persistenceContext.beforeLoad();

    final Object result;

    try
    {
      try
      {
        result = doQuery(session,
                         qp);
      }
      finally
      {
        persistenceContext.afterLoad();
      }

      persistenceContext.initializeNonLazyCollections();
    }
    finally
    {
      // Restore the original default
      persistenceContext.setDefaultReadOnly(defaultReadOnlyOrig);
    }

    log.debug("done entity load");

    return result;
  }

  private Object doQuery(final SharedSessionContractImplementor session, final QueryParameters qp) throws SQLException
  {
    Object result = null;

    //TODO yuk! Is there a cleaner way to access the id?
    final Serializable id =
      (qp.getOptionalId() != null) ? qp.getOptionalId() : qp.getCollectionKeys()[0];
    PreparedStatement st = null;
    ResultSet resultset = null;

    try
    {
      final String sqlStatement = getCollectionPersister().getSqlSelectString();

      // Processing query filters.
      qp.processFilters(sqlStatement, session);

      // Applying LIMIT clause.
      final LimitHandler limitHandler = delegate.getLimitHandler(qp.getRowSelection());
      final String sql = limitHandler.processSql(qp.getFilteredSQL(), qp.getRowSelection());

      st = delegate.prepareQueryStatementPublic(sql, qp, limitHandler, false, session);

      getCollectionPersister().getKeyType().nullSafeSet(st, id, 1, session);
      resultset =
        delegate.getResultSetPublic(st, qp.getRowSelection(), limitHandler, qp.hasAutoDiscoverScalarTypes(), session);
      handleEmptyCollections(qp.getCollectionKeys(), resultset, session);

      //for each element in resultset
      while (resultset.next())
      {
        result = getRowFromResultSet(resultset, session);
      }
    }
    finally
    {
      session.getJdbcCoordinator().getResourceRegistry().release(st);
      session.getJdbcCoordinator().afterStatementExecution();
    }

    //end of for each element in resultset
    initializeEntitiesAndCollections(resultset, session);
    resultset.close();

    return result;
  }

  private Object getRowFromResultSet(final ResultSet resultset, final SharedSessionContractImplementor session) throws SQLException
  {
    final Object[] row = null;
    readCollectionElements(row, resultset, session);

    return row;
  }

  /*
   * Copied and adapted from Loader#readCollectionElements
   */
  private void readCollectionElements(final Object[] row, final ResultSet resultSet, final SharedSessionContractImplementor session)
    throws HibernateException, SQLException
  {
    final CollectionPersister[] collectionPersisters = getCollectionPersisters();

    if (collectionPersisters != null)
    {
      //we don't load more than one instance per row, shortcircuiting it for the moment
      final int[] collectionOwners = null;

      for (int i = 0; i < collectionPersisters.length; i++)
      {
        final CollectionAliases[] descriptors = getCollectionAliases();
        final boolean hasCollectionOwners = false;

        //true if this is a query and we are loading multiple instances of the same collection role
        //otherwise this is a CollectionInitializer and we are loading up a single collection or batch
        final Object owner = hasCollectionOwners ? row[collectionOwners[i]] : null; //if null, owner will be retrieved from session
        final CollectionPersister collectionPersister = collectionPersisters[i];

        readCollectionElement(owner,
                              collectionPersister,
                              descriptors[i],
                              resultSet,
                              session);
      }
    }
  }

  /**
   * Read one collection element from the current row of the JDBC result set.
   */
  private void readCollectionElement(final Object optionalOwner,
                                     final CollectionPersister persister,
                                     final CollectionAliases descriptor,
                                     final ResultSet rs,
                                     final SharedSessionContractImplementor session) throws HibernateException, SQLException
  {
    final PersistenceContext persistenceContext = session.getPersistenceContext();
    final Serializable collectionRowKey =
      (Serializable) persister.readKey(rs,
                                       descriptor.getSuffixedKeyAliases(),
                                       session);

    if (collectionRowKey != null)
    {
      // we found a collection element in the result set
      if (log.isDebugEnabled())
      {
        log.debug("found row of collection: " +
                  MessageHelper.collectionInfoString(persister, collectionRowKey, getFactory()));
      }

      Object owner = optionalOwner;

      if (owner == null)
      {
        owner = persistenceContext.getCollectionOwner(collectionRowKey, persister);
      }

      final PersistentCollection rowCollection =
        persistenceContext.getLoadContexts().getCollectionLoadContext(rs)
          .getLoadingCollection(persister, collectionRowKey);

      if (rowCollection != null)
      {
        rowCollection.readFrom(rs,
                               persister,
                               descriptor,
                               owner);
      }
    }
  }

  protected void postInstantiate()
  {
    final CollectionPersister[] collectionPersisters = getCollectionPersisters();
    List<String> bagRoles = null;

    if (collectionPersisters != null)
    {
      //String[] collectionSuffixes = getCollectionSuffixes();
      collectionAliases = new CollectionAliases[collectionPersisters.length];

      for (int i = 0; i < collectionPersisters.length; i++)
      {
        if (isBag(collectionPersisters[i]))
        {
          if (bagRoles == null)
          {
            bagRoles = new ArrayList<String>();
          }

          bagRoles.add(collectionPersisters[i].getRole());
        }

        collectionAliases[i] = new GeneratedCollectionAliases(collectionPersisters[i], ""); //Don't use suffixes
      }
    }
    else
    {
      collectionAliases = null;
    }

    if ((bagRoles != null) && (bagRoles.size() > 1))
    {
      throw new MultipleBagFetchException(bagRoles);
    }
  }

  private void handleEmptyCollections(final Serializable[] keys,
                                      final ResultSet resultSetId,
                                      final SharedSessionContractImplementor session)
  {
    if (keys != null)
    {
      // this is a collection initializer, so we must create a collection
      // for each of the passed-in keys, to account for the possibility
      // that the collection is empty and has no rows in the result set
      final CollectionPersister[] collectionPersisters = getCollectionPersisters();

      for (int j = 0; j < collectionPersisters.length; j++)
      {
        for (int i = 0; i < keys.length; i++)
        {
          //handle empty collections
          if (log.isDebugEnabled())
          {
            log.debug("result set contains (possibly empty) collection: " +
                      MessageHelper.collectionInfoString(collectionPersisters[j], keys[i], getFactory()));
          }

          session.getPersistenceContext().getLoadContexts().getCollectionLoadContext(resultSetId)
            .getLoadingCollection(collectionPersisters[j], keys[i]);
        }
      }
    }

    // else this is not a collection initializer (and empty collections will
    // be detected by looking for the owner's identifier in the result set)
  }

  /**
   * copied from Loader#initializeEntitiesAndCollections.
   */
  private void initializeEntitiesAndCollections(final ResultSet resultSetId,
                                                final SharedSessionContractImplementor session) throws HibernateException
  {
    final CollectionPersister[] collectionPersisters = getCollectionPersisters();

    if (collectionPersisters != null)
    {
      for (int i = 0; i < collectionPersisters.length; i++)
      {
        if (collectionPersisters[i].isArray())
        {
          endCollectionLoad(resultSetId, session, collectionPersisters[i]);
        }
      }
    }

    if (collectionPersisters != null)
    {
      for (int i = 0; i < collectionPersisters.length; i++)
      {
        if (!collectionPersisters[i].isArray())
        {
          endCollectionLoad(resultSetId, session, collectionPersisters[i]);
        }
      }
    }
  }

  /**
   * copied from Loader#endCollectionLoad.
   */
  private void endCollectionLoad(final ResultSet resultSetId,
                                 final SharedSessionContractImplementor session,
                                 final CollectionPersister collectionPersister)
  {
    //this is a query and we are loading multiple instances of the same collection role
    session.getPersistenceContext().getLoadContexts().getCollectionLoadContext(resultSetId)
      .endLoadingCollections(collectionPersister);
  }

  public CollectionAliases[] getCollectionAliases()
  {
    return collectionAliases;
  }

  public ExternalCollectionPersister getCollectionPersister()
  {
    return collectionPersister;
  }

  public ExternalCollectionPersister[] getCollectionPersisters()
  {
    return new ExternalCollectionPersister[] { collectionPersister };
  }

  private SessionFactoryImplementor getFactory()
  {
    return factory;
  }

  protected Type getKeyType()
  {
    return collectionPersister.getKeyType();
  }

  /**
   * copied from BasicLoader#getResultSet.
   */
  private boolean isBag(final CollectionPersister collectionPersister)
  {
    return collectionPersister.getCollectionType().getClass().isAssignableFrom(BagType.class);
  }

  private class SimpleLoader extends Loader
  {
    public SimpleLoader(SessionFactoryImplementor factory)
    {
      super(factory);
    }

    /**
     * This method is used to give access to super.getLimitHandler, which is is protected.
     */
    @Override
    public LimitHandler getLimitHandler(RowSelection selection)
    {
      return super.getLimitHandler(selection);
    }

    /**
     * This method is used to give access to super.prepareQueryStatement, which is is protected and final
     */
    public PreparedStatement prepareQueryStatementPublic(String sql,
                                                         final QueryParameters queryParameters,
                                                         final LimitHandler limitHandler,
                                                         final boolean scroll,
                                                         final SharedSessionContractImplementor session)
      throws SQLException, HibernateException
    {
      return super.prepareQueryStatement(sql, queryParameters, limitHandler, scroll, session);
    }

    /**
     * This method is used to give access to super.getResultSetPublic, which is is protected and final
     */
    public final ResultSet getResultSetPublic(final PreparedStatement st,
                                              final RowSelection selection,
                                              final LimitHandler limitHandler,
                                              final boolean autodiscovertypes,
                                              final SharedSessionContractImplementor session)
      throws HibernateException, SQLException
    {
      return super.getResultSet(st, selection, limitHandler, autodiscovertypes, session);
    }

    @Override
    protected Loadable[] getEntityPersisters()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected LockMode[] getLockModes(LockOptions lockOptions)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getSQLString()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected EntityAliases[] getEntityAliases()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected CollectionAliases[] getCollectionAliases()
    {
      throw new UnsupportedOperationException();
    }
  }
}