package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialNet {
    private UUID id=UUID.randomUUID();
    private String nameUz;
    private String link;

    public SocialNet(String nameUz,  String link) {
        this.nameUz = nameUz;
        this.link = link;
    }
}
