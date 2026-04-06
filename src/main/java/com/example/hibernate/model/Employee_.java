package com.example.hibernate.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Employee.class)
public class Employee_ {
    public static volatile SingularAttribute<Employee, EmployeeIdImpl> id;
    public static volatile SingularAttribute<Employee, String> name;
    public static volatile SingularAttribute<Employee, Double> salary;
    public static volatile SetAttribute<Employee, WorkingDepartment> workingDepartments;
    public static volatile ListAttribute<Employee, Contact> contacts;
    public static volatile ListAttribute<Employee, Address> addresses;
//    public static volatile SetAttribute<Employee, Contact> contacts;
//    public static volatile SetAttribute<Employee, Address> addresses;
}
