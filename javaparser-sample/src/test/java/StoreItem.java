import java.util.ArrayList;
import java.util.List;

public class StoreItem {

    private String itemName;
    private double price;
    private int quantity;


    public StoreItem(String itemName, double price, int quantity) {
        this.setItemName(itemName);
        this.setPrice(price);
        this.setQuantity(quantity);
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public static void printInvoiceHeader() {
        System.out.println(String.format("%30s %25s %10s %25s %10s", "Item", "|", "Price($)", "|", "Qty"));
        System.out.println(String.format("%s", "----------------------------------------------------------------------------------------------------------------"));
    }
    public void printInvoice() {
        System.out.println(String.format("%30s %25s %10.2f %25s %10s", this.getItemName(), "|", this.getPrice(), "|", this.getQuantity()));
    }

    public static List<StoreItem> buildInvoice() {
        List<StoreItem> itemList = new ArrayList<>();
        itemList.add(new StoreItem("Nestle Decaff Coffee", 759.99, 2));
        itemList.add(new StoreItem("Brown's Soft Tissue Paper", 15.80, 2));
        itemList.add(new StoreItem("LG 500Mb External Drive", 700.00, 2));
        return itemList;
    }

    public static void main (String[] args) {

        StoreItem.printInvoiceHeader();
        StoreItem.buildInvoice().forEach(StoreItem::printInvoice);
     }
}