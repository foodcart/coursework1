package core;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * 
 */

/**
 * This class is used to record the menu items and read from the already provided text file
 * @author Anwar Kamil
 *
 */
public class ItemList {
	private HashMap<String, Item> ItemList;

	/**
	 * Constructor to create ItemList object / Menu
	 * @param itemList
	 */
	public ItemList(HashMap<String, Item> itemList) {
		ItemList = itemList;
	}
	
	/**
	 * Method to search by Id the item record
	 * @param id
	 * @return Item
	 */
	public Item findByID(String id) {
		return ItemList.get(id); 	
	}
	
	/**
	 * Method to read menu text file.
	 * @param filename
	 */
	public void readFile(String filename) {
		try {
			File f = new File(filename);
			Scanner scanner = new Scanner(f);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine(); 
				if (line.length() != 0) {
					try {
						String parts [] = line.split(",");
						String id = parts[0];
						String category = parts[1];
						String description = parts[2];
						Double cost = Double.parseDouble(parts[3].trim());
						
						Item item = new Item(id,category,description, cost);
						ItemList.put(item.getCategory() + item.getId(), item);
						
					
					}catch (NumberFormatException nfe) {
						String error = "Number conversion error in '" + line + "'  - " + nfe.getMessage();
						System.out.println(error);
					}
					//this catches null values
					catch (NullPointerException npe) {
						String error = "Null value in '" + line + "'  - " + npe.getMessage();
						System.out.println(error);
					}
					//this catches other errors
					catch (Exception e) {
						String error = "Error occured in '" + line + "'  - " + e.getMessage();
						System.out.println(error);
					}
					}
	

			}
			scanner.close();
		}
		//if the file is not found, stop with system exit
		catch (FileNotFoundException fnf){
		System.out.println( filename + " not found ");
		System.exit(0);
		}
	}
	
	/**
	 * This method provides the set for all the hashmap key value pairs of the ItemList created
	 * @param itemlist
	 * @return entryset
	 */
	public Set<HashMap.Entry<String,Item>> getMenu(){
		return ItemList.entrySet();
	}
	
}
