/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bidding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import ws.wampee7.annotations.URIPrefix;
import ws.wampee7.annotations.onPublish;
import ws.wampee7.annotations.onRPC;
import ws.wampee7.annotations.onSubscribe;
import ws.wampee7.controllers.WAMPee7Contoller;
import static ws.wampee7.controllers.WAMPee7Contoller.cancel;

// Prefix is optional, but helps remove duplicate code.
@URIPrefix("http://example.com/auction#")
public class Bidder extends WAMPee7Contoller {
    public static class Bid{
            String bidderName;
            int amount;
            int auction;
        }
    static int MAX_MESSAGE_LENGTH = 10;
    static ArrayList<Bid> bids1 = new ArrayList<Bid>();
    static ArrayList<Bid> bids2 = new ArrayList<Bid>();
    static ArrayList<Bid> bids3 = new ArrayList<Bid>();

    /**
     * Method that truncates an event message before it's published.
     *
     * @param client WAMP client that sent the event
     * @param event Event to be truncated
     * @return Modified json event, null to halt publish
     */
    @onPublish("bidding")
    public static JsonNode truncatePublish(String sessionID, JsonNode event) {
        System.out.println("SIZE:" + bids1.size());
        System.out.println(event.toString());

        if (!event.get("auction").isInt()) {
            System.out.println("Argument is not a number!");
            return cancel();
        } else {
            System.out.println("Auction: " + event.get("auction").getIntValue());
        }

        if (!event.get("amount").isInt()) {
            System.out.println("Argument is not a number!");
            return cancel();
        } else {
            System.out.println("Amount: " + event.get("amount").getIntValue());
        }

        if (!event.get("bidder").isTextual()) {
            System.out.println("Argument is not a number!");
            return cancel();
        } else {
            System.out.println("Bidder: " + event.get("bidder").getTextValue());
        }

        int auction = event.get("auction").getIntValue();
        int amount = event.get("amount").getIntValue();
        String bidder = event.get("bidder").getTextValue();

        if ((auction >= 0) && (auction <= 3)) {
            Bid currentBid = null;
            switch (auction) {
                case 0:
                    if (bids1.size() > 0) {
                        currentBid = bids1.get(bids1.size() - 1);
                    }
                    break;
                case 1:
                    if (bids2.size() > 0) {
                        currentBid = bids2.get(bids2.size() - 1);
                    }
                    break;
                case 2:
                    if (bids3.size() > 0) {
                        currentBid = bids3.get(bids3.size() - 1);
                    }
                    break;
            }

if (currentBid != null){
                System.out.println("Amounts " + currentBid.amount + " " + amount);
                } else {
                    System.out.println("null");
                }
            if ((currentBid == null) || (amount > currentBid.amount)) {
                
                Bid newBid = new Bid();
                newBid.amount = amount;
                newBid.auction = auction;
                newBid.bidderName = bidder;
                switch (auction) {
                    case 0:
                        bids1.add(newBid);
                        break;
                    case 1:
                        bids2.add(newBid);
                        break;
                    case 2:
                        bids3.add(newBid);
                        break;
                }
                return event;
            } else {
                return cancel();
            }
        } else {
            return cancel();
        }

    }

    /**
     * Only one onPublish or onSubscribe annotation is necessary to create a
     * topic.
     *
     * @param subscribingClient
     * @return True if client is allowed to subscribe, false otherwise.
     */
    @onSubscribe("bidding")
    public static boolean capitalSubscribe(String sessionID) {
        return true;
    }
    
    @onRPC("highest")
    public static JsonNode getHighestBid(String sessionID, JsonNode event){
        int value = 0;
        if (!event.isInt()){
            System.out.println("No auction selected");
            return cancel();
        } else {
            switch (event.getIntValue()){
                case 0:
                    if (bids1.size() > 0){
                        value = bids1.get(bids1.size() - 1).amount;
                    }
                    break;
                case 1:
                    if (bids2.size() > 0){
                        value = bids2.get(bids2.size() - 1).amount;
                    }
                    break;
                    case 2:
                    if (bids3.size() > 0){
                        value = bids3.get(bids3.size() - 1).amount;
                    }
                    break;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
                
                JsonNode rootNode = null;
        try {
                rootNode = mapper.readTree("{\"amount\" : \"" + value + "\"}");

                
                //rootNode = mapper.readTree(message);
            } catch (IOException ex) {
                Logger.getLogger(Bidder.class.getName()).log(Level.SEVERE, null, ex);
            }

                                /**
                                 * * read value from key "name" **
                                 */
                                //return rootNode.path("value");
            System.out.println(rootNode.toString());
            return rootNode.path("value");
    }
    
}