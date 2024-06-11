import java.io.*;
import java.util.*;
import javax.swing.*;

public class WestminsterShoppingManager implements ShoppingManager{
    private List<Product> productList;
    private int totalAvailableItems;
    private static WestminsterShoppingManager shoppingManager;

    public WestminsterShoppingManager() {

        this.productList = new ArrayList<>();
    }

    // Implementation of methods from the ShoppingManager interface
    /**
     * Add a new product to the list
     * @param product the product to be added */
    @Override
    public void addProduct(Product product) {
        //Check if the limit
        if (getTotalAvailableItems() + product.getAvailableItems() <= 50) {
            productList.add(product);
            System.out.println("Product added successfully.");
        } else {
            System.out.println("Cannot add more products. Maximum limit reached.");
        }
    }

    /**
     * Delete a product from the list
     * @param productId the product ID to be deleted */
    @Override
    public void deleteProduct(String productId) {
        boolean productFound = false;

        //Check the product id
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getProductId().equals(productId)) {
                int removedAvailableItems = product.getAvailableItems();
                iterator.remove();
                System.out.println("Product deleted successfully.");

                // Adjust total available items after deletion
                adjustTotalAvailableItems(-removedAvailableItems);
                productFound = true;
                break;
            }
        }
        if (!productFound) {
            System.out.println("Product ID is not found.");
        }
    }

    //Display the product list
    @Override
    public void printProductList() {
        if (productList.isEmpty()){
            System.out.println("Product list is empty.");
        }
        else {
            productList.sort(Comparator.comparing(Product::getProductId));
            for (Product product : productList) {
                System.out.println("Type: " + getProductType(product) + "\n" + product.getProductInfo());
            }
        }
    }

    // Method to get the total available items in the productList
    private int getTotalAvailableItems() {
        totalAvailableItems = productList.stream().mapToInt(Product::getAvailableItems).sum();
        return totalAvailableItems;
    }

    // Method to adjust the total available items
    private void adjustTotalAvailableItems(int adjustment) {

        totalAvailableItems += adjustment;
    }

    //Check the product type
    public String getProductType(Product product) {
        if (product instanceof Electronics) {
            return "Electronics";
        } else if (product instanceof Clothing) {
            return "Clothing";
        } else {
            return "Unknown";
        }
    }

    /**
     * Save the product details in the product list
     * @param filename the details to be saved in
     */
    public void saveToFile(String filename) {
        try{
            FileWriter fileWriter = new FileWriter("product_details.txt");

            for(Product product:productList){
                fileWriter.write(product.getProductInfo()+"\n");
            }

            fileWriter.close();
            System.out.println("Product details saved to the file successfully.");
        }
        catch(IOException e) {
            System.out.println("An error occurred while storing data");
            e.printStackTrace();
        }
    }

    /**
     * Get the details from the saved file
     * @param filename the filename of the saved file
     */
    public void loadFromFile(String filename) {
        try {
            File file = new File("product_details.txt");
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();

                // Split the line into parts based on a separator (assuming a specific format)
                String[] parts = line.split(" - ");

                if (parts.length >= 6) {
                    String type = parts[0];
                    String productId = parts[1];
                    String productName = parts[2];
                    double price = Double.parseDouble(parts[3]);

                    switch (type) {
                        case "Electronics":
                            String brand = parts[4];
                            int warrantyPeriod = Integer.parseInt(parts[5]);
                            productList.add(new Electronics(productId, productName, 0, price, brand, warrantyPeriod));
                            break;

                        case "Clothing":
                            String size = parts[4];
                            String color = parts[5];
                            productList.add(new Clothing(productId, productName, 0, price, size, color));
                            break;

                        // Add more cases for other product types if needed
                        default:
                            System.out.println("Unknown product type: " + type);
                    }
                } else {
                    System.out.println(line);
                }
            }

            fileReader.close();
            System.out.println("Program data loaded successfully.");

        }
        catch (FileNotFoundException e) {
            System.out.println("Program data file not found. No data loaded.");
        }

    }

    //Filter the Electronic products
    public List<Product> filterElectronics() {
        List<Product> electronicsList = new ArrayList<>();
        for (Product product : productList) {
            if (product instanceof Electronics) {
                electronicsList.add(product);
            }
        }
        return electronicsList;
    }

    //Filter the Clothing products
    public List<Product> filterClothing() {
        List<Product> clothingList = new ArrayList<>();
        for (Product product : productList) {
            if (product instanceof Clothing) {
                clothingList.add(product);
            }
        }
        return clothingList;
    }

    //Get the product list
    public List<Product> getProductList() {
        return productList;
    }

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            WestminsterShoppingCenter gui = new WestminsterShoppingCenter(shoppingManager);
            gui.setVisible(true);
        });
    }

    // Validate if the input yes or no correctly
    private static boolean isValidYesNo(String input) {
        return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no");
    }

    // Validate if the input is a positive integer
    private static boolean isValidPositiveInteger(String str) {
        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    public static void main(String[] args){

        shoppingManager = new WestminsterShoppingManager();
        Scanner scanner = new Scanner(System.in);

        //Display the menu
        while (true) {
            System.out.println("\n      -------- Menu ---------");
            System.out.println("1 - Add new product");
            System.out.println("2 - Delete a product");
            System.out.println("3 - Print the list of products");
            System.out.println("4 - Save product list to file");
            System.out.println("5 - Load product list from file");
            System.out.println("6 - View the GUI");
            System.out.println("7 - Exit");

            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();

            System.out.println(" ");

            switch (choice) {
                case 1:
                    // Add a new product with user input
                    System.out.print("Enter product ID: ");
                    String productId = scanner.next();

                    System.out.print("Enter product name: ");
                    String productName = scanner.next();

                    System.out.print("Enter number of available items: ");
                    String availableItemsInput = scanner.next();

                    if (!isValidPositiveInteger(availableItemsInput)) {
                        System.out.println("Invalid input. Please enter a positive integer for available items.");
                        continue;
                    }

                    int availableItems = Integer.parseInt(availableItemsInput);

                    System.out.print("Enter price: ");
                    double price = 0.0;
                    // Validate input for a non-negative price
                    boolean validPrice = false;
                    while (!validPrice) {
                        try {
                            price = scanner.nextDouble();
                            if (price >= 0) {
                                validPrice = true;
                            } else {
                                System.out.println("Price cannot be negative. Please enter a non-negative value.");
                                System.out.print("Enter price: ");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid numeric value.");
                            scanner.next(); // Consume the invalid input to avoid an infinite loop
                            System.out.print("Enter price: ");
                        }
                    }
                    System.out.print("Is it an electronics product? (yes/no): ");
                    String electronicsInput = scanner.next();

                    if (!isValidYesNo(electronicsInput)) {
                        System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        continue;
                    }

                    boolean isElectronics = electronicsInput.equalsIgnoreCase("yes");

                    //Get electronic product details
                    if (isElectronics) {
                        System.out.print("Enter brand: ");
                        String brand = scanner.next();

                        System.out.print("Enter warranty period (in months): ");
                        int warrantyPeriod = scanner.nextInt();

                        // Create an Electronics object and add it to the system
                        Electronics newElectronics = new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
                        shoppingManager.addProduct(newElectronics);
                    }
                    //Get clothing product details
                    else {
                        System.out.print("Enter size: ");
                        String size = scanner.next();

                        System.out.print("Enter color: ");
                        String color = scanner.next();

                        // Create a Clothing object and add it to the system
                        Clothing newClothing = new Clothing(productId, productName, availableItems, price, size, color);
                        shoppingManager.addProduct(newClothing);
                    }
                    break;

                case 2:
                    // Delete a product
                    System.out.print("Enter product ID to delete: ");
                    String productIdToDelete = scanner.next();
                    shoppingManager.deleteProduct(productIdToDelete);
                    break;

                case 3:
                    // Print the list of products
                    shoppingManager.printProductList();
                    break;

                case 4:
                    // Save product list to file
                    shoppingManager.saveToFile("product_details.txt");
                    break;

                case 5:
                    // Load product list from file
                    shoppingManager.loadFromFile("product_details.txt");
                    break;

                case 6:
                    System.out.print("Username: ");
                    String username = scanner.next();

                    System.out.print("Password: ");
                    String password = scanner.next();

                    //Show the GUI
                    shoppingManager.createAndShowGUI();
                    break;

                case 7:
                    // Exit the program
                    System.exit(7);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
