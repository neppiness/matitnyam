package com.wanted.matitnyam.domain;

import com.wanted.matitnyam.domain.xmlparser.Restaurant;
import com.wanted.matitnyam.domain.xmlparser.MetaInformation;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Genrestrtchifood")
@Getter
@NoArgsConstructor
public class ChineseRestaurants implements RestaurantsData {

    @XmlElement(name = "head")
    private MetaInformation metaInformation;

    @XmlElement(name = "row")
    private List<Restaurant> restaurants;

    public RestaurantsData preprocess() {
        this.restaurants = this.restaurants.stream()
                .filter(RestaurantsDataPreprocessor::hasEssentialInformation)
                .toList();
        return this;
    }

}
