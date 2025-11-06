// 🌱 SPRING & HIBERNATE DEMO APPLICATION
// Demonstrates: 
// (a) Dependency Injection using Spring
// (b) Hibernate CRUD operations
// (c) Transaction Management using Spring + Hibernate

package com.example.demo;

import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.*;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.util.*;

// =======================
// PART (a): SPRING DEPENDENCY INJECTION
// =======================

class Course {
    private String courseName;
    public Course(String courseName) { this.courseName = courseName; }
    public void displayCourse() { System.out.println("Enrolled Course: " + courseName); }
}

class Student {
    private Course course;
    public Student(Course course) { this.course = course; }
    public void displayInfo() {
        System.out.println("Student is enrolled in:");
        course.displayCourse();
    }
}

// =======================
// PART (b): HIBERNATE CRUD OPERATIONS
// =======================

@Entity
@Table(name = "student")
class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;
    @Column
    private String email;

    public StudentEntity() {}
    public StudentEntity(String name, String email) {
        this.name = name; this.email = email;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return id + " | " + name + " | " + email; }
}

// =======================
// PART (c): SPRING + HIBERNATE TRANSACTION MANAGEMENT
// =======================

@Entity
@Table(name = "account")
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double balance;

    public Account() {}
    public Account(String name, double balance) {
        this.name = name; this.balance = balance;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

// =======================
// DAO AND SERVICE CLASSES
// =======================

@Repository
class AccountDAO {
    private final SessionFactory sessionFactory;
    public AccountDAO(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory; }

    public Account getAccount(int id) {
        return sessionFactory.getCurrentSession().get(Account.class, id);
    }

    public void updateAccount(Account acc) {
        sessionFactory.getCurrentSession().update(acc);
    }
}

@Service
class BankService {
    private final AccountDAO accountDAO;
    public BankService(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        Account from = accountDAO.getAccount(fromId);
        Account to = accountDAO.getAccount(toId);
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountDAO.updateAccount(from);
        accountDAO.updateAccount(to);
        System.out.println("✅ Transaction Successful: " + amount + " transferred from " +
                from.getName() + " to " + to.getName());
    }
}

// =======================
// SPRING CONFIGURATION
// =======================

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.example.demo")
class AppConfig {

    // ----- Beans for Part (a)
    @Bean
    public Course course() { return new Course("Spring Framework with Hibernate"); }

    @Bean
    public Student student() { return new Student(course()); }

    // ----- Beans for Part (b) & (c)
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/demo_db");  // Change DB name if needed
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.example.demo");

        Properties props = new Properties();
        props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        props.put(Environment.SHOW_SQL, "true");
        props.put(Environment.HBM2DDL_AUTO, "update");
        factory.setHibernateProperties(props);

        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}

// =======================
// MAIN APPLICATION
// =======================

public class SpringHibernateDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("\n=== PART (a): SPRING DEPENDENCY INJECTION ===");
        Student student = context.getBean(Student.class);
        student.displayInfo();

        System.out.println("\n=== PART (b): HIBERNATE CRUD OPERATIONS ===");
        SessionFactory factory = context.getBean(SessionFactory.class);
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        StudentEntity s1 = new StudentEntity("Akshat", "akshat@example.com");
        session.save(s1);
        System.out.println("Inserted Student: " + s1);

        List<StudentEntity> list = session.createQuery("from StudentEntity", StudentEntity.class).list();
        System.out.println("All Students:");
        list.forEach(System.out::println);

        s1.setEmail("akshat_updated@example.com");
        session.update(s1);
        System.out.println("Updated Student: " + s1);

        session.delete(s1);
        System.out.println("Deleted Student: " + s1.getId());

        tx.commit();
        session.close();

        System.out.println("\n=== PART (c): SPRING + HIBERNATE TRANSACTION MANAGEMENT ===");
        BankService service = context.getBean(BankService.class);
        service.transferMoney(1, 2, 500);

        context.close();
    }
}
