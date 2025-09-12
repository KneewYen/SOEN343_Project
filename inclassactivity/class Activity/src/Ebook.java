public class Ebook extends Book {
    private double discount ;
    private int sizeMB ;

    public Ebook( String title, String author,double price, double discount, int sizeMB) {
        super(title, author, price);
        this.discount = discount;
        this.sizeMB = sizeMB;
    }
    @Override
    public double computeCost(){
        return price - discount *  price;
    }

    
}
