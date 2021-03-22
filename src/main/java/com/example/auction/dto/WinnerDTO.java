package com.example.auction.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class WinnerDTO {

    private BidDTO winnerBid;
    private String msg;
    private Double total;
}
