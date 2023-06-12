package meshJoin;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class MainClass {

	public static void main(String[] args)
	{
		System.out.println("Enter Password");
		Scanner scan= new Scanner(System.in);
		String pwd=scan.nextLine();   

		
		try
		{
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db","root",pwd);
			Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/DWH","root",pwd);


			String sql="SELECT count(*) FROM db.customers;";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			result.next();
			int cust_start = 0;
			int cust_end = result.getInt(1)/5;
			int counter = cust_end;
			int total1 = result.getInt(1);
			
			sql="SELECT count(*) FROM db.products;";
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			result.next();
			int prod_start = 0;
			int prod_end = result.getInt(1)/5;
			int counter1=prod_end;
			int total2 = result.getInt(1);
			
			sql="SELECT count(*) FROM db.transactions;";
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			result.next();
			int t_start = 0;
			int t_end = result.getInt(1)/5;
			int counter2=t_end;
			
			
			HashMap<String, String> cust = new HashMap<String, String>();
			Queue<ArrayList<String>> q= new LinkedList<ArrayList<String>>();
			
			for(int j=0;true;j++)
			{
				ArrayList<String> id = new ArrayList<String>();
				sql="SELECT * FROM db.transactions LIMIT "+Integer.toString(t_start)+","+Integer.toString(counter2)+";";
				statement = conn.createStatement();
				result = statement.executeQuery(sql);
				
				if (!result.next())
				{
				    System.out.println("no data");
				} 
				else 
				{
					do 
					{
						String tid = result.getString("TRANSACTION_ID");
					    String prodid = result.getString("PRODUCT_ID");
					    String custid = result.getString("CUSTOMER_ID");					    
					    String storeid = result.getString("STORE_ID");
					    String storename = result.getString("STORE_NAME");
					    String timeid = result.getString("TIME_ID");
					    String date = result.getString("T_DATE");
					    String quantity = result.getString("QUANTITY");
					 
					    String key1=tid+"*"+custid;

					    String key2=tid+"*"+prodid;
					    
					    cust.put(key1,tid+'+'+key2+'+'+custid+'+'+storeid+'+'+storename+'+'+timeid+'+'+date + '+'+quantity);
					    cust.put(key2,"");
					    
					    id.add(key1);	
				    } while (result.next());
					q.add(id);	
				}
				System.out.println(q.size()); 
				

				for (ArrayList l : q)
				{
					sql="SELECT * FROM db.customers LIMIT "+Integer.toString(cust_start)+","+Integer.toString(total1)+";";
					statement = conn.createStatement();
					result = statement.executeQuery(sql);
					
					ArrayList<String> li = l;
					while (result.next())
					{
						String custid1 = result.getString("CUSTOMER_ID");
						String custname = result.getString("CUSTOMER_NAME");
						
						for(String i:li)
						{
							
							if(cust.containsKey(i.split("\\*")[0]+"*"+custid1))
							{
								
						    	String temp = cust.get(i.split("\\*")[0]+"*"+custid1);
								cust.put(i.split("\\*")[0]+"*"+custid1,temp+'+'+custname);
							}
						}
		
					}
					
					sql="SELECT * FROM db.products LIMIT "+Integer.toString(prod_start)+","+Integer.toString(total2)+";";
					statement = conn.createStatement();
					result = statement.executeQuery(sql);
					
					while (result.next())
					{
						String prodid = result.getString("PRODUCT_ID");
						String prodname = result.getString("PRODUCT_NAME");
						String suppid = result.getString("SUPPLIER_ID");
						String suppname = result.getString("SUPPLIER_NAME");
						String price = result.getString("PRICE");
						
						for(String i:li)
						{
							String prod = cust.get(i).split("\\+")[1].split("\\*")[0]+"*"+prodid;
							
							if(cust.containsKey(prod));
							{
								cust.put(prod,prodname+'+'+suppid+'+'+suppname+'+'+price);

							}
						}
			
					}
				}

				
				if(j>=4)
				{
					
					ArrayList<String> lis = q.poll();
					String customer = null;
					String products = null;
					
					for(String i:lis)
					{
						customer = cust.get(i);
//						System.out.println(customer);
//						System.out.println(customer.split("\\+")[1].split("\\*")[1]);
//						System.out.println(cust.get(customer.split("\\+")[1]));
						
						
//						System.out.println(customer.split("\\+")[0]);   //transaction id
//						System.out.println(customer.split("\\+")[1]);	//key2
//						System.out.println(customer.split("\\+")[2]);	//customer id
//						System.out.println(customer.split("\\+")[3]);	//store id
//						System.out.println(customer.split("\\+")[4]);	//store name
//						System.out.println(customer.split("\\+")[5]);	// time id
//						System.out.println(customer.split("\\+")[6]);	//time date
//						System.out.println(customer.split("\\+")[7]);  //quantity
//						System.out.println(customer.split("\\+")[8]);   //customer name	
//						
//						
//						
//						
//						System.out.println(customer.split("\\+")[1].split("\\*")[1]);  //product id
//						System.out.println(cust.get(customer.split("\\+")[1]).split("\\+")[0]);  //product name
//						System.out.println(cust.get(customer.split("\\+")[1]).split("\\+")[1]);   //supplier id
//						System.out.println(cust.get(customer.split("\\+")[1]).split("\\+")[2]);     //supplier name
//						System.out.println(cust.get(customer.split("\\+")[1]).split("\\+")[3]);   //product price
						
//						System.out.println("");
//						System.out.println("");
//						
						sql = "INSERT INTO cust_dim VALUES (?,?)";
						PreparedStatement stmt = conn1.prepareStatement(sql);
						stmt.setString(1,customer.split("\\+")[2]);
						stmt.setString(2,customer.split("\\+")[8]);
						try
						{
							stmt.executeUpdate();
						}
						catch(Exception e)
						{
							
						}
						
						sql = "INSERT INTO prod_dim VALUES (?,?,?)";
						stmt = conn1.prepareStatement(sql);
						stmt.setString(1,customer.split("\\+")[1].split("\\*")[1]);
						stmt.setString(2,cust.get(customer.split("\\+")[1]).split("\\+")[0]);
						stmt.setFloat(3,Float.parseFloat(cust.get(customer.split("\\+")[1]).split("\\+")[3]));
						try
						{
							stmt.executeUpdate();
						}
						catch(Exception e)
						{
							
						}
						
						sql = "INSERT INTO store_dim VALUES (?,?)";
						stmt = conn1.prepareStatement(sql);
						stmt.setString(1,customer.split("\\+")[3]); 
						stmt.setString(2,customer.split("\\+")[4]);
						try
						{
							stmt.executeUpdate();
						}
						catch(Exception e)
						{
							
						}
						
						sql = "INSERT INTO supplier_dim VALUES (?,?)";
						stmt = conn1.prepareStatement(sql);
						stmt.setString(1,cust.get(customer.split("\\+")[1]).split("\\+")[1]);
						stmt.setString(2,cust.get(customer.split("\\+")[1]).split("\\+")[2]);
						try
						{
							stmt.executeUpdate();
						}
						catch(Exception e)
						{
							
						}
						
						sql = "INSERT INTO time_dim VALUES (?,?)";
						stmt = conn1.prepareStatement(sql);
						stmt.setString(1,customer.split("\\+")[5]);
						stmt.setDate(2,java.sql.Date.valueOf(customer.split("\\+")[6]));
						try
						{
							stmt.executeUpdate();
						}
						catch(Exception e)
						{
							
						}
						
						sql = "INSERT INTO fact_table VALUES (?,?,?,?,?,?,?,?)";
						stmt = conn1.prepareStatement(sql);
						stmt.setString(1,null);
						stmt.setString(2,customer.split("\\+")[1].split("\\*")[1]);
						stmt.setString(3,customer.split("\\+")[2]);
						stmt.setString(4,customer.split("\\+")[3]);
						stmt.setString(5,cust.get(customer.split("\\+")[1]).split("\\+")[1]);
						stmt.setString(6,customer.split("\\+")[5]);
						stmt.setFloat(7,Float.parseFloat(customer.split("\\+")[7]));
						stmt.setFloat(8,Float.parseFloat(customer.split("\\+")[7]) * Float.parseFloat(cust.get(customer.split("\\+")[1]).split("\\+")[3]));
						stmt.executeUpdate();
						
						cust.remove(cust.get(i).split("\\+")[1]);
						cust.remove(i);	
						
					}
					if(q.isEmpty())
					{
						System.out.println("Execution Finished");
						System.exit(0);
					}
				}
				cust_start = cust_end % total1;  
				if(cust_start==0)
				{
					cust_end = (cust_end +counter) % total1;
				}
				else
				{
					cust_end = (cust_end +counter);
				}
				
				prod_start = (prod_end) % total2;
				if(cust_start==0)
				{
					prod_end = (prod_end +counter1) % total2;
				}
				else
				{
					prod_end = (prod_end +counter1);
				}
				
				t_start=t_end;
				t_end=t_end+counter2;

			}
		
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}