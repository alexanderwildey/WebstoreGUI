/*  Name: Alexander Wildey
    Date: January 30, 2022
    Project: NileDotCom Webstore with GUI
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class NileDotCom implements ActionListener {

    // Declare the variables that will need to be accessed and edited by multiple methods.
    public static int currentItemNumber = 1;
    public static String itemFound = "";
    public static String currentItem;
    public static String[] currentOrder = new String[10];
    public static double itemTotal = 0;
    public static double subtotal = 0;

    public static JTextField itemID = new JTextField(String.format("Enter item ID for Item #%d:", currentItemNumber));
    public static JTextField itemQuantity = new JTextField(String.format("Enter quantity for Item #%d:", currentItemNumber));
    public static JTextField itemDetails = new JTextField(String.format("Details for Item #%d:", currentItemNumber));
    public static JTextField orderSubtotal = new JTextField(String.format("Order subtotal for %d item(s):", currentItemNumber - 1));

    public static JTextField itemIDInput = new JTextField();
    public static JTextField itemQuantityInput = new JTextField();
    public static JTextField itemDetailsOutput = new JTextField();
    public static JTextField orderSubtotalOutput = new JTextField();

    public static JButton processItem = new JButton(String.format("Process Item #%d", currentItemNumber));
    public static JButton confirmItem = new JButton(String.format("Confirm Item #%d", currentItemNumber));
    public static JButton viewOrder = new JButton("View Order");
    public static JButton finishOrder = new JButton("Finish Order");
    public static JButton newOrder = new JButton("New Order");
    public static JButton closeStore = new JButton("Exit");

    // Adds the needed components of the GUI (labels, fields, and buttons).
    public static void addGUIComponents(Container pane) {

        // Create the JPanel that contains the field labels that are next to the GUI input fields.
        JPanel storePanel = new JPanel();
        storePanel.setLayout(new BoxLayout(storePanel, BoxLayout.PAGE_AXIS));

        itemID.setEditable(false);
        itemQuantity.setEditable(false);
        itemDetails.setEditable(false);
        orderSubtotal.setEditable(false);

        storePanel.add(itemID);
        storePanel.add(itemQuantity);
        storePanel.add(itemDetails);
        storePanel.add(orderSubtotal);

        // Create the JPanel that containts the two item input fields and the two data display fields for the GUI.
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.PAGE_AXIS));

        itemIDInput.setEditable(true);
        itemQuantityInput.setEditable(true);
        itemDetailsOutput.setEditable(false);
        orderSubtotalOutput.setEditable(false);

        itemPanel.add(itemIDInput);
        itemPanel.add(itemQuantityInput);
        itemPanel.add(itemDetailsOutput);
        itemPanel.add(orderSubtotalOutput);

        // Create the JPanel that contains the six buttons at the bottom of the GUI.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        buttonPanel.add(processItem);
        buttonPanel.add(confirmItem);
        buttonPanel.add(viewOrder);
        buttonPanel.add(finishOrder);
        buttonPanel.add(newOrder);
        buttonPanel.add(closeStore);

        // Limit user options in the GUI by disabling fields that should not be
        // currently used.
        confirmItem.setEnabled(false);
        viewOrder.setEnabled(false);
        finishOrder.setEnabled(false);

        // Add all of the JPanels to the GUI's JFrame content pane.
        pane.add(storePanel, BorderLayout.LINE_START);
        pane.add(itemPanel, BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.PAGE_END);

    }

    // Creates the program's GUI.
    public static void createGUI() {

        // Create the JFrame that houses the GUI, call the addGUIComponents() method to 
        // add all of the needed labels, fields, and buttons that users will need to use
        // the program.
        JFrame frame = new JFrame("Nile Dot Com Store");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        addGUIComponents(frame.getContentPane());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Searches the inventory.txt file provided for an item with the user provided itemIDNumber.
    // If the item is found, the itemFound variable is set to contain the data from the inventory.
    // If the item is found but is out of stock, the itemFound variable is set to show that.
    // If the item is not found, the itemFound variable is set to show that.
    public static void searchInventory(String itemIDNumber) throws FileNotFoundException, IOException{
        
        File inpFile = new File("res/inventory.txt");
        Scanner sc = new Scanner(inpFile);
        String scan = ""; // This variable holds the current line from the inventory.txt file.

        while (sc.hasNextLine()) {

            scan = sc.nextLine();
            if (scan.matches(itemIDNumber + "\\b(.*)")) {
                if (scan.contains("false")) {
                    scan = "Out of Stock";
                }
                break;
            }
            else if (sc.hasNextLine() == false) {
                scan = "Item Not Found";
            }
        }
        
        // itemFound is used by the processItem ActionListener to determine if the desired item was found,
        // not found, or not in stock and act accordingly.
        itemFound = scan;
        
        sc.close();
    }

    // Takes the total variable, which is the subtotal for the currently selected item,
    // and adds it to the current order subtotal that is shown to the user in the 
    // orderSubtotalOutput JTextField of the GUI.
    public static void addToSubtotal(double total) {
        
        // Add the latest confirmed item's price to the order subtotal
        // Has to convert the JTextField value to string, check if empty,
        // otherwise remove the dollar sign from the JTextField subtotal,
        // then convert to a double to add to the subtotal.
        String tempSubtotalString = orderSubtotalOutput.getText();
        if (tempSubtotalString.isEmpty()) {
            tempSubtotalString = "0";
        }
        else {
            tempSubtotalString = tempSubtotalString.substring(1);
        }
        subtotal = Double.parseDouble(tempSubtotalString);
        subtotal += total;
        orderSubtotalOutput.setText(String.format("$%.2f", subtotal));

    }

    // Takes the current shopping cart stored in itemString and writes the 
    // corresponding log to the transactions.txt file.
    public static void writeToTransactionLog(String[] itemString) throws IOException{

        File transactionLog = new File("transactions.txt");

        FileWriter transactionWriter = new FileWriter(transactionLog, true);

        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("DDMMYYYYHHMM");
        ZonedDateTime now = ZonedDateTime.now();
        String transactionDT = String.format(dtFormat.format(now));
        
        for (int i = 0; i < itemString.length; i++) {
            transactionWriter.write(String.format("%s, %s\n", transactionDT, itemString[i]));
        }

        transactionWriter.close();
    }

    // Override the default actionPerformed() method to allow each of the six buttons to have
    // custom behavior when clicked.
    @Override
    public void actionPerformed(ActionEvent event) {

    }

    // Contains the ActionListeners and corresponding actions done when each button is clicked.
    public static void processButtonClick() {

        // Processes the item selected by the user and the quantity desired when the user clickes 
        // the Process Item button. Gathers the data for the selected item and reformats it for the 
        // user to view and determines the total for the selected item in the quantity requested.
        processItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                if (currentItemNumber > 1) {
                    itemDetails.setText(String.format("Details for Item #%d:", currentItemNumber));
                }

                // Gets the selected item's ID number as a string.
                String tempIDString = itemIDInput.getText();

                // Attempt to search the inventory.txt file for the item selected by the user.
                try {
                    searchInventory(tempIDString);
                } catch (FileNotFoundException fnfe) {
                    System.out.println("Error, inventory.txt file not found. Please put the file in the res subfolder of the NileDotCom folder.");
                } catch (IOException ioe) {
                    System.out.println("Error, IOException encountered.");
                }

                double discount = 0.0;

                // After searching if the item is in the inventory file, if the 
                // item doesn't exist, inform the user and reset the GUI to 
                // allow the user to select another item. If the item was found
                // but is out of stock, inform the user and reset the GUI to 
                // allow the user to select another item. If the item was found 
                // and is in stock, reformat the gathered data so it can be 
                // displayed to the user properly.
                if (itemFound.equals("Item Not Found")) {
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, String.format("item ID %s not in file", tempIDString), "Nile Dot Com - ERROR", 0);
                    itemIDInput.setText("");
                    itemQuantityInput.setText("");
                }
                else if (itemFound.equals("Out of Stock")) {
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, "Sorry... that item is out of stock, please try another item", "Nile Dot Com - ERROR", 0);
                    itemIDInput.setText("");
                    itemQuantityInput.setText("");
                }
                else {
                    // Divide the gathered data by the commas so the 
                    // information can be rearranged.
                    String[] parsedItem = new String[] {};
                    parsedItem = itemFound.split(", ");
                    
                    // Get the quantity of the selected item and find the total cost.
                    String tempQuantity = itemQuantityInput.getText();
                    Double quantityDouble = Double.parseDouble(tempQuantity);
                    itemTotal = Double.parseDouble(parsedItem[3]) * quantityDouble;
                    
                    // Determine the discount amount for the selected item.
                    if(quantityDouble >= 1 && quantityDouble <= 4) {
                        discount = 0.0;
                    }
                    else if(quantityDouble >= 5 && quantityDouble <= 9) {
                        discount = 0.10;
                    }
                    else if(quantityDouble >= 10 && quantityDouble <= 14) {
                        discount = 0.15;
                    }
                    else {
                        discount = 0.20;
                    }
                    
                    // Apply the discount (no change to the cost if there is not a discount).
                    // Make sure that all cost totals round down to two decimal places.
                    itemTotal -= (itemTotal * discount);
                    itemTotal = Math.floor(itemTotal * 100) / 100;
                    
                    // Format the selected item's data for the user to view.
                    currentItem = String.format("%s %s $%s %s %.0f%% $%.2f", parsedItem[0], 
                        parsedItem[1], parsedItem[3], tempQuantity, discount * 100, itemTotal);
                                    
                    itemDetailsOutput.setText(currentItem);

                    // Limit user options in the GUI by disabling fields that should not be
                    // currently used.
                    itemIDInput.setEditable(false);
                    itemQuantityInput.setEditable(false);
                    processItem.setEnabled(false);
                    confirmItem.setEnabled(true);
                }
            }
        });

        // Confirms that the user wishes to buy the selected item and adds it to the cart when
        // the user clicks the Confirm Item button. Gathers the subtotal of the items in the cart
        // for the user to view and updates the GUI with the correct number of items.
        confirmItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                // Add the cost of the confirmed item to the order subtotal
                addToSubtotal(itemTotal);

                // Add the current item to the cart.
                currentOrder[currentItemNumber - 1] = currentItem;

                // Create new JFrame for the popup window.
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, String.format("Item #%d accepted. Added to your cart", currentItemNumber), "Nile Dot Com - Item Confirmed", 1);
                
                currentItemNumber++;

                // Update the GUI labels, fields, and buttons with the current item number for the order.
                processItem.setText(String.format("Process Item #%d", currentItemNumber));
                confirmItem.setText(String.format("Confirm Item #%d", currentItemNumber));
                itemID.setText(String.format("Enter item ID for Item #%d:", currentItemNumber));
                itemQuantity.setText(String.format("Enter quantity for Item #%d:", currentItemNumber));
                orderSubtotal.setText(String.format("Order subtotal for %d item(s):", currentItemNumber - 1));
                
                itemIDInput.setText("");
                itemQuantityInput.setText("");

                // Limit user options in the GUI by disabling fields that should not be
                // currently used.
                processItem.setEnabled(true);
                confirmItem.setEnabled(false);
                viewOrder.setEnabled(true);
                finishOrder.setEnabled(true);
                itemIDInput.setEditable(true);
                itemQuantityInput.setEditable(true);

                if(currentItemNumber > 10) {
                    processItem.setEnabled(false);
                    itemIDInput.setEditable(false);
                    itemQuantityInput.setEditable(false);
                    itemID.setText(String.format("Enter item ID for Item #%d:", currentItemNumber - 1));
                    itemQuantity.setText(String.format("Enter quantity for Item #%d:", currentItemNumber - 1));
                }
            }
        });

        // Allows the user to view the current status of their cart when the user clicks the
        // View Order button. Displays to the user all of the items in the cart, the quantity 
        // of each item, and the cost of each of the items in the desired quantity.
        viewOrder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                // Create new JFrame for the popup window.
                JFrame f = new JFrame();                
                
                // Create a temporary list that stores the current items in the order
                // so that the null values can be removed for display purposes when there
                // is less than 10 items in the order.
                List<String> tempList = new ArrayList<String>();

                for (String aString : currentOrder) {
                    if (aString != null) {
                        tempList.add(aString);
                    }
                }
                   
                // Change the temporary list back into an array so that it can be used to 
                // create a new array that also contains the item numbers for each item in 
                // the order (ex: 1. 14 "Stanley #2 Philips Screwdriver" $6.95 1 0% $6.95)
                // for the View Order popup window.
                String[] cleanedOrder = tempList.toArray(new String[tempList.size()]);
                String[] numberedOrder = new String[cleanedOrder.length];
                
                for(int i = 1; i <= cleanedOrder.length; i++) {
                    numberedOrder[i - 1] = String.format("%d. %s", i, cleanedOrder[i - 1]);
                }

                // Turn the new array with the numbers for each item into a JList so that it 
                // can be added to the popup window.
                JList<String> numberedOrderList = new JList<>(numberedOrder);

                JOptionPane.showMessageDialog(f, numberedOrderList, "Nile Dot Com - Current Shopping Cart Status", 1);
            }
        });

        // Completes the user's order when the user clicks the Finish Order button. Displays a final 
        // invoice for the user that contains the current date and time, the number of items purchased,
        // the individual items purchased, the subtotal of the order, the tax rate applied, the total 
        // cost in taxes, the final total of the order, and then thanks the user for shopping at the store.
        finishOrder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                // Create new JFrame for the popup window.
                JFrame f = new JFrame();

                // Date and Time
                // Make two Strings of it, base form for the transaction log,
                // second form with additional data for the invoice popup.
                DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM/DD/YY, hh:MM:SS a zzz");
                ZonedDateTime now = ZonedDateTime.now();
                String transactionLogDT = String.format(dtFormat.format(now));
                String orderDateTime = String.format("Date: " + transactionLogDT + " \n\n");

                // Number of items, display organization
                String numItems = String.format("Number of items: %d \n\n", currentItemNumber - 1);
                String itemOrg = String.format("Item# / ID / Title / Price / Qty / Disc%% / Subtotal \n\n");
                
                // Numbered item list, which is created in the same way 
                // as in the View Order ActionListener above.
                List<String> tempList = new ArrayList<String>();
                for (String aString : currentOrder) {
                    if (aString != null) {
                        tempList.add(aString);
                    }
                }

                String[] cleanedFinalOrder = tempList.toArray(new String[tempList.size()]);
                String[] numberedFinalOrder = new String[cleanedFinalOrder.length];

                for (int i = 1; i <= cleanedFinalOrder.length; i++) {
                    numberedFinalOrder[i - 1] = String.format("%d. %s", i, cleanedFinalOrder[i - 1]);
                }

                // Order Subtotal, tax rate, tax amount, and order total.
                String finalSubtotal = String.format("\n\nOrder subtotal: $%.2f \n\n", subtotal);
                String taxRate = String.format("Tax rate: 6%% \n\n");
                String taxAmount = String.format("Tax amount: %.2f \n\n", (subtotal * 0.06));
                String orderFinalTotal = String.format("Order total: $%.2f \n\n", (subtotal + (subtotal * 0.06)));

                // Thank the customer for shopping.
                String thanks = String.format("Thanks for shopping at Nile Dot Com");

                // Place all of the created Strings into arrays of Strings to add to the final invoice popup.
                // The two parts of the invoice shown below are separated by the numbered item list in the final 
                // invoice.
                String[] invoicePart1 = new String[] {
                    orderDateTime,
                    numItems,
                    itemOrg
                };
                String[] invoicePart2 = new String[] {
                    finalSubtotal,
                    taxRate,
                    taxAmount,
                    orderFinalTotal,
                    thanks
                };
                
                // Display the final invoice popup.
                JOptionPane.showMessageDialog(f, new Object[]{invoicePart1, numberedFinalOrder, invoicePart2}, "Nile Dot Com - Final Invoice", 1);
            
                // Create String array for the transactions log file using the 
                // final state of the order without the null values and the 
                // datetime information that was already gathered.
                String[] orderForLog = new String[cleanedFinalOrder.length];
                for (int j = 0; j < cleanedFinalOrder.length; j++) {
                    orderForLog[j] = String.format("%s, %s", cleanedFinalOrder[j], transactionLogDT);
                }

                // Attempt to call the writeToTransactionLog() method that writes a log of the 
                // order that was just completed.
                try {
                    writeToTransactionLog(orderForLog);
                } catch (IOException ioex) {
                    System.out.println("Error, IOException occurred creating transaction log file");
                }

                // Limit user options in the GUI by disabling fields that should not be currently used.
                processItem.setEnabled(false);
                confirmItem.setEnabled(false);
                finishOrder.setEnabled(false);
                itemIDInput.setEditable(false);
                itemQuantityInput.setEditable(false);
            }
        });

        // Resets the variables that contain the current cart and the current item number
        // and reset the GUI labels, fields, and buttons as if the user just started the 
        // program after clicking the New Order button.
        newOrder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                currentOrder = new String[currentOrder.length];

                currentItemNumber = 1;

                processItem.setText(String.format("Process Item #%d", currentItemNumber));
                confirmItem.setText(String.format("Confirm Item #%d", currentItemNumber));
                itemID.setText(String.format("Enter item ID for Item #%d:", currentItemNumber));
                itemQuantity.setText(String.format("Enter quantity for Item #%d:", currentItemNumber));
                itemDetails.setText(String.format("Details for Item #%d:", currentItemNumber));
                orderSubtotal.setText(String.format("Order subtotal for %d item(s):", currentItemNumber - 1));
            
                itemIDInput.setText("");
                itemQuantityInput.setText("");
                itemDetailsOutput.setText("");
                orderSubtotalOutput.setText("");

                // Limit user options in the GUI by disabling fields that should not be
                // currently used, and make sure that options that should be enabled when 
                // the program starts are enabled.
                processItem.setEnabled(true);
                confirmItem.setEnabled(false);
                viewOrder.setEnabled(false);
                finishOrder.setEnabled(false);
                itemIDInput.setEditable(true);
                itemQuantityInput.setEditable(true);
            }
        });

        // Simply exits the program when the Exit button is clicked.
        closeStore.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                System.exit(0);
            }
        });
    }

    public static void main(String args[]) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
                processButtonClick();
            }
        });
    }
}
