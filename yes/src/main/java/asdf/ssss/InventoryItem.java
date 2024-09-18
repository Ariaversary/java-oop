package asdf.ssss;

public class InventoryItem {
    private String code;
    private String description;
    private String supplier;
    private int quantity;

    public InventoryItem(String code, String description, String supplier, int quantity) {
        this.code = code;
        this.description = description;
        this.supplier = supplier;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }
}