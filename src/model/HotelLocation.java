
package model;
public class HotelLocation {

    private String city;
    private String address;

    public HotelLocation(String city, String address) {
        this.city = city;
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return city + ", " + address;
    }
}