package com.example.auction.rest;

import com.example.auction.dto.BidDTO;
import com.example.auction.service.AuctionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/auction")
public class AuctionResource {

    private final AuctionService auctionService;

    @Autowired
    public AuctionResource(AuctionService auctionService){
        this.auctionService = auctionService;
    }

    @PostMapping(value = "/find-winner")
    @SneakyThrows
    public ResponseEntity<?> findWinner(@RequestBody @Valid @NotNull List<BidDTO> bids) {
        String msg = auctionService.validation(bids);

        if(msg != null){
            return ResponseEntity.badRequest().body(msg);
        } else {
            return ResponseEntity.ok(auctionService.findWinner(bids));
        }
    }
}
