package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithAmount {
    private UUID id=UUID.randomUUID();
    private Product product;
    private double productPrice;
    private int amount;
    private Order order;
    public ProductWithAmount(Product product, double productPrice, int amount, Order order) {
        this.product = product;
        this.productPrice = productPrice;
        this.amount = amount;
        this.order = order;
    }
}
