package biblio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import net.proteanit.sql.DbUtils;

public class Library {
    
	/* Fonction pour la page de connection */
	
    public static void login() throws IOException {
        
        JFrame f=new JFrame("Login");
        JLabel l1,l2,l3,l4,l5;  
        l1=new JLabel("Username");  
        l1.setBounds(30,15, 100,30);  
         
        l2=new JLabel("Password");  
        l2.setBounds(30,50, 100,30);    
         
        l3=new JLabel("ENSAO");
        l3.setFont(new java.awt.Font("Footlight MT Light", 0,32));
        l3.setForeground(new java.awt.Color(0, 102, 102));
        l3.setBounds(360,20, 100,30); 
        
        l4=new JLabel("LIBRARY");
        l4.setFont(new java.awt.Font("Footlight MT Light", 0,20));
        l4.setForeground(new java.awt.Color(0, 102, 102));
        l4.setBounds(360,40, 100,30); 
        
        l5=new JLabel("2020");
        l5.setFont(new java.awt.Font("Footlight MT Light", 0,20));
        l5.setForeground(new java.awt.Color(0, 102, 102));
        l5.setBounds(410,60, 100,30);
        
        JTextField user = new JTextField();
        user.setBounds(110, 15, 200, 30);
             
        JPasswordField pass=new JPasswordField(); 
        pass.setBounds(110, 50, 200, 30);
           
        JButton log=new JButton("Login");
        log.setBounds(130,90,80,25);
        log.addActionListener(new ActionListener() { 
             
            public void actionPerformed(ActionEvent e){ 
     
            String username = user.getText();
			char[] password = pass.getPassword(); 
             
            if(username.equals(""))
            {
                JOptionPane.showMessageDialog(null,"Please enter username"); 
            } 
            else if(password.length == 0) 
            {
                JOptionPane.showMessageDialog(null,"Please enter password"); 
            }
            else { 
                
                Connection connection=connect(); 
                try
                {
                Statement stmt = connection.createStatement();
                  stmt.executeUpdate("USE LIBRARY"); 
                  String st = ("SELECT * FROM USERS WHERE USERNAME='"+username+"' AND PASSWORD='"+String.valueOf(password)+"'"); 
                  ResultSet rs = stmt.executeQuery(st); 
                  if(rs.next()==false) { 
                      JOptionPane.showMessageDialog(null,"Wrong Username or Password!!"); 
                  }
                  else {
                      f.dispose();
                    rs.beforeFirst();
                    while(rs.next())
                    {
                      String admin = rs.getString("ADMIN"); 
                      String UID = rs.getString("UID"); 
                      if(admin.equals("1")) { 
                          admin_menu(); 
                      }
                      else{
                          user_menu(UID); 
                      }
                  }
                  }
                }
                catch (Exception exception) {
                     System.out.println(exception.getMessage());
            }
            }
        }               
        });
        
        f.add(pass);
        f.add(log);
        f.add(user);  
        f.add(l1);  
        f.add(l2); 
        f.add(l3);
        f.add(l4);
        f.add(l5);
        
        f.setSize(500,180);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
         
    }
    
    public static class ex{
        public static int days=0;
            }
    
 
  /* Fonction pour la Connection à la Base de Données */  
    
