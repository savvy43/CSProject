package model;

import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private List<InvoiceItem> items = new ArrayList<>();

    public void addItem(InvoiceItem item) {
        items.add(item);
    }

    public double getTotalAmount() {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getAmount();
        }
        return total;
    }
}