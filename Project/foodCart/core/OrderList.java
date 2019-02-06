package core;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.text.*;
import java.time.format.DateTimeParseException;

public class OrderList {

	private TreeMap<Integer,Order> OrderList;
	
	public OrderList() {
		OrderList = new TreeMap<Integer,Order>();
	}
	
	public void add(Order o) {
		OrderList.put(o.getID(),o);
	}
		
	public int getSize() {
		return OrderList.size();
	}
	
	public void readFile(String filename) {
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
