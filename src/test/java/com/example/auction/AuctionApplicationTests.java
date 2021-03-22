package com.example.auction;

import com.example.auction.dto.BidDTO;
import com.example.auction.dto.WinnerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuctionApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void nullValues() throws Exception{

        mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyValues() throws Exception{
        List<BidDTO> bids = new ArrayList<>();

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Bids is required");
    }

    @Test
    void emptyName() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Name is required");
    }

    @Test
    void emptyValue() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Value is required");
    }

    @Test
    void emptyValueZero() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bid.setValue(0.0);
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Value is required");
    }

    @Test
    void negativeValue() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bid.setValue(-1.0);
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Value must be positive");
    }

    @Test
    void moreThanTwoDecimalPlacesValue() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bid.setValue(1.001);
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Value cannot have more than two decimal places");
    }

    @Test
    void noWinner() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bid.setValue(1.01);
        bids.add(bid);
        BidDTO bid2 = new BidDTO();
        bid2.setName("Test 2");
        bid2.setValue(1.01);
        bids.add(bid2);
        double total = 2*0.98;

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        WinnerDTO winnerDTO = objectMapper.readValue(content, WinnerDTO.class);
        assertEquals(winnerDTO.getMsg(), "No winner. All bids has the same value. Total: "+total);
    }

    @Test
    void max999Bids() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        double total = 999*0.98;
        BidDTO bid = new BidDTO();
        bid.setName("Test");
        bid.setValue(0.1);
        bids.add(bid);

        for(int i=0; i<1000; i++){
            BidDTO bid2 = new BidDTO();
            bid2.setName("Test"+i);
            bid2.setValue(0.2);
            bids.add(bid2);
        }

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        WinnerDTO winnerDTO = objectMapper.readValue(content, WinnerDTO.class);
        assertEquals(winnerDTO.getMsg(), "Winner: Test with the bid 0.1 and total: "+total);
    }

    @Test
    void rightWinner() throws Exception{
        List<BidDTO> bids = new ArrayList<>();
        double total = 4*0.98;
        BidDTO bid = new BidDTO();
        bid.setName("João");
        bid.setValue(0.01);
        bids.add(bid);

        bid = new BidDTO();
        bid.setName("Maria");
        bid.setValue(0.3);
        bids.add(bid);

        bid = new BidDTO();
        bid.setName("Renata");
        bid.setValue(0.01);
        bids.add(bid);

        bid = new BidDTO();
        bid.setName("Pedro");
        bid.setValue(12.34);
        bids.add(bid);

        MvcResult mvcResult = mockMvc.perform(post("/api/auction/find-winner")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bids)))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        WinnerDTO winnerDTO = objectMapper.readValue(content, WinnerDTO.class);
        assertEquals(winnerDTO.getMsg(), "Winner: Maria with the bid 0.3 and total: "+total);
    }

}
