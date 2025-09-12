import java.util.List;

public class RegularBook extends Book {
    private double shippingCost;

    public RegularBook(String title, String author, double price, double shippingCost) {
        super(title, author, price);
        this.shippingCost = shippingCost;
    }

    @Override
    public double computeCost() {
        return price + shippingCost;
    }
}
