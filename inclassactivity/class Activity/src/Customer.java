import java.util.*;

public class Customer {
    private String name ;
    private static List<Order> orders;

    public Customer(String name) {
        this.name = name;
        this.orders = orders;
    }
    public double computeCost(List<Order> orders) {
        double totalCost = 0.0;
        for (Order order: orders)
            totalCost += Order.computeCost();
        return totalCost;
    }
    private class CreditCard{
        private long number;
        private Date expiration;
    }
    }
