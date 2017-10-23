/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.siproject3;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import projectClasses.BankLoan;
import projectClasses.TalkToCreditScore;

/**
 * REST Web Service
 *
 * @author Yoana
 */
@Path("getinterest")
public class GenericResource {

    private TalkToCreditScore score;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() 
    {
        
    }

    /**
     * Retrieves representation of an instance of com.mycompany.siproject3.GenericResource
     * @return an instance of java.lang.String
     */
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(String loan) 
    {

//        
//            BankLoan loan4e = new BankLoan();
//            JsonObject json = new JsonParser().parse(loan).getAsJsonObject();
//
//            loan4e.setSsn(json.get("ssn").getAsString());
//            loan4e.setLoanAmount(json.get("loanAmount").getAsDouble());
//            loan4e.setLoanDuration(json.get("loanDuration").getAsDouble());
//            //getting credit score as passing whole object consisting of ssn
//            score = new TalkToCreditScore();
//            int scoreR = -10;
//            try 
//            {
//                
//               scoreR = score.getYourScore(loan4e);
//               System.out.println("the score from Post method is "+scoreR);
//
//            } catch (InterruptedException ex)
//            {
//                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
//
//            } catch (ExecutionException ex) 
//            {
//                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
//
//            }
//
//            JsonObject feetback = new JsonObject();
//            feetback.addProperty("score",scoreR);
//            return feetback.toString();
            return "ha";
    } // End of post()
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
    public String getJson() 
    {
        JsonObject feetback = new JsonObject();
        feetback.addProperty("message", "Get method of project");
        return feetback.toString();

    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
  
}
