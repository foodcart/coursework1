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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;

public class ShopGUI {

	// GUI elements, shared globally
	private JFrame frame;
	private JPanel mainPanel;
	private JPanel panelNorth;
	private JPanel panelEast;
	private JPanel panelWest;
	private JPanel panelSouth;
	private JPanel panelCentre;
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
	private Item chosenItem;

	// sub class actionListener for Category
	class crActionListener implements ActionListener {
		@Override
		/** Listens to the radio buttons. */
		public void actionPerformed(ActionEvent e) {

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
				;

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
			case "TOTA":
				System.out.print(e.getActionCommand());
			case "BILL":
				System.out.print(e.getActionCommand());
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

	private void addItem() {
		String colVal;
		boolean exists = false;
		int counter = 0;
		int quantity = 0;
		//get the item count, and add to it
		int count = model.getRowCount();
		try {
			if (!(chosenItem == null)) {
				if (!(count == 0)) {
					//check if this item is already there in the list
					for(int row = 0; row < model.getRowCount(); row++){
						// check if an item with same description exists
						if(chosenItem.getId().equals(model.getValueAt(row, 4))){
							//found item, just increment count and price.
							exists = true;
							//get quantity
							colVal = model.getValueAt(row, 2).toString();
							quantity = Integer.parseInt(colVal) +  1;
							model.setValueAt(quantity, row, 2);
						}
					}
					if(exists == false){
					// this is a new item	
					colVal = model.getValueAt(count - 1, 0).toString();
					counter = Integer.parseInt(colVal);
					}
				}else{
					//first item,do nothing here
				}
				// New Item, increment counters and add it here
				if(exists == false){
				counter++;
				quantity = 1; //default quantity
				model.insertRow(count,
						new Object[] { counter, chosenItem.getDescription(), quantity, chosenItem.getCost(), chosenItem.getId() });
				}
			}
			else{
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
		try{
		for (Entry<String, String> entry : menuItems.entrySet()) {
			rbMenuPlaceHolder = new JRadioButton(entry.getValue());
			rbMenuPlaceHolder.setActionCommand(entry.getKey());
			rbMenuPlaceHolder.addActionListener(menuActionListener);
			itemGrp.add(rbMenuPlaceHolder);
			itemPanel.add(rbMenuPlaceHolder);
		}
		}
		catch(Exception e){
			reportException(e, "setMenuButtons(Category = " + Category + ")");
		}
	}

	private void instantiate() {
		
		// init the model
		model = new javax.swing.table.DefaultTableModel(0, 5);
		// instantiate the MenuList
		ItemList = new ItemList();
		menuCategories = ItemList.getMenuCategories();
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
		frame.setSize(1200, 600);// 1200 width : 600 height
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
		//int cpWidth = panelWest.getWidth();
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
		
		//the table
		oneOrder = new JTable(model);
		oneOrder.setShowGrid(false);
		
		//prepare right alignment rendering for certain columns of table.
		DefaultTableCellRenderer rAlignRndr = new DefaultTableCellRenderer();
		rAlignRndr.setHorizontalAlignment(JLabel.RIGHT);
		
		//set table properties
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

		//billButtons.add(doTotal);
		billButtons.add(commitOrder);

		rightPane.add(billButtons);

		rightPane.setPreferredSize(new Dimension(270, 300));

		panelCentre.add(rightPane);

		panelCentre.setPreferredSize(new Dimension(430, 400));
	}

	private void prepareMainPanel() {
		prepareWestPanel();
		prepareCentrePanel();
		mainPanel = new JPanel(new BorderLayout(2, 2));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		mainPanel.add(panelWest, BorderLayout.WEST);
		mainPanel.add(panelCentre, BorderLayout.CENTER);
	}

	// public methods
	public static void main(String[] args) {
		ShopGUI gui = new ShopGUI();
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
}
