package com.example.hibernate;
import com.example.hibernate.model.Address;
import com.example.hibernate.model.Contact;
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
import javax.persistence.criteria.Join;
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

                // Create first employee with contacts and addresses
                Employee emp = new Employee("Paul Rifel", 55000);
                emp.setId(new EmployeeIdImpl(11));
                emp.setWorkingDepartments(workingDepartments);

                List<Contact> empContacts = new ArrayList<>();
                empContacts.add(new Contact("0771234567", "paul@example.com", "Mobile"));
                empContacts.add(new Contact("0112345678", "paul.rifel@company.com", "Office"));
                emp.setContacts(empContacts);

                List<Address> empAddresses = new ArrayList<>();
                empAddresses.add(new Address("123 Galle Rd", "Colombo", "Western", "00100", "Sri Lanka", "Home"));
                empAddresses.add(new Address("456 Business Park", "Colombo", "Western", "00100", "Sri Lanka", "Office"));
                emp.setAddresses(empAddresses);

                session.save(emp);

                // Create second employee with different contacts and addresses
                Employee emp2 = new Employee("John Smith", 60000);
                emp2.setId(new EmployeeIdImpl(12));

                List<Contact> emp2Contacts = new ArrayList<>();
                emp2Contacts.add(new Contact("0779876543", "john@example.com", "Mobile"));
                emp2.setContacts(emp2Contacts);

                List<Address> emp2Addresses = new ArrayList<>();
                emp2Addresses.add(new Address("789 Mount Lavinia Rd", "Colombo", "Western", "00200", "Sri Lanka", "Home"));
                emp2.setAddresses(emp2Addresses);

                session.save(emp2);

                // Create third employee
                Employee emp3 = new Employee("Maria Garcia", 58000);
                emp3.setId(new EmployeeIdImpl(13));

                List<Contact> emp3Contacts = new ArrayList<>();
                emp3Contacts.add(new Contact("0775551234", "maria@example.com", "Mobile"));
                emp3Contacts.add(new Contact("0114445678", null, "Office"));
                emp3Contacts.add(new Contact("0769999999", "maria.personal@email.com", "Home"));
                emp3.setContacts(emp3Contacts);

                List<Address> emp3Addresses = new ArrayList<>();
                emp3Addresses.add(new Address("321 Colombo Fort", "Colombo", "Western", "00100", "Sri Lanka", "Office"));
                emp3.setAddresses(emp3Addresses);

                session.save(emp3);

                // Create fourth employee in a different city
                Employee emp4 = new Employee("David Wilson", 52000);
                emp4.setId(new EmployeeIdImpl(14));

                List<Contact> emp4Contacts = new ArrayList<>();
                emp4Contacts.add(new Contact("0332123456", "david@example.com", "Mobile"));
                emp4.setContacts(emp4Contacts);

                List<Address> emp4Addresses = new ArrayList<>();
                emp4Addresses.add(new Address("999 Kandy Rd", "Kandy", "Central", "20000", "Sri Lanka", "Home"));
                emp4.setAddresses(emp4Addresses);

                session.save(emp4);

                // Create fifth employee in Colombo
                Employee emp5 = new Employee("Sarah Johnson", 56000);
                emp5.setId(new EmployeeIdImpl(15));

                List<Contact> emp5Contacts = new ArrayList<>();
                emp5Contacts.add(new Contact("0778888888", "sarah@example.com", "Mobile"));
                emp5Contacts.add(new Contact("0115555555", "sarah.work@company.com", "Office"));
                emp5.setContacts(emp5Contacts);

                List<Address> emp5Addresses = new ArrayList<>();
                emp5Addresses.add(new Address("555 Union Place", "Colombo", "Western", "00100", "Sri Lanka", "Home"));
                emp5Addresses.add(new Address("777 Tech Park", "Colombo", "Western", "00100", "Sri Lanka", "Office"));
                emp5.setAddresses(emp5Addresses);

                session.save(emp5);
            }

            // Fetch employees by department (original query)
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

            System.out.println("\n=== Employees in Accounting Department ===");
            resultList.forEach(e ->
                System.out.println("ID: " + e.getId() + ", Name: " + e.getName() + ", Salary: " + e.getSalary())
            );

            // Query to fetch all employees who live in Colombo
            CriteriaBuilder cbColombo = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cqColombo = cbColombo.createQuery(Employee.class);
            Root<Employee> rootColombo = cqColombo.from(Employee.class);
            Join<Employee, Address> addressJoin = rootColombo.join("addresses");
            Predicate colomboPredicate = cbColombo.equal(addressJoin.get("city"), "Colombo");
            cqColombo.select(rootColombo).where(colomboPredicate).distinct(true);
            List<Employee> colomboEmployees = session.createQuery(cqColombo).getResultList();

            System.out.println("\n=== Employees Living in Colombo ===");
            colomboEmployees.forEach(e -> {
                System.out.println("\nEmployee: " + e.getName() + " (ID: " + e.getId() + ")");
                System.out.println("Salary: " + e.getSalary());
                System.out.println("Contacts: " + e.getContacts());
                System.out.println("Addresses: " + e.getAddresses());
            });

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