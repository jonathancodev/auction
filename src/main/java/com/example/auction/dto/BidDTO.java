package com.example.auction.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"name"})
public class BidDTO {

    private String name;
    private Double value;

    @Override
    public String toString(){
        return this.name+" with the bid "+this.value;
    }
}
