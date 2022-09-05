package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private UUID id=UUID.randomUUID();
    private String model;
    private double price;
    private String color;
    private double ram;
    private double storage;
    private String operativeSystem;
    private double camera;
    private String size;
    private String link;

    public Product(String model, double price, String color, double ram, double storage, String operativeSystem, double camera, String size,String link) {
        this.model = model;
        this.price = price;
        this.color = color;
        this.ram = ram;
        this.storage = storage;
        this.operativeSystem = operativeSystem;
        this.camera = camera;
        this.size = size;
        this.link = link;
    }
}
