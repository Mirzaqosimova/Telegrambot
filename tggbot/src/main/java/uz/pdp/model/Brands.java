package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brands {
 private    UUID id = UUID.randomUUID();
    private  String brand;
    private List<Product> product;
    public  Brands(String brand,List<Product> product){
        this.brand = brand;
        this.product = product;
    }
}
