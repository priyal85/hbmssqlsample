package com.example.hibernate.custome;

import java.io.Serializable;
import java.util.Map;

import com.example.hibernate.model.Department;
import com.example.hibernate.model.DepartmentIdImpl;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.spi.PersisterCreationContext;

public class DepartmentlEntityPersister extends ExternalEntityPersisterAdapter
{
  public DepartmentlEntityPersister(PersistentClass persistentClass,
                                    EntityDataAccess cacheAccessStrategy,
                                    NaturalIdDataAccess naturalIdRegionAccessStrategy,
                                    PersisterCreationContext creationContext) throws HibernateException
  {
    super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
    postConstruct( creationContext.getMetadata() );
  }


  @Override
  public Object load(final Serializable id,
                     final Object optionalObject,
                     final LockOptions lockOptions,
                     final SharedSessionContractImplementor session)
    throws HibernateException
  {
    return loadDepartment(id);
  }

  private static Department loadDepartment(Serializable id)
  {
    if(id instanceof DepartmentIdImpl)
    {
      DepartmentIdImpl departmentId = (DepartmentIdImpl) id;
      if(((DepartmentIdImpl) id).getValue() == 51)
      {
        Department department = new Department("Accounting", 1);
        department.setId(departmentId);
        return department;
      }
      else if(((DepartmentIdImpl) id).getValue() == 52)
      {
        Department department = new Department("Welfare", 2);
        department.setId(departmentId);
        return department;
      }
      else if(((DepartmentIdImpl) id).getValue() == 53)
      {
        Department department = new Department("Human Resources", 3);
        department.setId(departmentId);
        return department;
      }
      else
      {
        return null;
      }

    }
    else
    {
      throw new HibernateException("Invalid id type: " + id.getClass().getName());
    }
  }

  public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session, Boolean readOnly)
    throws HibernateException {
  return loadDepartment(id);
  }

}
