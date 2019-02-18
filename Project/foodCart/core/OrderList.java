package core;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.text.*;
import java.time.format.DateTimeParseException;

public class OrderList {

	private TreeMap<Integer,Order> OrderList;
	private String FileName;
	
	public OrderList(String file) {
		FileName = file;//new String("../foodCart/core/orderlist.db");
		OrderList = new TreeMap<Integer,Order>();
		readFile(FileName);
	}
	public OrderList() {
		OrderList = new TreeMap<Integer,Order>();
	}
	// this method adds a give order to the map
	public void add(Order o){
		OrderList.put(o.getID(),o);
	}
	// This method adds a new Order to the OrderList and returns the Order
	public boolean add(int customer, String item, int quantity, double discount, double total) {
		boolean status = false;
		int id = OrderList.lastKey() + 1; //increment order id
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Order newOrder;
		try {
			newOrder = new Order(id, customer, item, quantity, time, discount, total);
			add(newOrder);
			status = true;
		} catch (InvalidQuantityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
		
	public int getSize() {
		return OrderList.size();
	}
	
	private void readFile(String filename) {
		try {
			File f = new File(filename);
			Scanner scanner = new Scanner(f);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine(); 
				if (line.length() != 0) {
					try {
						String [] parts = line.split(",");
						int id = Integer.parseInt(parts[0].trim());
						int cust = Integer.parseInt(parts[1].trim());
						String item = parts[2].trim();
						int qty = Integer.parseInt(parts[3].trim());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Date d = sdf.parse(parts[4].trim());
						Timestamp ts = new Timestamp(d.getTime());
						double disc = Double.parseDouble(parts[5].trim());
						double total = Double.parseDouble(parts[6].trim());
						Order o = new Order(id, cust, item, qty, ts, disc, total);
						this.add(o);	
					}
					catch (NumberFormatException nfe) {
						String error = "Number conversion error in '" + line + "'  - " + nfe.getMessage();
						System.out.println(error);
					}
					catch (DateTimeParseException dtpe) {
						String error = "DateTime conversion error in '" + line + "'  - " + dtpe.getMessage();
						System.out.println(error);
					}
					catch (ArrayIndexOutOfBoundsException air) {
						String error = "Not enough items in  : '" + line + "' index position : " + air.getMessage();
						System.out.println(error);
					}
					catch (NullPointerException npe) {
						String error = "Null value in '" + line + "'  - " + npe.getMessage();
						System.out.println(error);
					}
					catch (Exception e) {
						String error = "Error occured in '" + line + "'  - " + e.getMessage();
						System.out.println(error);
					}
				}
			}
			scanner.close();
		}
		catch (FileNotFoundException fnf){
			 System.out.println( filename + " not found ");
			 System.exit(0);
		 }
	}
}
