package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.model.enums.OrderStatus;

import java.sql.Timestamp;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private UUID id=UUID.randomUUID();
    private TgUser user;
    private OrderStatus orderStatus;
    private Timestamp orderDate;
    private Payment payment;
    private Float lan;
    private Float lat;
    private String address;

    public Order(TgUser user, OrderStatus orderStatus) {
        this.user = user;
        this.orderStatus = orderStatus;
    }
}
