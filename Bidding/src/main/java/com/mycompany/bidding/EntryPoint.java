/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bidding;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import ws.wampee7.controllers.WAMPee7Server;

/**
 *
 * @author brendonbody
 */
@Startup
@Singleton
public class EntryPoint {
    public EntryPoint(){
        // Add WAMP controllers to WAMP
        WAMPee7Server.addController(new Bidder());
    }
}