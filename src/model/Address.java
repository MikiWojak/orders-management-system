package model;

public record Address(
        String street,
        String houseNumber,
        String apartmentNumber,
        String city,
        String zipCode,
        String district,
        String country
) {
    public String getFileRecord() {
        return
                this.street + ";" +
                this.houseNumber  + ";" +
                this.apartmentNumber  + ";" +
                this.city  + ";" +
                this.zipCode  + ";" +
                this.district + ";" +
                this.country;
    }

    @Override
    public String toString() {
        return
                street + " " +
                houseNumber +
                (apartmentNumber.isEmpty() ? "" : "/" + apartmentNumber) +
                ", " +
                zipCode + " " +
                city + ", " +
                district + ", " +
                country;
    }

    public boolean isValid() {
        return !(
                street.isEmpty() ||
                houseNumber.isEmpty() ||
                city.isEmpty() ||
                zipCode.isEmpty() ||
                district.isEmpty() ||
                country.isEmpty()
        );
    }
}