    public static Connection connect()
    {
    try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/library", "root", "");
            return con;
     } 
     catch (Exception ex) {
            ex.printStackTrace();
     }
    return null;
    }
    
    /* Fonction permettant la création d'une Base de Données et de 
     * 	Quelques tables
     */
    
    public static void create() {
        try {
        Connection connection=connect();
        ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
              
              String databaseName = resultSet.getString(1);
              if(databaseName.equals("library")) {
                  
                  Statement stmt = connection.createStatement();
                  String sql = "DROP DATABASE library";
                  stmt.executeUpdate(sql);
              }
            }
              Statement stmt = connection.createStatement();
               
              String sql = "CREATE DATABASE LIBRARY"; 
              stmt.executeUpdate(sql); 
              stmt.executeUpdate("USE LIBRARY"); 
              String sql1 = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30), ADMIN BOOLEAN)";
              stmt.executeUpdate(sql1);

              stmt.executeUpdate("CREATE TABLE BOOKS(BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, BNAME VARCHAR(50), GENRE VARCHAR(20), AUTHOR VARCHAR(20))");
 
              stmt.executeUpdate("CREATE TABLE ISSUED(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT, BID INT, ISSUED_DATE VARCHAR(20), RETURN_DATE VARCHAR(20), PERIOD INT, FINE INT)");
              
              stmt.executeUpdate("INSERT INTO BOOKS(BNAME, GENRE, AUTHOR) VALUES ('Programmer en Java', 'Programmation', 'John R. Hubbard'),  ('LAlchimiste', 'Roman', 'Paulo Coelho'), ('Comptabilité générale','Economie', 'EL Mouatassim'), ('Miracle Morning', 'Roman', 'Hal Elrod'), ('Civil Engineering Formulas','Science','M. Loftin')");
               
        resultSet.close();
        }
         catch (Exception exception) {
        	 System.out.println(exception.getMessage());
         }   
    
}
   
    /* Fonction pour le menu utilisateur */
     
    public static void user_menu(String UID) {      
        
        JFrame f=new JFrame("User Functions"); 
 
        JButton view = new JButton("Books");  
        view.setBounds(20,20,120,25); 
        view.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e){
                 
                JFrame f = new JFrame("Books Available");                  
                 
                Connection connection = connect();
                String sql = "select * from BOOKS"; 
                try {
                    Statement stmt = connection.createStatement(); 
                    stmt.executeUpdate("USE LIBRARY"); 
                    stmt=connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable book_list = new JTable(); 
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                      
                    JScrollPane scrollPane = new JScrollPane(book_list);
     
                    f.add(scrollPane); 
                    f.setSize(800, 400); 
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                     JOptionPane.showMessageDialog(null, e1);
                }               
                 
        }
        }
        );
         
        JButton my_book=new JButton("My Books");  
        my_book.setBounds(150,20,120,25);
        my_book.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e){
                 
                   
                JFrame f = new JFrame("My Books");
               
                int UID_int = Integer.parseInt(UID); 
     
                Connection connection = connect();
      
                String sql="select distinct issued.*,books.bname,books.genre,books.Author from issued,books " + "where ((issued.uid=" + UID_int + ") and (books.bid in (select bid from issued where issued.uid="+UID_int+"))) group by iid";
                String sql1 = "select bid from issued where uid = "+ UID_int;
                try {
                    Statement stmt = connection.createStatement();

                    stmt.executeUpdate("USE LIBRARY");
                    stmt=connection.createStatement();

                    ArrayList<String> books_list = new ArrayList<String>();
      
                    
                     
                    ResultSet rs=stmt.executeQuery(sql);
                    JTable book_list= new JTable(); 
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
   
                    JScrollPane scrollPane = new JScrollPane(book_list);
     
                    f.add(scrollPane); 
                    f.setSize(800, 400); 
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                     JOptionPane.showMessageDialog(null, e1);
                }               
                     
        }
        }
        );
         
        f.add(my_book); 
        f.add(view);
        f.setSize(300,100);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        }
    
    /* Fonction pour le menu utilisateur */
    
    public static void admin_menu() {
        
        
        JFrame f=new JFrame("Admin Functions");
         
        JButton create=new JButton("Create/Reset");
        create.setBounds(450,60,120,25);
        create.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e){
                 
                create();
                JOptionPane.showMessageDialog(null,"Database Created/Reset!"); 
                 
            }
        });
         
         
        JButton view = new JButton("Books");
        view.setBounds(20,20,120,25);
        view.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                 
                JFrame f = new JFrame("Books Available"); 
                 
                Connection connection = connect(); 
                String sql="select * from BOOKS";
                try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY"); 
                    stmt=connection.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    JTable book_list= new JTable(); 
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 

                    JScrollPane scrollPane = new JScrollPane(book_list); 
     
                    f.add(scrollPane);
                    f.setSize(800, 400); 
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                     JOptionPane.showMessageDialog(null, e1);
                }               
                 
        }
        }
        );
         
        JButton users=new JButton("Users");
        users.setBounds(150,20,120,25);
        users.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e){
                     
                    JFrame f = new JFrame("Users List");
                  
                     
                     
                    Connection connection = connect();
                    String sql="select * from users";
                    try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY"); 
                        stmt=connection.createStatement();
                        ResultSet rs=stmt.executeQuery(sql);
                        JTable book_list= new JTable();
                        book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                        JScrollPane scrollPane = new JScrollPane(book_list);
     
                        f.add(scrollPane); 
                        f.setSize(800, 400);
                        f.setVisible(true);
                        f.setLocationRelativeTo(null);
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }       
                     
                     
        }
            }
        );  
         
        JButton issued = new JButton("Issued Books");
        issued.setBounds(280,20,160,25);
        issued.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                     
                    JFrame f = new JFrame("Users List");                  
                     
                    Connection connection = connect();
                    String sql="select * from issued";
                    try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY");
                        stmt=connection.createStatement();
                        ResultSet rs=stmt.executeQuery(sql);
                        JTable book_list= new JTable();
                        book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                         
                        JScrollPane scrollPane = new JScrollPane(book_list);
                        
                        book_list.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        scrollPane.setViewportView(book_list);
     
                        f.add(scrollPane);
                        f.setSize(800, 400);
                        f.setVisible(true);
                        f.setLocationRelativeTo(null);
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }       
                                 
        }
            }
        );
         
         
        JButton add_user = new JButton("Add User"); 
        add_user.setBounds(20,60,120,25); 
         
        add_user.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                     
                    JFrame g = new JFrame("Enter User Details"); 
                    JLabel l1,l2;  
                    l1=new JLabel("Username"); 
                    l1.setBounds(30,15, 100,30); 
                     
                     
                    l2=new JLabel("Password"); 
                    l2.setBounds(30,50, 100,30); 
                     
                    JTextField user = new JTextField();
                    user.setBounds(110, 15, 200, 30);
                     
                    JPasswordField pass=new JPasswordField();
                    pass.setBounds(110, 50, 200, 30);
                    JRadioButton a1 = new JRadioButton("Admin");
                    a1.setBounds(55, 80, 200,30);
                    JRadioButton a2 = new JRadioButton("User");
                    a2.setBounds(130, 80, 200,30);
                    ButtonGroup bg=new ButtonGroup();    
                    bg.add(a1);bg.add(a2);  
                     
                                     
                    JButton create=new JButton("Create");
                    create.setBounds(130,130,80,25);
                    create.addActionListener(new ActionListener() {
                         
                        public void actionPerformed(ActionEvent e){
                         
                        String username = user.getText();
                        char[] password = pass.getPassword();
                        Boolean admin = false;
                         
                        if(a1.isSelected()) {
                            admin=true;
                        }
                         
                        Connection connection = connect();
                         
                        try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY");
                         stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('"+username+"','"+String.valueOf(password)+"',"+admin+")");
                         JOptionPane.showMessageDialog(null,"User added!!");
                         g.dispose();
                          
                        }
                         
                        catch (SQLException e1) {
                             JOptionPane.showMessageDialog(null, e1);
                        }   
                         
                        }
                         
                    });
                         
                     
                        g.add(create);
                        g.add(a2);
                        g.add(a1);
                        g.add(l1);
                        g.add(l2);
                        g.add(user);
                        g.add(pass);
                        g.setSize(350,200);
                        g.setLayout(null); 
                        g.setVisible(true);
                        g.setLocationRelativeTo(null);
                     
                     
        }
        });
             
         
        JButton add_book = new JButton("Add Book"); 
        add_book.setBounds(150,60,120,25); 
         
        add_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                    JFrame g = new JFrame("Enter Book Details");
                    JLabel l1,l2,l3;  
                    l1=new JLabel("Book Name");  
                    l1.setBounds(30,15, 100,30); 
                     
                     
                    l2=new JLabel("Genre");  
                    l2.setBounds(30,53, 100,30); 
                     
                    l3=new JLabel("Author");  
                    l3.setBounds(30,90, 100,30); 
                     
                    JTextField F_bname = new JTextField();
                    F_bname.setBounds(110, 15, 200, 30);
                     
                    JTextField F_genre=new JTextField();
                    F_genre.setBounds(110, 53, 200, 30);
                    JTextField F_Author=new JTextField();
                    F_Author.setBounds(110, 90, 200, 30);
                             
                     
                    JButton create=new JButton("Submit");
                    create.setBounds(130,130,80,25);
                    create.addActionListener(new ActionListener() {
                         
                        public void actionPerformed(ActionEvent e){
           
                        String bname = F_bname.getText();
                        String genre = F_genre.getText();
                        String author = F_Author.getText();
                         
                        Connection connection = connect();
                         
                        try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY");
                         stmt.executeUpdate("INSERT INTO BOOKS(BNAME,GENRE,AUTHOR) VALUES ('"+bname+"','"+genre+"','"+author+"')");
                         JOptionPane.showMessageDialog(null,"Book added!");
                         g.dispose();
                          
                        }
                         
                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                             JOptionPane.showMessageDialog(null, e1);
                        }   
                         
                        }
                         
                    });
                                     
                        g.add(l3);
                        g.add(create);
                        g.add(l1);
                        g.add(l2);
                        g.add(F_bname);
                        g.add(F_genre);
                        g.add(F_Author);
                        g.setSize(350,200);
                        g.setLayout(null); 
                        g.setVisible(true);
                        g.setLocationRelativeTo(null);
                                 
        }
        });
         
         
        JButton issue = new JButton("Issue Book"); 
        issue.setBounds(450,20,120,25); 
         
        issue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                    JFrame g = new JFrame("Enter Details");
                    JLabel l1,l2,l3,l4;  
                    l1=new JLabel("Book ID(BID)"); 
                    l1.setBounds(30,15, 100,30); 
                     
                     
                    l2=new JLabel("User ID(UID)");
                    l2.setBounds(30,53, 100,30); 
                     
                    l3=new JLabel("Period(days)");  
                    l3.setBounds(30,90, 100,30); 
                     
                    l4=new JLabel("Issued Date(DD-MM-YYYY)"); 
                    l4.setBounds(30,127, 150,30); 
                     
                    JTextField F_bid = new JTextField();
                    F_bid.setBounds(110, 15, 200, 30);
                     
                     
                    JTextField F_uid=new JTextField();
                    F_uid.setBounds(110, 53, 200, 30);
                     
                    JTextField F_period=new JTextField();
                    F_period.setBounds(110, 90, 200, 30);
                     
                    JTextField F_issue=new JTextField();
                    F_issue.setBounds(180, 130, 130, 30);   
     
                     
                    JButton create=new JButton("Submit");
                    create.setBounds(130,170,80,25);
                    create.addActionListener(new ActionListener() {
                         
                        public void actionPerformed(ActionEvent e){
                         
                        String uid = F_uid.getText();
                        String bid = F_bid.getText();
                        String period = F_period.getText();
                        String issued_date = F_issue.getText();
     
                        int period_int = Integer.parseInt(period);
                         
                        Connection connection = connect();
                         
                        try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY");
                         stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES ('"+uid+"','"+bid+"','"+issued_date+"',"+period_int+")");
                         JOptionPane.showMessageDialog(null,"Book Issued!");
                         g.dispose();
                          
                        }
                         
                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                             JOptionPane.showMessageDialog(null, e1);
                        }   
                         
                        }
                         
                    });
                         
                     
                        g.add(l3);
                        g.add(l4);
                        g.add(create);
                        g.add(l1);
                        g.add(l2);
                        g.add(F_uid);
                        g.add(F_bid);
                        g.add(F_period);
                        g.add(F_issue);
                        g.setSize(350,250);
                        g.setLayout(null);
                        g.setVisible(true);
                        g.setLocationRelativeTo(null);
                     
                     
        }
        });
         
         
        JButton return_book = new JButton("Return Book");
        return_book.setBounds(280,60,160,25); 
         
        return_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                     
                    JFrame g = new JFrame("Enter Details");
                    JLabel l1,l2,l3,l4;  
                    l1=new JLabel("Issue ID(IID)");  
                    l1.setBounds(30,15, 100,30); 
                    
                     
                    l4=new JLabel("Return Date(DD-MM-YYYY)");  
                    l4.setBounds(30,50, 150,30); 
                     
                    JTextField F_iid = new JTextField();
                    F_iid.setBounds(110, 15, 200, 30);
                     
                     
                    JTextField F_return=new JTextField();
                    F_return.setBounds(180, 50, 130, 30);
                 
     
                    JButton create=new JButton("Return");
                    create.setBounds(130,170,80,25);
                    create.addActionListener(new ActionListener() {
                         
                        public void actionPerformed(ActionEvent e){                 
                         
                        String iid = F_iid.getText();
                        String return_date = F_return.getText();
                         
                        Connection connection = connect();
                         
                        try {
                        Statement stmt = connection.createStatement();
                         stmt.executeUpdate("USE LIBRARY");
                         String date1=null;
                         String date2=return_date; 
                         ResultSet rs = stmt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID="+iid);
                         while (rs.next()) {
                             date1 = rs.getString(1);
                              
                           }
                          
                         try {
                                Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                                Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                                long diff = date_2.getTime() - date_1.getTime();
                                ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                                 
                                 
                            } catch (ParseException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                          
                         stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='"+return_date+"' WHERE IID="+iid);
                         g.dispose();
                          
     
                         Connection connection1 = connect();
                         Statement stmt1 = connection1.createStatement();
                         stmt1.executeUpdate("USE LIBRARY");                
                        ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID="+iid); 
                        String diff=null; 
                        while (rs1.next()) {
                             diff = rs1.getString(1);
                              
                           }
                        int diff_int = Integer.parseInt(diff);
                        if(ex.days > diff_int) { 
                             
                            int fine = (ex.days-diff_int)*10; 
                            stmt1.executeUpdate("UPDATE ISSUED SET FINE="+fine+" WHERE IID="+iid);  
                            String fine_str = ("Fine: Rs. "+fine);
                            JOptionPane.showMessageDialog(null,fine_str);
                             
                        }
     
                         JOptionPane.showMessageDialog(null,"Book Returned!");
                          
                        }
                                 
                         
                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                             JOptionPane.showMessageDialog(null, e1);
                        }   
                         
                        }
                         
                    }); 
                        g.add(l4);
                        g.add(create);
                        g.add(l1);
                        g.add(F_iid);
                        g.add(F_return);
                        g.setSize(350,250);
                        g.setLayout(null);
                        g.setVisible(true);
                        g.setLocationRelativeTo(null);              
        }
        });
         
        f.add(create);
        f.add(return_book);
        f.add(issue);
        f.add(add_book);
        f.add(issued);
        f.add(users);
        f.add(view);
        f.add(add_user);
        f.setSize(600,200);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
         
        }
  
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel( new NimbusLookAndFeel() );
        login();
    }  
    }
    
 