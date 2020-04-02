// NOTE:
// MODIFY CONNECTION INFO:
//      URL / USER / PASS
// IN initializeConnection() TO LINK TO DATABASE
package jbdcproject1;

//STEP 1. Import required packages
import java.sql.*;
import java.util.Scanner;

public class Main {
    static Connection conn = null;

//// Utility Methods //////////////////////////////////////////////////////////////
    /**
     * Input validation for menu selection
     * @param upperBound The last option on the menu
     * @param input A System.in scanner object
     * @return The input validated choice
     */
    public static int menuValidation(final int upperBound, final Scanner input) {
        int userChoice;
        try {
            final String choiceString = input.next();
            userChoice = Integer.parseInt(choiceString);
            if (userChoice > upperBound || userChoice < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.print("ERROR please make a valid selection: ");
            return menuValidation(upperBound, input);
        }
        return userChoice;
    }

    /**
     * Takes a string and returns it underlined
     * @param text The string to underline
     * @return The underlined string
     */
    public static String underlineText(final String text) {
        String underLine = "";
        for (int i = text.length(); i > 0; --i) {
            underLine = underLine + "-";
        }
        return text + "\n" + underLine;
    }
//// Utility Methods //////////////////////////////////////////////////////////////

    /**
     * Generates the SQL to obtain the primary key field for the given table
     * 1 : Groups, 2 : Publishers, 3 : Books, 4: Book with only title
     * @param tableCode The table to query
     * @return The SQL to get the PK
     */
    static String get_pk_sql(int tableCode) {
        String sql = "SELECT ";
        switch (tableCode) {
            case 1:
                sql += "GROUP_NAME FROM WRITINGGROUPS";
                break;
            case 2:
                sql += "PUBLISHER_NAME FROM PUBLISHERS";
                break;
            case 3:
                sql += "GROUP_NAME, BOOK_TITLE FROM BOOKS";
                break;
            case 4:
                sql += "BOOK_TITLE FROM BOOKS";
                break;
        }
        return sql;
    }

    /**
     * Queries and displays the primary key(s) of the selected table
     * 1 : Groups, 2 : Publishers, 3 : Books, 4: Book with only title
     * @param tableCode The identifier for the selected table
     * @return The number of entries
     */
    static int showTablePK(int tableCode) {
        String pkQuery, pk2Query;
        pkQuery = pk2Query = "";
        String pk, pk2, sql;
        int counter = 0;

        switch (tableCode) {
            case 1:
                pkQuery = "GROUP_NAME";
                break;
            case 2:
                pkQuery = "PUBLISHER_NAME";
                break;
            case 3:
                pkQuery = "GROUP_NAME";
                pk2Query = "BOOK_TITLE";
                break;
            case 4:
                pkQuery = "BOOK_TITLE";
                break;
        }
        sql = get_pk_sql(tableCode);
        if (tableCode == 3) {
            System.out.println(underlineText("\nDISPLAYING " + pk2Query + "S AND ASSOCIATED " + pkQuery + "S"));
        } else {
            System.out.println(underlineText("\nDISPLAYING " + pkQuery + "S"));
        }

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql);
            rs.beforeFirst();
            while (rs.next()) {
                pk = rs.getString(pkQuery);
                if (tableCode == 3) {
                    pk2 = rs.getString(pk2Query);
                    System.out.println("   " + (++counter) + ". " + pkQuery + ": " + pk + ", " + pk2Query + ": " + pk2);
                } else {
                    System.out.println("   " + (++counter) + ". " + pkQuery + ": " + pk);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }

    /**
     * Displays all rows of a given table, prompting uer to select one,
     * all data for the row and its associated data is displayed
     * 1 : Groups, 2 : Publishers, 3 : Books
     * @param tableCode The table our specific entry resides
     * @param input A System.in Scanner object
     */
    static void display_all_data(int tableCode, Scanner input){
        String sql, joinKey, joinColumn, itemName, item_sql;
        int numEntries, itemIndex;
        joinColumn = sql = itemName = "";

        switch(tableCode){
            case 1:
                itemName = "Writing Group";
                joinColumn = "GROUP_NAME";
                sql = "SELECT * FROM WritingGroups w INNER JOIN Books b ON b.GROUP_NAME = w.GROUP_NAME" +
                        " INNER JOIN Publishers p on b.PUBLISHER_NAME = p.PUBLISHER_NAME WHERE w.GROUP_NAME" +
                        "=?";
                break;
            case 2:
                itemName = "Publisher";
                joinColumn = "PUBLISHER_NAME";
                sql = "SELECT * from Publishers p INNER JOIN Books b ON b.PUBLISHER_NAME = "
                    + "p.PUBLISHER_NAME INNER JOIN WritingGroups w on w.GROUP_NAME = b.GROUP_NAME "
                    + "WHERE p.publisher_name =?";
                break;
            case 3:
                itemName = "Book";
                joinColumn = "BOOK_TITLE";
                sql = "SELECT * from Books b INNER JOIN Publishers p ON b.publisher_name = p.publisher_name " +
                        "INNER JOIN WritingGroups w on w.group_name = b.group_name " +
                        "WHERE b.book_title =?";
                break;
        }
        item_sql = get_pk_sql(tableCode);
        numEntries = showTablePK(tableCode);
        System.out.print("\nSelect which " + itemName + " to display info for: ");
        itemIndex = menuValidation(numEntries, input);

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(item_sql);
            rs.beforeFirst();
            rs.relative(itemIndex);
            joinKey = rs.getString(joinColumn);
            pstmt.setString(1, joinKey);

            rs = pstmt.executeQuery();
            rs.beforeFirst();
            while(rs.next()){
                String groupname = rs.getString("group_name");
                String booktitle = rs.getString("book_title");
                String publishername = rs.getString("publisher_name");
                String yearpublished = rs.getString("year_published");
                String numberpages = rs.getString("number_pages");
                String publisheraddress = rs.getString("publisher_address");
                String publisherphone = rs.getString("publisher_phone");
                String publisheremail = rs.getString("publisher_email");
                String headwriter = rs.getString("head_writer");
                String yearformed = rs.getString("year_formed");
                String subject = rs.getString("subject");

                System.out.print("Group Name: " + groupname);
                System.out.print(", Book Title: " + booktitle);
                System.out.print(", Publisher Name: " + publishername);
                System.out.print(", Year Published: " + yearpublished);
                System.out.print(", Number Pages: " + numberpages);
                System.out.print(", Publisher Address: " + publisheraddress);
                System.out.print(", Publisher Phone: " + publisherphone);
                System.out.print(", Publisher Email: " + publisheremail);
                System.out.print(", Head Writer: " + headwriter);
                System.out.print(", Year Formed: " + yearformed);
                System.out.print(", Subject: " + subject);
                System.out.println();
            }
        }
        catch(SQLException s) {s.printStackTrace();}
    }

    /**
     * Prompts the user for info to insert a new book, associating
     * it with an existing publisher and writing group
     * @param input A System.in Scanner object
     */
    static void insertBook(Scanner input){
        String groupName, bookTitle, publisherName, yearPublished, numberPages, sql;
        int groupCode, publisherCode;

        System.out.println(underlineText("\nINSERT A NEW BOOK"));
        System.out.print("   Enter the book's title: ");
        input.nextLine();
        bookTitle = input.nextLine();
        System.out.print("   Enter the year the book was published: ");
        yearPublished = input.nextLine();
        System.out.print("   Enter the number of pages in the book: ");
        numberPages = input.nextLine();

        int numGroups = showTablePK(1);
        System.out.print("\nSelect the writing group \"" + bookTitle + "\" is associated with: ");
        groupCode = menuValidation(numGroups, input);
        String group_sql = get_pk_sql(1);

        int numPublishers = showTablePK(2);
        System.out.print("\nSelect the publisher of \"" + bookTitle + "\": ");
        publisherCode = menuValidation(numPublishers, input);
        String publisher_sql = get_pk_sql(2);

        sql = "INSERT INTO BOOKS " + "VALUES(?,?,?,?,?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(group_sql);
            rs.beforeFirst();
            rs.relative(groupCode);
            groupName = rs.getString("GROUP_NAME");

            rs = stmt.executeQuery(publisher_sql);
            rs.beforeFirst();
            rs.relative(publisherCode);
            publisherName = rs.getString("PUBLISHER_NAME");

            pstmt.setString(1, groupName);
            pstmt.setString(2, bookTitle);
            pstmt.setString(3, publisherName);
            pstmt.setString(4, yearPublished);
            pstmt.setString(5, numberPages);
            pstmt.executeUpdate();
            System.out.println("Book was inserted into the table");
        }
        catch(SQLException s){ s.printStackTrace(); }
    }

