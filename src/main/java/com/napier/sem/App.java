package com.napier.sem;

import java.sql.*;

public class App
{
    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Get Employee
        Employee emp = a.getEmployee(255530);

        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }


    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT dept_manager.emp_no as manager_emp_id, employees.first_name,"+
                            "employees.last_name, employees.emp_no, titles.title, salaries.salary,"+
                            "departments.dept_name, titles.from_date, titles.to_date"+
                            "FROM dept_emp"+
                            "JOIN employees ON employees.emp_no = dept_emp.emp_no "+
                            "JOIN departments ON departments.dept_no = dept_emp.dept_no "+
                            "JOIN salaries ON salaries.emp_no = employees.emp_no "+
                            "JOIN titles ON titles.emp_no = employees.emp_no"+
                            "JOIN dept_manager ON dept_manager.emp_no = employees.emp_no"+
                            "WHERE titles.to_date = '9999-01-01'"+
                            "ORDER BY titles.from_date DESC ";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_no");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n");
                            //+ "Manager: " + emp.manager + "\n");
        }
    }

}