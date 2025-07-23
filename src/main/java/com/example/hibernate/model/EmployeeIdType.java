package com.example.hibernate.model;

public final class EmployeeIdType
  extends AbstractLongIdType<EmployeeIdImpl>
{


  @Override
  protected EmployeeIdImpl createId(final Long value)
  {
    return new EmployeeIdImpl(value);
  }

  @Override
  protected Long getId(EmployeeIdImpl value)
  {
    return ((value != null) ? value.getValue() : null);
  }

  @Override
  public Class<?> returnedClass()
  {
    return EmployeeIdImpl.class;
  }
}
