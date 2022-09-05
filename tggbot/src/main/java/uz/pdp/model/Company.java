package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private UUID id=UUID.randomUUID();
    private String nameUz;
    private String photoUrl;
    private String descriptionUz;
    private double lan;
    private double lat;
    private String phoneNum;

    public Company(String nameUz,  String photoUrl, String descriptionUz,double lan,double lat,String phoneNum) {
        this.nameUz = nameUz;
        this.photoUrl = photoUrl;
        this.descriptionUz = descriptionUz;
        this.lan = lan;
        this.lat = lat;
        this.phoneNum = phoneNum;
    }
}
