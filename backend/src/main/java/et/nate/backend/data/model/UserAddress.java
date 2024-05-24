package et.nate.backend.data.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
   private String address1;
   private  String address2;
   private  String zipCode;
   private  String city;
   private String country;
}