    /**
     * Prompts the user to select a book to remove from the table
     * and performs the removal
     * @param input System.in Scanner object
     */
    static void removeBook(Scanner input){
        String bookName, groupName;
        String sql = "DELETE from Books " + "WHERE book_title = ? AND group_name = ?";
        int numBooks = showTablePK(3);
        System.out.print("\nSelect which book to remove: ");
        int bookCode = menuValidation(numBooks, input);

        int numGroups = showTablePK(1);
        System.out.print("\nSelect which group the book is associated with: ");
        int groupCode = menuValidation(numGroups, input);

        String book_sql = get_pk_sql(3);
        String group_sql = get_pk_sql(1);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(book_sql);
            rs.beforeFirst();
            rs.relative(bookCode);
            bookName = rs.getString("BOOK_TITLE");
            pstmt.setString(1, bookName);

            rs = stmt.executeQuery(group_sql);
            rs.beforeFirst();
            rs.relative(groupCode);
            groupName = rs.getString("GROUP_NAME");
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
            System.out.println("Book was removed from table");
        }
        catch(SQLException s) { s.printStackTrace();}
    }

    /**
     * Prompts the user for info to insert a new publisher which
     * "replaces" a publisher that the user chooses
     * @param input A System.in Scanner object
     */
    static void insertPublisher(Scanner input){
        int publisherCode;
        System.out.println(underlineText("\nINSERT A NEW PUBLISHER"));
        System.out.print("   Enter the publisher name: ");
        input.nextLine();
        String name = input.nextLine();
        System.out.print("   Enter the publisher address: ");
        String address = input.nextLine();
        System.out.print("   Enter the publisher number: ");
        String number = input.nextLine();
        System.out.print("   Enter the publisher email: ");
        String email = input.nextLine();

        String sql = "INSERT INTO Publishers "
                + "VALUES(?,?,?,?)";
        try{
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, number);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            System.out.println("New publisher was inserted into the table ...\n");

            int numPublishers = showTablePK(2);
            System.out.print("\nSelect the publisher to be updated: ");
            publisherCode = menuValidation(numPublishers, input);
            String publisher_sql = get_pk_sql(2);
            ResultSet rs = stmt.executeQuery(publisher_sql);
            rs.beforeFirst();
            rs.relative(publisherCode);
            String publisherName = rs.getString("PUBLISHER_NAME");

            sql = "UPDATE Books "
                    + "SET publisher_name = ? "
                    + "WHERE publisher_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, publisherName);
            pstmt.executeUpdate();
            System.out.println("Updated Books published by" + publisherName + "to " + name + "...\n");
        }
        catch(SQLException se){ se.printStackTrace(); }
    }

    /**
     * Performs the query or operation selected by the user
     * @param userSelection The entry code selected from Main Menu
     * @param input A System.in scanner used for input
     */
    static void performOperation(int userSelection, Scanner input) {
        if (userSelection < 4) {
            if (userSelection == 3) {
                showTablePK(4);
            } // Displays just book_title and leaves out the associated group
            else {
                showTablePK(userSelection);
            }
        } else if (userSelection < 7) {
            display_all_data((userSelection - 3), input);
        } else { // Queries #7-10
            switch (userSelection) {
                case 7:
                    insertBook(input);
                    break;
                case 8:
                    insertPublisher(input);
                    break;
                case 9:
                    removeBook(input);
                    break;
            }
        }
        System.out.println("\nReturning to Main Menu . . .\n");
        mainMenu(input);
    }

    /**
     * Displays all query options and prompts user
     * for selection
     * @param input A System.in Scanner object
     */
    static void mainMenu(Scanner input) {
        System.out.println(underlineText("JDBC Interface"));
        System.out.println("   1. List all writing groups");
        System.out.println("   2. List all publishers");
        System.out.println("   3. List all book titles\n--");
        System.out.println("   4. List the data for a specific group");
        System.out.println("   5. List the data for a specific publisher"); // includes all the data for the associated books and writing groups.
        System.out.println("   6. List the data for a specific book\n--"); // includes all the data for the associated publisher and writing group.
        System.out.println("   7. Insert a new book");
        System.out.println("   8. Insert a new publisher to replace an existing one");
        System.out.println("   9. Remove a specific book\n--");
        System.out.println("   10. Exit");
        System.out.print("\nEnter your selection: ");

        int userSelection = menuValidation(10, input);
        if(userSelection < 10){ performOperation(userSelection, input);}
        else{
            System.out.println("Goodbye!");
            System.out.println("\nTerminating . . .");
        }
    }

    /**
     * Sets up the connection to the database by initializing the
     * Connection object that is used to generate statements
     */
    static void initializeConnection() {
        // MODIFY THIS FOR YOUR DATABASE
        // JDBC driver name and database URL
        final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
        final String DB_URL = "jdbc:derby://localhost:1527/323_JBDC";
        //  Database credentials
        final String USER = "Student";
        final String PASS = "database";
        try {
            Class.forName(JDBC_DRIVER); //STEP 2: Register JDBC driver
            System.out.print("Connecting to database . . . ");
            conn = DriverManager.getConnection(DB_URL, USER, PASS); //STEP 3: Open a connection
        } catch (SQLException sql) {
            sql.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
        System.out.println("Success\n");
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        initializeConnection();
        mainMenu(input);
        try{conn.close();} catch(SQLException sql){ sql.printStackTrace(); }
        input.close();
    }
}