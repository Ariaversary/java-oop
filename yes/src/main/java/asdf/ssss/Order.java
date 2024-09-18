package asdf.ssss;

import java.time.LocalDateTime;

public class Order {
    private String id;
    private String hospitalID;
    private LocalDateTime orderDate;
    private String itemCode;
    private String supplierID;
    private int quantity;
    private String type;

    public Order(String id, String hospitalID, LocalDateTime orderDate, String itemCode, String supplierID, int quantity, String type) {
        this.id = id;
        this.hospitalID = hospitalID;
        this.orderDate = orderDate;
        this.itemCode = itemCode;
        this.supplierID = supplierID;
        this.quantity = quantity;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }
}