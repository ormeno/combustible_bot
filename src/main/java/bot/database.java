package bot;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import bot.bo.ConsultaGas;


//import org.slf4j.Logger;
//import org.slf4j.event.Level;


/** 
 * Conexión básica a la base de datos PostgreSQL.
 *
 * @author Xules You can follow me on my website http://www.codigoxules.org/en
 * Puedes seguirme en mi web http://www.codigoxules.org).
 */
public class database {
	
	public Connection connect() {  
        Connection conn = null;  
        try {  
            // db parameters  
            String url = "jdbc:sqlite:BDCombustible.db";  
            // create a connection to the database  
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);  
            if (conn == null) {
				System.out.println("Failed to make connection SQLite!");
			} else {
				System.out.println("Connection to SQLite has been established.");  
			}
            
             
        } catch (SQLException | ClassNotFoundException e) {  
            System.out.println(e.getMessage());  
            e.printStackTrace();
        }   
        return conn;
    }
	
	public void createNewDatabase() {  
		   
        Connection conn = connect();  
        try {  
            if (conn != null) {  
                DatabaseMetaData meta = conn.getMetaData();  
                System.out.println("The driver name is " + meta.getDriverName());  
                System.out.println("A new database has been created.");  
            }  
   
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally{
        	 try {  
                 if (conn != null) {  
                     conn.close();  
                 }  
             } catch (SQLException ex) {  
                 System.out.println(ex.getMessage());  
             } 
         } 
    } 
	
	
	 public  void createTables() {  
	          
		    String sqlDrop = "DROP TABLE IF EXISTS consultasGas; ";
	        String sqlCreate = "CREATE TABLE consultasGas (\n"  
	                + " id integer PRIMARY KEY,\n"  
	                + " contTotal integer DEFAULT 0 NOT NULL,\n"  
	                + " contSemana integer DEFAULT 0 NOT NULL,\n"  
	                + " diaTopeSemana DATE NOT NULL,\n"  
	                + " contDia integer DEFAULT 0 NOT NULL, \n"  
	                + " diaTope DATE DEFAULT 0 NOT NULL \n"  
	                + ");";  
	        Connection conn = connect();  
	        try{  
	        	Statement stmt = conn.createStatement();  
	            stmt.execute(sqlDrop);
	            Statement stmt2 = conn.createStatement();  
	            stmt2.execute(sqlCreate);  
	            
	        } catch (SQLException e) {  
	            System.out.println(e.getMessage());  
	            e.printStackTrace();
	        } finally{
	        	 try {  
	                 if (conn != null) {  
	                     conn.close();  
	                 }  
	             } catch (SQLException ex) {  
	                 System.out.println(ex.getMessage());  
	                 ex.printStackTrace();
	             } 
	         }
	    }  
	 
	 public void insert() {  
	        String sql = "INSERT INTO consultasGas(contTotal, contSemana, diaTopeSemana, contDia, diaTope) VALUES(1,1,?,1,?)";  
	        Connection conn = connect(); 
	        try{  
	        	Calendar calendar = Calendar.getInstance();
	    	    java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
	    	    int diaSemana = calendar.getFirstDayOfWeek() + 1;
	    	    int target = 7;
	    	    if (target <= diaSemana)  target += 7;

	    	    calendar.setTime(startDate); 
	    	    calendar.add(Calendar.DAY_OF_YEAR, target-diaSemana);  
	    	    java.sql.Date  domingo =  new java.sql.Date(calendar.getTime().getTime()); 

	            PreparedStatement pstmt = conn.prepareStatement(sql);  
	            pstmt.setDate(1, domingo);  
	            pstmt.setDate(2, startDate);  
	            pstmt.executeUpdate();  
	        } catch (SQLException e) {  
	            System.out.println(e.getMessage());  
	            e.printStackTrace();
	        }  finally{
	        	 try {  
	                 if (conn != null) {  
	                     conn.close();  
	                 }  
	             } catch (SQLException ex) {  
	                 System.out.println(ex.getMessage());  
	                 ex.printStackTrace();
	             } 
	         }  
	    }
	 
	 public void actuContadores() throws ParseException {
		    Connection con = null;  
	    	try {    		
	    		con = connect();
	    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    		Date diaTopeSemana = null;
	    		Date diaTope =  null;
	    		Calendar calendar = Calendar.getInstance();
	    	    java.sql.Date hoy = new java.sql.Date(calendar.getTime().getTime());
	    	    Date hoy2 = new java.sql.Date(calendar.getTime().getTime());
	    	    hoy2 = df.parse(hoy2.toString());
	    	    PreparedStatement pstSelect = con.prepareStatement("SELECT diaTopeSemana, diaTope from consultasGas where id = 1 ");
	    		ResultSet rs = pstSelect.executeQuery();
	    		if (rs.next()) {
	    		  diaTopeSemana = df.parse(rs.getDate(1).toString());
	    		  diaTope =  df.parse(rs.getDate(2).toString());
	    		}
	    		String query = "Update consultasGas set contTotal = contTotal + 1, ";
	    		int status1 = hoy2.compareTo(diaTope);
	    		int status2 = hoy2.compareTo(diaTopeSemana);
	    		if (hoy2.compareTo(diaTope)>0) {
	    		  query= query + " contDia = 1, diaTope = ?, ";
	    		} else {
	    			query = query + " contDia=contDia + 1, ";
	    		}
	    		
	    		if (hoy2.compareTo(diaTopeSemana)>0) {
		    		  query= query + " contSemana = 1, diaTopeSemana = ? ";
		    	} else {
		    			query = query + " contSemana=contSemana + 1 ";
		    	}
	    		query = query + " where id= 1";	    		    	   	    	      
	        	PreparedStatement pst = con.prepareStatement(query); 
	        	int i=1;
	        	if (hoy2.compareTo(diaTope)>0) {
	          	  pst.setDate(i++, hoy);
	        	}
	          	if (hoy2.compareTo(diaTopeSemana)>0) {
	          		pst.setDate(i++, hoy);
	          	}
	          	 	pst.execute();                	            
	            pst.close();            
	            } catch (SQLException ex) {
	        	 System.out.println("Error SQL: " + ex);	                 
	            } finally{
	                if(con!=null)
						try {
							con.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	             }
	    }
	 
	 public ConsultaGas selectAll(){  
		    ConsultaGas cons = null;
	        String sql = "SELECT id, contTotal, contSemana, contDia, diaTopeSemana, diaTope FROM consultasGas where id = 1 ";  
	        Connection conn = connect();   
	        try {  
	            Statement stmt  = conn.createStatement();  
	            ResultSet rs    = stmt.executeQuery(sql);  
	            if (rs.next()) {  
	            	cons = new ConsultaGas(rs.getInt(1), rs.getInt(2), rs.getInt(3),rs.getInt(4),rs.getDate(5),rs.getDate(6)); 
	            }  
	            rs.close();
	            stmt.close();
	        } catch (SQLException e) {  
	            System.out.println(e.getMessage());  
	            e.printStackTrace();
	        }  finally{
	        	 try {  
	                 if (conn != null) {  
	                     conn.close();  
	                 }  
	             } catch (SQLException ex) {  
	                 System.out.println(ex.getMessage());  
	                 ex.printStackTrace();
	             } 
	         }  
	        return cons;
	    }  
	 
	
 	 
                  
   }
