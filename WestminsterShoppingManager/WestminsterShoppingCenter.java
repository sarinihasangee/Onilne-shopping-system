import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class WestminsterShoppingCenter extends JFrame {
    private JComboBox<String> productTypeComboBox;
    private JTable productTable;
    private JTextArea productDetailsTextArea;
    private JButton addToCartButton;
    private JButton shoppingCartButton;
    private ShoppingCart shoppingCart;
    private WestminsterShoppingManager shoppingManager;

    public WestminsterShoppingCenter(WestminsterShoppingManager shoppingManager) {
        this.shoppingManager = shoppingManager;
        setTitle("Westminster Shopping Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500,800);
        setLayout(new BorderLayout());

        productTypeComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothes"});
        productTable = new JTable();
        productDetailsTextArea = new JTextArea();
        addToCartButton = new JButton("Add to Shopping Cart");
        shoppingCartButton = new JButton("Shopping Cart");
        shoppingCart = new ShoppingCart();

        JPanel topPanel = new JPanel();
        topPanel.add(productTypeComboBox);
        topPanel.add(shoppingCartButton);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(productDetailsTextArea);
        bottomPanel.add(addToCartButton);
        add(bottomPanel, BorderLayout.SOUTH);

        updateTable("All", shoppingManager.getProductList());

        productTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable(productTypeComboBox.getSelectedItem().toString(), shoppingManager.getProductList());
            }
        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    Product selectedProduct = shoppingManager.getProductList().get(selectedRow);
                    shoppingCart.addProduct(selectedProduct);
                    updateProductDetailsTextArea(selectedProduct);
                }
            }
        });

        // Set a custom cell renderer to highlight rows with reduced availability in red
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Check if the column is "Available Items"
                if (column == 2) {
                    int availableItems = (int) value;
                    // If less than 3 items available, set the background color to red
                    if (availableItems < 3) {
                        cellComponent.setBackground(Color.RED);
                    } else {
                        cellComponent.setBackground(table.getBackground());
                    }
                } else {
                    cellComponent.setBackground(table.getBackground());
                }

                return cellComponent;
            }
        });

        shoppingCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openShoppingCartWindow();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void updateTable(String selectedType, List<Product> productList) {
        List<Product> filteredList;
        if (selectedType.equals("All")) {
            // Combine both electronics and clothing lists
            filteredList = new ArrayList<>(shoppingManager.filterElectronics());
            filteredList.addAll(shoppingManager.filterClothing());
        } else if (selectedType.equals("Electronics")) {
            filteredList = shoppingManager.filterElectronics();
        } else {
            filteredList = shoppingManager.filterClothing();
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ProductID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Available Items");
        tableModel.addColumn("Price");
        tableModel.addColumn("Info");

        for (Product product : filteredList) {
            tableModel.addRow(new Object[]{product.getProductId(), product.getProductName(), product.getAvailableItems(), product.getPrice(),getInfoText(product)});
        }

        productTable.setModel(tableModel);
    }

    // Get info text based on product type
    private String getInfoText(Product product) {
        if (product instanceof Electronics) {
            Electronics electronicProduct = (Electronics) product;
            return "Brand: " + electronicProduct.getBrand() + ", Warranty: " + electronicProduct.getWarrantyPeriod();
        } else if (product instanceof Clothing) {
            Clothing clothingProduct = (Clothing) product;
            return "Size: " + clothingProduct.getSize() + ", Color: " + clothingProduct.getColor();
        } else {
            return ""; // Default case or handle other product types
        }
    }

    private void updateProductDetailsTextArea(Product product) {
        productDetailsTextArea.setText(product.getProductInfo());
    }

    private void openShoppingCartWindow(){
        JFrame shoppingCartFrame = new JFrame("Shopping Cart");
        shoppingCartFrame.setLayout(new BorderLayout());

        JTable cartTable = new JTable();
        DefaultTableModel cartTableModel = new DefaultTableModel();

        cartTableModel.addColumn("Product Name");
        cartTableModel.addColumn("Quantity");
        cartTableModel.addColumn("Price");
        cartTable.setModel(cartTableModel);      //Set the model to the cart table

        List<Product> cartProducts = shoppingCart.getProducts();
        double total = 0.0;
        int categoryCount = 0;
        String currentCategory = null;

        for (Product product : cartProducts) {
            // Assume the quantity is always 1 for simplicity
            cartTableModel.addRow(new Object[]{product.getProductName(),1, product.getPrice()});
            total += product.getPrice(); // Accumulate total price

            // Check the category
            if (product instanceof Clothing || product instanceof Electronics) {
                if (currentCategory == null) {
                    currentCategory = shoppingManager.getProductType(product);
                    categoryCount = 1;
                } else if (currentCategory.equals(shoppingManager.getProductType(product))) {
                    categoryCount++;
                }
            }
        }

        double firstPurchaseDiscount = 0.1 * total; // 10% discount
        double categoryDiscount = (categoryCount >= 3) ? 0.2 * total : 0.0; // 20% discount for three items in the same category
        double finalTotal = total - firstPurchaseDiscount - categoryDiscount;

        JTextArea priceDetailsTextArea = new JTextArea();
        priceDetailsTextArea.setEditable(false);

        priceDetailsTextArea.append("\nTotal                   " + total);
        priceDetailsTextArea.append("\nFirst Purchase Discount(10%)          " + firstPurchaseDiscount);
        priceDetailsTextArea.append("\nThree items in same Category Discount(20%)          " + categoryDiscount);
        priceDetailsTextArea.append("\nFinal total              Rs." + finalTotal);

        shoppingCartFrame.add(cartTable,BorderLayout.CENTER);
        shoppingCartFrame.add(priceDetailsTextArea,BorderLayout.SOUTH);

        shoppingCartFrame.setSize(600, 400);
        shoppingCartFrame.setLocationRelativeTo(null);
        shoppingCartFrame.setVisible(true);

    }
}
