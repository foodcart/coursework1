package core;
/**
 * This Class is to keep a track of the Customer IDs generated for foodCart
 * On Startup, the GUI will initialize this class and set the variable Customer.
 * @author Vimal
 *
 */
public class Customer {
/*Static Customer. Keep a single counter for Customer to ensure only one
 *instance of ShopGUI is running 
 */
private static int Customer;

	public int Customer(int LastCustNumber){
		if (Customer == 0){
			Customer = LastCustNumber;
		}
		return Customer;
	}
	public int getNext(){
		return ++Customer;
	}
}
