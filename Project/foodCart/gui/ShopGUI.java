package gui;

/* This is the class for the GUI for managing Coffee Shop sales 
 * and inventory.
 */
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import core.Item;
import core.ItemList;
import core.MessageStore;
import core.Order;
import core.OrderList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ShopGUI {

	// GUI elements, shared globally
	private JFrame frame;
	private JPanel mainPanel;
	private JPanel panelNorth;
	private JPanel panelWest;
	private JPanel panelCentre;
	private JPanel panelEast;
	private JPanel BillContainer;
	private ButtonGroup catGrp;
	private ButtonGroup itemGrp;
	private JPanel catPanel;
	private JPanel itemPanel;
	private JTable oneOrder;
	private ActionListener actionListener;
	private DefaultTableModel model;

	// buttons with global functions
	private JButton commitOrder;
	private JButton doTotal;
	private JButton removeItem;
	private JButton addItem;
	private JButton showOrders;
	private JButton printSummary;
	private JButton newOrder;
	// in-memory storage
	private ItemList ItemList;
	private Map<String, String> menuItems;
	private Map<String, String> menuCategories;
	private OrderList OrderList;
	private Item chosenItem;

	// file paths
	private String OrderFile;
	private String MenuFile;

	// columns in Model for orders JTable
	public enum TABCOLS {
		COUNTER(0), DESC(1), QUAN(2), PRICE(3), ID(4);
		private final int value;

		TABCOLS(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	// enum for billing table columns
	public enum BILLCOLS {
		ID(0), NAME(1), QUANTITY(2), PRICE(3), DISCOUNT(4), TOTAL(5);
		private final int value;

		BILLCOLS(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	// sub class actionListener for Category
	class crActionListener implements ActionListener {
		@Override
		/** Listens to the radio buttons. */
		public void actionPerformed(ActionEvent e) {
			chosenItem = null;
			panelWest.remove(itemPanel);
			setMenuButtons(e.getActionCommand());
			panelWest.add(itemPanel);
			panelWest.revalidate();
			panelWest.repaint();
		}

	}

	// sub class actionListener for menu Radiobuttons
	class menuActionListener implements ActionListener {
		@Override
		/** Listens to the radio buttons. */
		public void actionPerformed(ActionEvent e) {

			chosenItem = ItemList.findByID(e.getActionCommand());
		}

	}

	// sub class actionListener for other buttons
	class ButtonsActionListener implements ActionListener {
		@Override
		/** Listens to the radio buttons. */
		public void actionPerformed(ActionEvent e) {

			// to do NEWO LISO SUMM ADDI REMI TOTA BILL
			switch (e.getActionCommand()) {
			case "NEWO":
				setMainPanelDefaultView();
				break;

			case "LISO":
				System.out.print(e.getActionCommand());
			case "SUMM":
				System.out.print(e.getActionCommand());
			case "ADDI":
				addItem();
				break;
			case "REMI":
				removeItem();
				break;
			// case "TOTA":
			// System.out.print(e.getActionCommand());
			case "BILL":
				generateBill();
				break;
			}
		}

	}

	// methods
	private void dialog(String msg, int msgtyp) {
		JOptionPane.showMessageDialog(null, msg, "Message", msgtyp);
	}

	private void reportException(Exception e, String method) {
		System.out.print("Error in method:" + method + ":" + e.getMessage());
		e.printStackTrace();
		dialog("Exception reached: Please contact FoodCart Support", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Apply discounts and generate bill
	 */
	private void generateBill() {
		if (!(model.getRowCount() > 0)) {
			dialog("Please add items to the Order list before generating bill", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		workingOrder wo;
		int quantity = 0;
		int counter = 0;
		int counter1 = 0;
		int counter3 = 0;
		// temp container for orders - category c1 and c2
		Map<Integer, workingOrder> c1 = new HashMap<Integer, workingOrder>();
		// Map<Integer, workingOrder> c2 = new HashMap<Integer,
		// workingOrder>();//temp container for orders, category c2
		// temp container for orders - category c1 and c3
		Map<Integer, workingOrder> c3 = new HashMap<Integer, workingOrder>();
		// temp container after applying discount
		Map<Integer, workingOrder> currOrder = new HashMap<Integer, workingOrder>();
		try {
			// add all orders in Model to C1, C2 or C3
			for (int row = 0; row < model.getRowCount(); row++) {
				quantity = Integer.parseInt(model.getValueAt(row, TABCOLS.QUAN.getValue()).toString());
				while (quantity > 0) {
					wo = new workingOrder();
					wo.item = model.getValueAt(row, TABCOLS.DESC.getValue()).toString();
					wo.category = model.getValueAt(row, TABCOLS.ID.getValue()).toString();
					wo.category = wo.category.substring(wo.category.length() - 2);
					wo.price = Double.parseDouble(model.getValueAt(row, TABCOLS.PRICE.getValue()).toString());
					wo.quantity = 1;
					wo.discount = 0;
					if (wo.category.equals("C1") || wo.category.equals("C1")) {
						c1.put(counter1, wo);
						counter1++;
					} else {
						if (wo.category.equals("C3")) {
							c3.put(counter3, wo);
							counter3++;
						}
					}

					quantity--;

				}

			}
		} catch (Exception e) {
			// unknown exception, so report this
			reportException(e, "generateBill/currOrder");
		}
		// apply discount. Any C1/C2 + C3 gets a 20% discount on C3.
		// we know we have number of items = counter .
		counter = 0;
		workingOrder c3wo;
		workingOrder cwo;
		boolean isFound = false;
		// process C3 first
		for (Entry<Integer, workingOrder> c3entry : c3.entrySet()) {
			c3wo = c3entry.getValue();
			// if any c1 or c 2 item is available, give 20% discount on C3
			if (c1.size() > 0) {
				// move one of the c1 items to currOrder, and remove it from c1.
				for (Entry<Integer, workingOrder> entry : c1.entrySet()) {
					cwo = entry.getValue();
					// check if current order has same entry, then update
					// quantity, else put
					isFound = false;
					// find and update.
					for (Entry<Integer, workingOrder> coentry : currOrder.entrySet()) {
						if (coentry.getValue().item.equals(cwo.item)) {
							isFound = true;
							int cic = coentry.getKey();
							workingOrder ciwo = coentry.getValue();
							ciwo.quantity = ciwo.quantity + cwo.quantity;
							currOrder.remove(cic);
							currOrder.put(cic, ciwo);// update new quantity
							break;
						}

					}
					if (!isFound) {
						// not found so increment counter and add items
						currOrder.put(counter, cwo);
						counter++;
					}
					// in either case above, we remove item from c1
					c1.remove(entry.getKey());
					break;
				}
				// add the discounted item
				c3wo.discount = 0.2; // 20% discount.
				currOrder.put(counter, c3wo);// updated
				counter++;
			} else {
				// add the non discounted item
				isFound = false;
				// if found increment quantity
				for (Entry<Integer, workingOrder> coentry : currOrder.entrySet()) {
					if (coentry.getValue().item.equals(c3wo.item) && (coentry.getValue().discount == c3wo.discount)) {
						isFound = true;
						int cic = coentry.getKey();
						workingOrder ciwo = coentry.getValue();
						ciwo.quantity = ciwo.quantity + c3wo.quantity;
						currOrder.remove(cic);
						currOrder.put(cic, ciwo);// update new quantity
						break;
					}
				}
				if (!isFound) {
					// not found so increment counter and add items
					currOrder.put(counter, c3wo);
					counter++;
				}

			}

		}
		// now process any remaining c1.
		for (Entry<Integer, workingOrder> entry : c1.entrySet()) {
			cwo = entry.getValue();
			isFound = false;
			// if item already there, increment quantity
			for (Entry<Integer, workingOrder> coentry : currOrder.entrySet()) {
				if (coentry.getValue().item.equals(cwo.item)) {
					isFound = true;
					int cic = coentry.getKey();
					workingOrder ciwo = coentry.getValue();
					// if required, we can add a buy 1, get one free discount
					// here, but not now.
					// we increment quantity for now
					ciwo.quantity = ciwo.quantity + cwo.quantity;
					currOrder.remove(cic);
					currOrder.put(cic, ciwo);// update new quantity
					break;
				}
			}
			if (!isFound) {
				// not found so increment counter and add items
				currOrder.put(counter, cwo);
				counter++;
			}
		}
		DefaultTableModel billModel = new javax.swing.table.DefaultTableModel(0, 6);
		Object retObj[];
		int modelrow = 0;
		// currOrder has the final items, now update to OrderList
		double total = 0;
		double grandTotal = 0;
		double order_price = 0;
		// get the last customer in orderlist, and increment
		int customer = OrderList.getLastCustomer() + 1;
		for (Entry<Integer, workingOrder> coentry : currOrder.entrySet()) {
			order_price = coentry.getValue().price;
			total = (coentry.getValue().price - (coentry.getValue().price * coentry.getValue().discount))
					* coentry.getValue().quantity;
			//keep grandtotal
			grandTotal+= total; 
		
			retObj = OrderList.add(customer, coentry.getValue().item, coentry.getValue().quantity,
					coentry.getValue().discount, total);
			if (!(retObj[0] == null)) {
				Order order = (Order) retObj[0];
				// get price from menulist
				/*for (Entry<String, String> mEntry : menuItems.entrySet()) {
					if (mEntry.getValue().equals(order.getItem())) {
						order_price = ItemList.findByID(mEntry.getKey()).getCost();
						break;
					}
				}*/
				billModel.insertRow(modelrow, new Object[] { Integer.toString(order.getID()), order.getItem(), Integer.toString(order.getQuantity()),
						Double.toString(order_price), Double.toString(( order.getDiscount() * order_price )), Double.toString(order.getTotal()) });
				modelrow++;
			} else {
				reportException((Exception) retObj[1], "generateBill:buildModel");
			}
		}
		// add grand total
		if(billModel.getRowCount() > 0){
			billModel.insertRow(modelrow, new Object[]{ "*", "Grand Total", "*", "*", "*", Double.toString(grandTotal)});
		}
		// add to the JPanel BillContainer.
		JLabel Ldate = new JLabel(DateFormat.getDateTimeInstance().format(new Date()));
		JLabel headLabel = new JLabel("Group 6 Coffee Shop");
		headLabel.setHorizontalAlignment(JLabel.CENTER);
		// Set a bigger possible font for the heading
		headLabel.setFont(new Font(headLabel.getFont().getName(), Font.CENTER_BASELINE, 12));
		JPanel headHolder = new JPanel();
		headHolder.setMaximumSize(new Dimension(340, 25));
		headHolder.setAlignmentX(Component.CENTER_ALIGNMENT);
		headHolder.add(headLabel);
		JPanel timeHolder = new JPanel();
		timeHolder.setMaximumSize(new Dimension(340, 20));
		timeHolder.setBackground(new Color(255, 255, 255));
		timeHolder.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeHolder.add(Ldate);

		JPanel CustomerPane = new JPanel(new GridLayout(1, 2));
		CustomerPane.setBackground(new Color(255, 255, 255));
		CustomerPane.setMaximumSize(new Dimension(340, 40));
		JLabel CustLabel = new JLabel("Customer:");
		JLabel CustID = new JLabel(Integer.toString(customer));
		CustID.setFont(new Font(headLabel.getFont().getName(), Font.CENTER_BASELINE, 20));
		CustomerPane.add(CustLabel);
		CustomerPane.add(CustID);
		try {
		// clear any existing bill on screen
			BillContainer.removeAll();
		// build the bill screen	
			BillContainer.add(headHolder);
			BillContainer.add(timeHolder);
			BillContainer.add(CustomerPane);
		} catch (Exception e) {
			reportException(e, "GenerateBill:Update GUI");
		}
		// add the table
		try {
			 JTable oneBill = new JTable(billModel);
			 oneBill.setShowGrid(false);
			 
			// prepare right alignment rendering for certain columns of table.
				DefaultTableCellRenderer rAlignRndr = new DefaultTableCellRenderer();
				rAlignRndr.setHorizontalAlignment(JLabel.RIGHT);
				// set table properties
				TableColumn column = oneBill.getColumnModel().getColumn(BILLCOLS.ID.getValue());
				column.setHeaderValue(new String("Order"));
				column.setMinWidth(30);	
				
				column = oneBill.getColumnModel().getColumn(BILLCOLS.NAME.getValue());
				column.setHeaderValue(new String("Item"));
				column.setMinWidth(120);
				
				column = oneBill.getColumnModel().getColumn(BILLCOLS.QUANTITY.getValue());
				column.setHeaderValue(new String("Nos."));
				column.setCellRenderer(rAlignRndr);
				column.setMinWidth(50);
				
				column = oneBill.getColumnModel().getColumn(BILLCOLS.PRICE.getValue());
				column.setHeaderValue(new String("Price"));
				column.setCellRenderer(rAlignRndr);
				column.setMinWidth(60);
				
				column = oneBill.getColumnModel().getColumn(BILLCOLS.DISCOUNT.getValue());
				column.setHeaderValue(new String("Discount"));
				column.setCellRenderer(rAlignRndr);
				column.setMinWidth(60);
				
				column = oneBill.getColumnModel().getColumn(BILLCOLS.TOTAL.getValue());
				column.setHeaderValue(new String("Total"));
				column.setCellRenderer(rAlignRndr);
				column.setMinWidth(60);
			
				JScrollPane billHolder = new JScrollPane(oneBill);
				billHolder.setBorder(BorderFactory.createTitledBorder("Orders"));
				BillContainer.add(billHolder);
				
			} catch (Exception e) {
				reportException(e, "GenerateBill:BuildTable");
			}
		try{
			mainPanel.removeAll();
			mainPanel.add(panelEast, BorderLayout.WEST);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Remove item from model
	 */
	private void removeItem() {
		try {
			int selectedRowID = oneOrder.getSelectedRow();
			if (selectedRowID >= 0) {
				model.removeRow(selectedRowID);
			} else {
				dialog("Please select an item to remove", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			reportException(e, "removeItem");
		}
	}

	/**
	 * add item to model
	 */
	private void addItem() {
		String colVal;
		boolean exists = false;
		int counter = 0;
		int quantity = 0;
		// get the item count, and add to it
		int count = model.getRowCount();
		try {
			if (!(chosenItem == null)) {
				if (!(count == 0)) {
					// check if this item is already there in the list
					for (int row = 0; row < model.getRowCount(); row++) {
						// check if an item with same description exists
						if (chosenItem.getId().equals(model.getValueAt(row, 4))) {
							// found item, just increment count and price.
							exists = true;
							// get quantity
							colVal = model.getValueAt(row, 2).toString();
							quantity = Integer.parseInt(colVal) + 1;
							model.setValueAt(quantity, row, 2);
						}
					}
					if (exists == false) {
						// this is a new item
						colVal = model.getValueAt(count - 1, 0).toString();
						counter = Integer.parseInt(colVal);
					}
				} else {
					// first item,do nothing here
				}
				// New Item, increment counters and add it here
				if (exists == false) {
					counter++;
					quantity = 1; // default quantity
					model.insertRow(count, new Object[] { counter, chosenItem.getDescription(), quantity,
							chosenItem.getCost(), chosenItem.getId() });
				}
			} else {
				dialog("Please select an item from the Menu", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			reportException(e, "addItem");
		}

	}

	private void setMenuButtons(String Category) {

		String chosenCategory = Category;
		// create instance of menu Action listener.
		ActionListener menuActionListener = new menuActionListener();
		// new JPanel for Radiobuttons of Menu
		itemPanel = new JPanel(new GridLayout(15, 1, 5, 0));
		itemPanel.setBorder(BorderFactory.createTitledBorder("Menu Items"));
		// get the menu items in the chosen category
		menuItems = ItemList.getMenuItems(chosenCategory);
		// start creating radio buttons in the itemPanel
		JRadioButton rbMenuPlaceHolder;
		try {
			for (Entry<String, String> entry : menuItems.entrySet()) {
				rbMenuPlaceHolder = new JRadioButton(entry.getValue());
				rbMenuPlaceHolder.setActionCommand(entry.getKey());
				rbMenuPlaceHolder.addActionListener(menuActionListener);
				itemGrp.add(rbMenuPlaceHolder);
				itemPanel.add(rbMenuPlaceHolder);
			}
		} catch (Exception e) {
			reportException(e, "setMenuButtons(Category = " + Category + ")");
		}
	}

	private void instantiate() {
		// filepaths
		MenuFile = new String("../foodCart/core/menuitems.db");
		OrderFile = new String("../foodCart/core/orderlist.db");
		// instantiate the MenuList
		ItemList = new ItemList(MenuFile);
		menuCategories = ItemList.getMenuCategories();
		// Instantiate the OrderList
		OrderList = new OrderList(OrderFile);
		MessageStore ms = OrderList.getInitExcp();
		if (!(ms == null)) {
			reportException(ms.getExcp(), "OrderList:OrderList()");
		}
		// init the model
		model = new javax.swing.table.DefaultTableModel(0, 5);
		// then listen to buttons
		actionListener = new ButtonsActionListener();
		// Set look and feel
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		// include the image
		ImageIcon icon = new ImageIcon("../foodCart/gui/coffee_shop.gif");
		// new frame for window
		frame = new JFrame();
		// set layout for the frame.
		frame.setLayout(new BorderLayout(2, 2));
		// set output properties of the frame.
		frame.setTitle("FoodCart: Coffee Shop Manager");
		frame.setIconImage(icon.getImage());
		frame.setSize(900, 600);// 1200 width : 600 height
		frame.setLocationRelativeTo(null);// to set to center
	}

	private void prepareNorthPanel() {
		// North Panel
		panelNorth = new JPanel(new GridLayout(0, 1));
		panelNorth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// Add the header
		JLabel headLabel = new JLabel("Group 6 Coffee Shop");
		headLabel.setHorizontalAlignment(JLabel.CENTER);
		// Set a bigger possible font for the heading
		headLabel.setFont(new Font(headLabel.getFont().getName(), Font.CENTER_BASELINE, 14));
		panelNorth.add(headLabel);
		JPanel line1 = new JPanel(new GridLayout(0, 1));
		line1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
				BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLUE)));
		// Also add a clock
		line1.add(new ClockPanel());
		panelNorth.add(line1);
		// Add Action buttons to North

		JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.CENTER));

		newOrder = new JButton("New Order");
		newOrder.addActionListener(actionListener);
		newOrder.setActionCommand("NEWO");
		actionBox.add(newOrder);

		showOrders = new JButton("Show Orders");
		showOrders.addActionListener(actionListener);
		showOrders.setActionCommand("LISO");
		actionBox.add(showOrders);

		printSummary = new JButton("Print Summary");
		printSummary.addActionListener(actionListener);
		printSummary.setActionCommand("SUMM");
		actionBox.add(printSummary);

		panelNorth.add(actionBox);
	}

	private void prepareWestPanel() {
		// West Panel
		panelWest = new JPanel(new GridLayout(1, 2));
		panelWest.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// int cpWidth = panelWest.getWidth();
		int cpHeight = panelWest.getHeight();
		panelWest.getBounds().setSize(20, cpHeight);
		catPanel = new JPanel(new GridLayout(15, 1, 5, 0));
		catPanel.setBorder(BorderFactory.createTitledBorder("Categories"));
		catGrp = new ButtonGroup();
		// Instance of Category Rabdiobutton action listener
		ActionListener catActionListener = new crActionListener();
		// add radio buttons to button group catGrp.
		JRadioButton rbCategoryPlaceholder;
		for (Entry<String, String> entry : menuCategories.entrySet()) {
			rbCategoryPlaceholder = new JRadioButton(entry.getValue());
			rbCategoryPlaceholder.addActionListener(catActionListener);
			catGrp.add(rbCategoryPlaceholder);
			catPanel.add(rbCategoryPlaceholder);

		}
		panelWest.add(catPanel);
		// add the MenuItems Panel
		itemGrp = new ButtonGroup();
		setMenuButtons("");
		panelWest.add(itemPanel);
		panelWest.setPreferredSize(new Dimension(400, 100));
	}

	private void prepareCentrePanel() {

		panelCentre = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 2));
		// panelCentre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
		// 10));
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		JLabel spacer1 = new JLabel(" ");
		spacer1.setMinimumSize(new Dimension(120, 10));
		spacer1.setMaximumSize(new Dimension(120, 10));
		leftPane.add(spacer1);

		addItem = new JButton("Add Item ->");
		addItem.setMaximumSize(new Dimension(120, 25));
		addItem.addActionListener(actionListener);
		addItem.setActionCommand("ADDI");
		leftPane.add(addItem);

		JLabel spacer2 = new JLabel(" ");
		spacer2.setMinimumSize(new Dimension(120, 20));
		spacer2.setMaximumSize(new Dimension(120, 20));
		leftPane.add(spacer2);

		removeItem = new JButton("<- Remove Item");
		removeItem.setMaximumSize(new Dimension(120, 25));
		removeItem.addActionListener(actionListener);
		removeItem.setActionCommand("REMI");
		leftPane.add(removeItem);

		panelCentre.add(leftPane);

		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.PAGE_AXIS));

		// the table
		oneOrder = new JTable(model);
		oneOrder.setShowGrid(false);

		// prepare right alignment rendering for certain columns of table.
		DefaultTableCellRenderer rAlignRndr = new DefaultTableCellRenderer();
		rAlignRndr.setHorizontalAlignment(JLabel.RIGHT);

		// set table properties
		TableColumn column = oneOrder.getColumnModel().getColumn(0);
		column.setHeaderValue(new String("#"));
		column.setMinWidth(30);

		column = oneOrder.getColumnModel().getColumn(1);
		column.setHeaderValue(new String("Item"));
		column.setMinWidth(120);

		column = oneOrder.getColumnModel().getColumn(2);
		column.setHeaderValue(new String("Nos."));
		column.setCellRenderer(rAlignRndr);
		column.setMinWidth(50);

		column = oneOrder.getColumnModel().getColumn(3);
		column.setHeaderValue(new String("Price/Unit"));
		column.setCellRenderer(rAlignRndr);
		column.setMinWidth(60);

		JScrollPane tableHolder = new JScrollPane(oneOrder);
		tableHolder.setBorder(BorderFactory.createTitledBorder("Order"));
		rightPane.add(tableHolder);

		doTotal = new JButton("Total");
		doTotal.setMaximumSize(new Dimension(120, 25));
		doTotal.addActionListener(actionListener);
		doTotal.setActionCommand("TOTA");

		commitOrder = new JButton("        Generate Bill        ");
		commitOrder.setMaximumSize(new Dimension(300, 25));
		commitOrder.addActionListener(actionListener);
		commitOrder.setActionCommand("BILL");

		JPanel billButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

		// billButtons.add(doTotal);
		billButtons.add(commitOrder);

		rightPane.add(billButtons);

		rightPane.setPreferredSize(new Dimension(270, 300));

		panelCentre.add(rightPane);

		panelCentre.setPreferredSize(new Dimension(430, 400));
	}

	private void prepareEastPanel() {
		panelEast = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		BillContainer = new JPanel();
		BillContainer.setLayout(new BoxLayout(BillContainer, BoxLayout.PAGE_AXIS));
		BillContainer.setBorder(BorderFactory.createTitledBorder("Customer Bill"));
		BillContainer.setBackground(new Color(255, 255, 255));
		BillContainer.setPreferredSize(new Dimension(500, 410));

		panelEast.add(BillContainer);

	}

	private void setMainPanelDefaultView(){
		mainPanel.removeAll();
		mainPanel.add(panelWest, BorderLayout.WEST);
		mainPanel.add(panelCentre, BorderLayout.CENTER);
	}
	private void prepareMainPanel() {
		prepareWestPanel();
		prepareCentrePanel();
		prepareEastPanel();
		mainPanel = new JPanel(new BorderLayout(2, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		setMainPanelDefaultView();
		//mainPanel.add(panelEast, BorderLayout.EAST);
	}

	// public methods
	public static void main(String[] args) {
		new ShopGUI();
	}

	public ShopGUI() {
		instantiate();
		prepareNorthPanel();
		prepareMainPanel();
		// add Panels to the Frame.
		frame.add(panelNorth, BorderLayout.NORTH);
		frame.add(mainPanel, BorderLayout.WEST);
		// frame.pack();
		// set visible
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);// set frame to visible

	}

	private class workingOrder {
		String item;
		String category;
		int quantity;
		double discount;
		double price;
	}
}
