import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class MainServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try {
            if ("login".equals(action)) {
                handleLogin(request, out);
            } else if ("employee".equals(action)) {
                handleEmployee(request, out);
            } else if ("attendance".equals(action)) {
                handleAttendance(request, out);
            } else {
                out.println("<h3 style='color:red;'>Unknown action!</h3>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }

    private void handleLogin(HttpServletRequest request, PrintWriter out) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("admin".equals(username) && "1234".equals(password)) {
            out.println("<h2>Welcome, " + username + "!</h2>");
        } else {
            out.println("<h3 style='color:red;'>Invalid login credentials!</h3>");
        }
    }

    private void handleEmployee(HttpServletRequest request, PrintWriter out) throws Exception {
        String empIdParam = request.getParameter("empid");

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/testdb", "root", "password");
        Statement stmt = con.createStatement();
        ResultSet rs;

        if (empIdParam != null && !empIdParam.isEmpty()) {
            rs = stmt.executeQuery("SELECT * FROM Employee WHERE EmpID=" + empIdParam);
        } else {
            rs = stmt.executeQuery("SELECT * FROM Employee");
        }

        out.println("<h2>Employee Details</h2>");
        out.println("<table border='1' cellpadding='5'><tr><th>ID</th><th>Name</th><th>Salary</th></tr>");
        boolean found = false;

        while (rs.next()) {
            found = true;
            out.println("<tr><td>" + rs.getInt("EmpID") + "</td><td>" +
                    rs.getString("Name") + "</td><td>" +
                    rs.getDouble("Salary") + "</td></tr>");
        }

        if (!found)
            out.println("<tr><td colspan='3'>No records found!</td></tr>");

        out.println("</table>");
        con.close();
    }

    private void handleAttendance(HttpServletRequest request, PrintWriter out) throws Exception {
        String studentId = request.getParameter("studentId");
        String date = request.getParameter("date");
        String status = request.getParameter("status");

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/testdb", "root", "password");

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO Attendance (StudentID, Date, Status) VALUES (?, ?, ?)");
        ps.setInt(1, Integer.parseInt(studentId));
        ps.setString(2, date);
        ps.setString(3, status);

        int i = ps.executeUpdate();

        if (i > 0)
            out.println("<h3>Attendance Recorded Successfully!</h3>");
        else
            out.println("<h3 style='color:red;'>Failed to record attendance!</h3>");

        con.close();
    }
}
