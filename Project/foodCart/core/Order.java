package core;

import java.sql.Timestamp;

public class Order {

	private int id;
	private int customer;
	private String item;
	private int quantity;
	private Timestamp time;
	private double discount;
	private double total;
	
	public Order(int id, int customer, String item, int quantity, Timestamp time, double discount, double total) 
		throws InvalidQuantityException {
		if (quantity < 1) throw new InvalidQuantityException();
		else {
			this.id = id;
			this.customer = customer;
			this.item = item;
			this.quantity = quantity;
			this.time = time;
			this.discount = discount;
			this.total = total;
		}
	}
	
	int getID() { return id;}
	int getCustomer() { return customer; }
	String getItem() { return item; }
	int getQuantity() {return quantity; }
	Timestamp getTime() { return time; }
	double getDiscount() { return discount; }
	double getTotal() { return total; }
	
}
