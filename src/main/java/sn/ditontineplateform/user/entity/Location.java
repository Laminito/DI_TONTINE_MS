package sn.ditontineplateform.user.entity;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Location {

    private String country;
    private String region;
    private String city;
    private String postalCode;
    private String addressLine;
}
