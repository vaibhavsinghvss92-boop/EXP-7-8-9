import java.sql.*;
import java.util.*;

class Emp {
    int empId;
    String name;
    double salary;
    Emp(int empId, String name, double salary) {
        this.empId = empId;
        this.name = name;
        this.salary = salary;
    }
    public String toString() {
        return empId + " | " + name + " | " + salary;
    }
}

class Product {
    int productId;
    String productName;
    double price;
    int quantity;
    Product(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    public String toString() {
        return productId + " | " + productName + " | " + price + " | " + quantity;
    }
}

class Student {
    int studentId;
    String name;
    String department;
    double marks;
    Student(int studentId, String name, String department, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }
    public String toString() {
        return studentId + " | " + name + " | " + department + " | " + marks;
    }
}

public class JDBCMenuApp {
    static final String URL = "jdbc:mysql://localhost:3306/testdb";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            while (true) {
                System.out.println("\n===== JDBC MENU =====");
                System.out.println("1. Fetch Employees");
                System.out.println("2. Product CRUD");
                System.out.println("3. Student Management");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                switch (ch) {
                    case 1: fetchEmployees(con); break;
                    case 2: productCRUD(con, sc); break;
                    case 3: studentManagement(con, sc); break;
                    case 4: con.close(); System.exit(0); break;
                    default: System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fetchEmployees(Connection con) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT EmpID, Name, Salary FROM Employee");
        System.out.println("\n--- Employee Table ---");
        while (rs.next()) {
            System.out.println(rs.getInt("EmpID") + " | " + rs.getString("Name") + " | " + rs.getDouble("Salary"));
        }
    }

    static void productCRUD(Connection con, Scanner sc) throws Exception {
        while (true) {
            System.out.println("\n--- Product CRUD ---");
            System.out.println("1. Insert Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            switch (ch) {
                case 1: insertProduct(con, sc); break;
                case 2: displayProducts(con); break;
                case 3: updateProduct(con, sc); break;
                case 4: deleteProduct(con, sc); break;
                case 5: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void insertProduct(Connection con, Scanner sc) throws Exception {
        String sql = "INSERT INTO Product(ProductID, ProductName, Price, Quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("ID: "); ps.setInt(1, sc.nextInt());
        System.out.print("Name: "); ps.setString(2, sc.next());
        System.out.print("Price: "); ps.setDouble(3, sc.nextDouble());
        System.out.print("Qty: "); ps.setInt(4, sc.nextInt());
        ps.executeUpdate();
        System.out.println("Product Added!");
    }

    static void displayProducts(Connection con) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Product");
        System.out.println("\n--- Product Table ---");
        while (rs.next()) {
            System.out.println(rs.getInt("ProductID") + " | " + rs.getString("ProductName") + " | " + rs.getDouble("Price") + " | " + rs.getInt("Quantity"));
        }
    }

    static void updateProduct(Connection con, Scanner sc) throws Exception {
        String sql = "UPDATE Product SET Price=?, Quantity=? WHERE ProductID=?";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("Enter ID: "); int id = sc.nextInt();
        System.out.print("New Price: "); double price = sc.nextDouble();
        System.out.print("New Qty: "); int qty = sc.nextInt();
        ps.setDouble(1, price); ps.setInt(2, qty); ps.setInt(3, id);
        int rows = ps.executeUpdate();
        if (rows > 0) System.out.println("Updated!"); else System.out.println("Not Found!");
    }

    static void deleteProduct(Connection con, Scanner sc) throws Exception {
        String sql = "DELETE FROM Product WHERE ProductID=?";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("Enter ID: "); ps.setInt(1, sc.nextInt());
        int rows = ps.executeUpdate();
        if (rows > 0) System.out.println("Deleted!"); else System.out.println("Not Found!");
    }

    static void studentManagement(Connection con, Scanner sc) throws Exception {
        while (true) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Marks");
            System.out.println("4. Delete Student");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            switch (ch) {
                case 1: addStudent(con, sc); break;
                case 2: viewStudents(con); break;
                case 3: updateStudent(con, sc); break;
                case 4: deleteStudent(con, sc); break;
                case 5: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void addStudent(Connection con, Scanner sc) throws Exception {
        String sql = "INSERT INTO Student(StudentID, Name, Department, Marks) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("ID: "); ps.setInt(1, sc.nextInt());
        System.out.print("Name: "); ps.setString(2, sc.next());
        System.out.print("Dept: "); ps.setString(3, sc.next());
        System.out.print("Marks: "); ps.setDouble(4, sc.nextDouble());
        ps.executeUpdate();
        System.out.println("Student Added!");
    }

    static void viewStudents(Connection con) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Student");
        System.out.println("\n--- Student Table ---");
        while (rs.next()) {
            System.out.println(rs.getInt("StudentID") + " | " + rs.getString("Name") + " | " + rs.getString("Department") + " | " + rs.getDouble("Marks"));
        }
    }

    static void updateStudent(Connection con, Scanner sc) throws Exception {
        String sql = "UPDATE Student SET Marks=? WHERE StudentID=?";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("Enter ID: "); int id = sc.nextInt();
        System.out.print("New Marks: "); double marks = sc.nextDouble();
        ps.setDouble(1, marks); ps.setInt(2, id);
        int rows = ps.executeUpdate();
        if (rows > 0) System.out.println("Updated!"); else System.out.println("Not Found!");
    }

    static void deleteStudent(Connection con, Scanner sc) throws Exception {
        String sql = "DELETE FROM Student WHERE StudentID=?";
        PreparedStatement ps = con.prepareStatement(sql);
        System.out.print("Enter ID: "); ps.setInt(1, sc.nextInt());
        int rows = ps.executeUpdate();
        if (rows > 0) System.out.println("Deleted!"); else System.out.println("Not Found!");
    }
}
