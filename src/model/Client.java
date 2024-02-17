package model;

import java.util.UUID;

public record Client(
        UUID id,
        String firstName,
        String lastName,
        String companyName,
        String tin,
        Address address,
        Address deliveryAddress
) {
    public Client (
            String firstName,
            String lastName,
            String companyName,
            String tin,
            Address address,
            Address deliveryAddress
    ) {
        this(
                UUID.randomUUID(),
                firstName,
                lastName,
                companyName,
                tin,
                address,
                deliveryAddress
        );
    }

    public String getFileRecord() {
        return
                this.id + ";" +
                this.firstName  + ";" +
                this.lastName  + ";" +
                this.companyName  + ";" +
                this.tin  + ";" +
                this.address.getFileRecord() + ";" +
                this.deliveryAddress.getFileRecord();
    }

    @Override
    public String toString() {
        return
                firstName + " " +
                lastName +
                (companyName.isEmpty() ? "" : " " + companyName) +
                (tin.isEmpty() ? "" : " " + tin);
    }
}
