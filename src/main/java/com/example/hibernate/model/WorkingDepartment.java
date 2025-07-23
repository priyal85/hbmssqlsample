package com.example.hibernate.model;

@javax.persistence.Embeddable
public class WorkingDepartment
{


  @javax.persistence.ManyToOne
    (
      targetEntity = Department.class,
      optional = false

      , fetch = javax.persistence.FetchType.LAZY


    )
    private Department department;



  @javax.persistence.Basic
  @javax.persistence.Column(nullable = false, length = 50 )
  private String code;

  public Department getDepartment()
  {
    return department;
  }

  public void setDepartment(Department department)
  {
    this.department = department;
  }

  public String getCode()
  {
    return code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }
}
