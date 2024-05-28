import javax.swing.*; // Importing Swing components for GUI
import javax.swing.table.DefaultTableModel; // Importing DefaultTableModel for table data management
import java.awt.*; // Importing AWT components for GUI
import java.awt.event.ActionEvent; // Importing ActionEvent for event handling
import java.awt.event.ActionListener; // Importing ActionListener for event handling
import java.io.*; // Importing IO components for file operations
import java.util.ArrayList; // Importing ArrayList for data storage
import java.util.HashMap; // Importing HashMap for data storage
import java.util.Map; // Importing Map interface

public class SalesReport extends JFrame implements ActionListener {

    private JTextArea salesReportArea; // Text area to display sales report
    private JButton viewProductStatsButton, viewTransactionHistoryButton, clearReportButton, goHomeButton; // Buttons for actions
    private JLabel totalSalesLabel; // Label to display total sales
    private ArrayList<Transaction> transactions; // List to store transactions
    private static final String SALES_FILE_PATH = "sales_report.dat"; // File path for sales report data
    private static final Color BACKGROUND_COLOR = new Color(175, 238, 219); // Background color for GUI

    public SalesReport() {
        transactions = loadTransactions(); // Load transactions from file

        setTitle("Sales Report"); // Set window title
        setSize(900, 600); // Set window size
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        setLocationRelativeTo(null); // Center the window

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20)); // Main panel with border layout
        mainPanel.setBackground(BACKGROUND_COLOR); // Set background color
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Set border

        totalSalesLabel = new JLabel("Total Sales: ₱" + calculateTotalSales()); // Initialize total sales label
        totalSalesLabel.setFont(new Font("Arial", Font.PLAIN, 28)); // Set font size to 28
        mainPanel.add(totalSalesLabel, BorderLayout.NORTH); // Add total sales label to the top of the panel

        salesReportArea = new JTextArea(); // Initialize sales report area
        salesReportArea.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font
        salesReportArea.setBackground(Color.WHITE); // Set background color of the sales report area
        salesReportArea.setForeground(Color.GRAY); // Set foreground color of the sales report area
        salesReportArea.setEditable(false); // Make the sales report area non-editable
        JScrollPane scrollPane = new JScrollPane(salesReportArea); // Add scroll pane for the sales report area
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add sales report area with scroll pane to the center of the panel

        JPanel buttonPanel = new JPanel(); // Panel for buttons
        buttonPanel.setBackground(BACKGROUND_COLOR); // Set background color

        viewProductStatsButton = new JButton("View Product Statistics"); // Initialize view product statistics button
        viewProductStatsButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font
        viewProductStatsButton.setBackground(new Color(255, 251, 160)); // Set background color
        viewProductStatsButton.setForeground(Color.BLACK); // Set foreground color
        viewProductStatsButton.addActionListener(this); // Add action listener to the button
        buttonPanel.add(viewProductStatsButton); // Add button to the button panel

        viewTransactionHistoryButton = new JButton("View Transaction History"); // Initialize view transaction history button
        viewTransactionHistoryButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font
        viewTransactionHistoryButton.setBackground(new Color(252, 186, 112)); // Set background color
        viewTransactionHistoryButton.setForeground(Color.BLACK); // Set foreground color
        viewTransactionHistoryButton.addActionListener(this); // Add action listener to the button
        buttonPanel.add(viewTransactionHistoryButton); // Add button to the button panel

        clearReportButton = new JButton("Clear Report"); // Initialize clear report button
        clearReportButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font
        clearReportButton.setBackground(new Color(255, 99, 71)); // Set background color
        clearReportButton.setForeground(Color.BLACK); // Set foreground color
        clearReportButton.addActionListener(this); // Add action listener to the button
        buttonPanel.add(clearReportButton); // Add button to the button panel

        goHomeButton = new JButton("Go Home"); // Initialize go home button
        goHomeButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font
        goHomeButton.setBackground(new Color(70, 130, 180)); // Set background color
        goHomeButton.setForeground(Color.BLACK); // Set foreground color
        goHomeButton.addActionListener(this); // Add action listener to the button
        buttonPanel.add(goHomeButton); // Add button to the button panel

        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom of the main panel

        add(mainPanel); // Add main panel to the frame

        setVisible(true); // Make the frame visible
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewProductStatsButton) { // If view product statistics button is clicked
            viewProductStatistics(); // Call method to view product statistics
        } else if (e.getSource() == viewTransactionHistoryButton) { // If view transaction history button is clicked
            viewTransactionHistory(); // Call method to view transaction history
        } else if (e.getSource() == clearReportButton) { // If clear report button is clicked
            clearReport(); // Call method to clear the report
        } else if (e.getSource() == goHomeButton) { // If go home button is clicked
            dispose(); // Close the window
        }
    }

    private double calculateTotalSales() {
        return transactions.stream().mapToDouble(Transaction::getTotalPrice).sum(); // Calculate total sales from all transactions
    }

    private void viewProductStatistics() {
        HashMap<String, Integer> productQuantities = new HashMap<>(); // Map to store quantities of products sold
        HashMap<String, Double> productSales = new HashMap<>(); // Map to store total sales of products

        for (Transaction transaction : transactions) { // Iterate through transactions
            for (Map.Entry<Product, Integer> entry : transaction.getCart().entrySet()) { // Iterate through products in the transaction
                Product product = entry.getKey(); // Get the product
                int quantity = entry.getValue(); // Get the quantity sold
                productQuantities.put(product.getName(), productQuantities.getOrDefault(product.getName(), 0) + quantity); // Update quantity sold
                productSales.put(product.getName(), productSales.getOrDefault(product.getName(), 0.0) + (quantity * product.getPrice())); // Update total sales
            }
        }

        String[] columnNames = {"Product Name", "Quantity Sold", "Total Sales"}; // Column names for the table
        DefaultTableModel model = new DefaultTableModel(columnNames, 0); // Table model for product statistics

        for (String productName : productQuantities.keySet()) { // Iterate through product names
            int quantitySold = productQuantities.get(productName); // Get quantity sold
            double totalSales = productSales.get(productName); // Get total sales
            Object[] row = {productName, quantitySold, String.format("%.2f Php", totalSales)}; // Create a row for the table
            model.addRow(row); // Add row to the table model
        }

        JTable table = new JTable(model); // Create table with the model
        table.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font for the table
        table.setRowHeight(30); // Set row height
        JScrollPane scrollPane = new JScrollPane(table); // Create scroll pane for the table

        JDialog dialog = new JDialog(this, "Product Statistics", true); // Create dialog for displaying product statistics
        dialog.setSize(800, 400); // Set dialog size
        dialog.setLocationRelativeTo(this); // Center the dialog
        dialog.add(scrollPane); // Add scroll pane to the dialog
        dialog.setVisible(true); // Make the dialog visible
    }

    private void viewTransactionHistory() {
        StringBuilder history = new StringBuilder(); // StringBuilder to build transaction history
        for (Transaction transaction : transactions) { // Iterate through transactions
            history.append("Transaction:\n"); // Append transaction label
            for (Map.Entry<Product, Integer> entry : transaction.getCart().entrySet()) { // Iterate through products in the transaction
                Product product = entry.getKey(); // Get the product
                int quantity = entry.getValue(); // Get the quantity sold
                history.append(product.getName()).append(" - Quantity: ").append(quantity).append(" - Price: ").append(String.format("%.2f Php", product.getPrice())).append("\n"); // Append product details
            }
            history.append("Total Price: ").append(String.format("%.2f Php", transaction.getTotalPrice())).append("\n\n"); // Append total price
        }
        salesReportArea.setText(history.toString()); // Set the text area with transaction history
    }

    private void clearReport() {
        transactions.clear(); // Clear the list of transactions
        saveTransactions(); // Save the empty list to the file
        totalSalesLabel.setText("Total Sales: ₱0.00"); // Reset total sales label
        salesReportArea.setText(""); // Clear the sales report area
        JOptionPane.showMessageDialog(this, "Sales report cleared"); // Show confirmation message
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SALES_FILE_PATH))) { // Create output stream to save transactions
            oos.writeObject(transactions); // Write transactions to file
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace in case of exception
        }
    }

    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>(); // List to store transactions
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SALES_FILE_PATH))) { // Create input stream to read transactions from file
            transactions = (ArrayList<Transaction>) ois.readObject(); // Read transactions from file
        } catch (FileNotFoundException e) {
            System.out.println("Sales report file not found. Starting with an empty report."); // Print message if file not found
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Print stack trace if an exception occurs
        }
        return transactions; // Return the list of transactions
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesReport()); // Run the SalesReport GUI
    }
}
