package com.example.hibernate.model;

public final class DepartmentIdType
  extends AbstractLongIdType<DepartmentIdImpl>
{


  @Override
  protected DepartmentIdImpl createId(final Long value)
  {
    return new DepartmentIdImpl(value);
  }

  @Override
  protected Long getId(DepartmentIdImpl value)
  {
    return ((value != null) ? value.getValue() : null);
  }

  @Override
  public Class<?> returnedClass()
  {
    return DepartmentIdImpl.class;
  }
}
