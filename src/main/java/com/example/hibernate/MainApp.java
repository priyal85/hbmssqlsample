package com.example.hibernate;
import com.example.hibernate.model.Department;
import com.example.hibernate.model.DepartmentIdImpl;
import com.example.hibernate.model.Department_;
import com.example.hibernate.model.Employee;
import com.example.hibernate.model.EmployeeIdImpl;
import com.example.hibernate.model.Employee_;
import com.example.hibernate.model.WorkingDepartment;
import com.example.hibernate.model.WorkingDepartment_;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainApp {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        try {

            Set<WorkingDepartment> workingDepartments = new HashSet<>();
            session.beginTransaction();
            if (readBooleanProperty("app.create.data")) {
                // Insert data only if the property is set to true
                Department department = new Department("Accounting", 1);
                department.setId(new DepartmentIdImpl(51));
                session.save(department);

                WorkingDepartment workingDepartment = new WorkingDepartment();
                workingDepartment.setDepartment(department);
                workingDepartment.setCode("A15");
                workingDepartments.add(workingDepartment);

              Department department2 = new Department("Welfare", 2);
                department2.setId(new DepartmentIdImpl(52));
                session.save(department2);

              WorkingDepartment workingDepartment2 = new WorkingDepartment();
              workingDepartment2.setDepartment(department2);
              workingDepartment2.setCode("B70");
              workingDepartments.add(workingDepartment2);


              Employee emp = new Employee("Paul Rifel", 55000);
              emp.setId(new EmployeeIdImpl(11));
              emp.setWorkingDepartments(workingDepartments);
              session.save(emp);

            }





          List<DepartmentIdImpl> departmentIds = new ArrayList<>();
          departmentIds.add(new DepartmentIdImpl(51));
          CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
          Predicate predicate = root
            .join(Employee_.workingDepartments)
            .get(WorkingDepartment_.department)
            .get(Department_.id)
            .in(departmentIds);
            cq.select(root).where(predicate);
            List<Employee> resultList = session.createQuery(cq).getResultList();
            resultList.forEach(e ->
                System.out.println("ID: " + e.getId() + ", Name: " + e.getName() + ", Salary: " + e.getSalary())
            );
            session.getTransaction().commit();
        } finally {
            session.close();
            factory.close();
        }
    }
    //write a method to read a boolean value from a properties file in resource folder named "config.properties".
  //implement the logic to read the property value using java.util.Properties
    //if the property is not found, return false.
    //if the property is found, return true if the value is "true" (case
    //insensitive), otherwise return false.
    public static boolean readBooleanProperty(String propertyName) {
        try {
            java.util.Properties properties = new java.util.Properties();
            properties.load(MainApp.class.getClassLoader().getResourceAsStream("config.properties"));
            String value = properties.getProperty(propertyName);
            return value != null && value.equalsIgnoreCase("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}