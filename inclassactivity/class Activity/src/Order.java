import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Order {
    private String id;
    private static List<Book> books ;

    public Order (String id){
        this.id = id;
        this.books = books;
    }

    public static double computeCost() {
        return Optional.ofNullable(books.stream().mapToDouble(Book::computeCost).sum()).orElse(null);
    }
}
