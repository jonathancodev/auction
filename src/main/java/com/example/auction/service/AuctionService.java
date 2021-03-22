package com.example.auction.service;

import com.example.auction.dto.BidDTO;
import com.example.auction.dto.WinnerDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class AuctionService {

    public String validation(List<BidDTO> bids){

        String msg = null;

        for(BidDTO bidDTO: bids){

            if(bidDTO.getName() == null || bidDTO.getName().trim().equals("")){
                msg = "Name is required";
                break;
            } else if(bidDTO.getValue() == null || bidDTO.getValue().equals(0.0)){
                msg = "Value is required";
                break;
            } else if(bidDTO.getValue() <= 0){
                msg = "Value must be positive";
                break;
            } else {
                BigDecimal bigDecimal = new BigDecimal(String.valueOf(bidDTO.getValue()));

                if(bigDecimal.scale() > 2) {
                    msg = "Value cannot have more than two decimal places";
                    break;
                }
            }
        }

        return msg;
    }

    public WinnerDTO findWinner(List<BidDTO> bids){
        String msg;
        WinnerDTO winnerDTO = new WinnerDTO();
        BidDTO winnerBid = new BidDTO();
        winnerBid.setName("No winner");
        winnerBid.setValue(Double.MAX_VALUE);
        double total = bids.size()*0.98;
        winnerDTO.setTotal(total);

        LinkedHashSet<BidDTO> noDuplicate = new LinkedHashSet<>(bids);

        if(noDuplicate.size() == 1 && bids.size() > 1){
            msg = "No winner. All bids has the same value. Total: "+total;
            winnerDTO.setMsg(msg);
        } else {
            int count = 0;
            for(BidDTO bid: noDuplicate) {

                if(count >= 999){
                    break;
                }

                if(bid.getValue()<winnerBid.getValue() && Collections.frequency(bids, bid) == 1){
                    winnerBid = bid;
                }

                count++;
            }

            msg = "Winner: "+winnerBid.toString()+" and total: "+total;
            winnerDTO.setMsg(msg);
        }

        winnerDTO.setWinnerBid(winnerBid);

        return winnerDTO;

    }
}
