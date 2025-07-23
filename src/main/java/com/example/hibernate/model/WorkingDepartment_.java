package com.example.hibernate.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(WorkingDepartment.class)
public class WorkingDepartment_ {
    public static volatile SingularAttribute<WorkingDepartment, Department> department;
    public static volatile SingularAttribute<WorkingDepartment, String> code;
}
