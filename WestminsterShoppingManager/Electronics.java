public class Electronics extends Product{
    private String brand;
    private int warrantyPeriod;

    public Electronics(String productId, String productName, int availableItems, double price, String brand, int warrantyPeriod) {
        super(productId, productName, availableItems, price);
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }

    // Getters and setters for Electronics-specific attributes

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public String getProductInfo() {
        return String.format("Product ID: %s \nProduct Name: %s \nAvailable Items: %s \nProduct Price: %s \nBrand: %s \nWarranty: %d months", getProductId(), getProductName(), getAvailableItems(), getPrice(), brand, warrantyPeriod);
    }
}
