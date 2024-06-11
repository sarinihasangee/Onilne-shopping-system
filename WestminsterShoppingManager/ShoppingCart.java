import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Product> products;

    public ShoppingCart() {
        this.products = new ArrayList<>();
    }

    // Add a product to the cart
    public void addProduct(Product product) {
        this.products.add(product);
    }

    // Remove a product from the cart
    public void removeProduct(Product product) {
        this.products.remove(product);
    }

    // Calculate the total cost of products in the cart
    public double calculateTotalCost() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public List<Product> getProducts() {
        return new ArrayList<>(this.products);
    }
}
