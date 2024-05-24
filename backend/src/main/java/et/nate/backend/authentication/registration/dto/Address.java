package et.nate.backend.authentication.registration.dto;

import et.nate.backend.data.model.UserAddress;

public record Address(
        String address1,
        String address2,
        String zipCode,
        String city,
        String country
) {

    public UserAddress toUserAddress() {
        return new UserAddress(this.address1, this.address2, this.zipCode, this.city, this.country);
    }
}
