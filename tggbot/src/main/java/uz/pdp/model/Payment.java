package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.model.enums.PayType;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private UUID id=UUID.randomUUID();
    private double paySum;
    private PayType payType;

    public Payment(double paySum, PayType payType) {
        this.paySum = paySum;
        this.payType = payType;
    }
}
