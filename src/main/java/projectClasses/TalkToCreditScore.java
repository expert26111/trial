/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectClasses;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bank.credit.web.service.CreditScoreService_Service;
import org.json.simple.JSONObject;
import static project.Starter.getFinalAnswers;

/**
 *
 * @author Yoana
 */
public class TalkToCreditScore 
{
    
            private JsonArray array;           
            private static final ExecutorService threadpool = Executors.newFixedThreadPool(1);
            
            public TalkToCreditScore(/*BankLoan loan*/)
            {
            }
            public JsonArray getYourScore(BankLoan loan) throws InterruptedException, ExecutionException 
            {
                                  JsonObject feetback = null;
                                  GetBaseScore task = new GetBaseScore(loan.getSsn());
                                  System.out.println("Submitting Task ..."); 
                                  Future future = threadpool.submit(task);
                                  System.out.println("Task is submitted");
        //                          while (!future.isDone())
        //                          { 
        //                              System.out.println("Task is not completed yet...."); 
        //                             // Thread.sleep(1);
        //                          } 
//                                  String resultCredit = (String) future.get();
                                  int resultCredit = (Integer) future.get();
                                  System.out.println("from getYourScore "+resultCredit);
                                  
                                  if(resultCredit > 300)
                                  {
                                      //method for rabbitmq
                                        loan.setCreaditScore(resultCredit);
                                        PublishToTranslator publish = new PublishToTranslator(loan);
                                        publish.sendToTRanslators();
                                      try 
                                      {
                                          
                                          while(System.currentTimeMillis() <= System.currentTimeMillis() + 6000) 
                                          {
                                                getFinalAnswers();
                                          }
                                         
                                        //  getFinalAnswers();//I receive a string
                                          
                                      } catch (IOException ex) 
                                      {
                                          Logger.getLogger(TalkToCreditScore.class.getName()).log(Level.SEVERE, null, ex);
                                      } catch (TimeoutException ex) 
                                      {
                                          Logger.getLogger(TalkToCreditScore.class.getName()).log(Level.SEVERE, null, ex);
                                      }
                                      
                                  }else
                                  {
                                      
                                      
                                     feetback = new JsonObject();
                                     feetback.addProperty("answer","not enough credit score");
                                     array.add(feetback);
                                  }
                                  
                           return array;
                                  
                        

        //             }catch(Exception ex)
        //             {
        //                       System.out.println("So sorry i dump!!!");
        //             }

            }
            
            
            
             public  void getFinalAnswers() throws IOException, TimeoutException
        {
            
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("datdb.cphbusiness.dk");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(Aggregator.QUEUENAME, false, false, false, null);
                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                
                Consumer consumer = new DefaultConsumer(channel) 
                {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException 
                    {
                        String message = new String(body, "UTF-8");
                        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
                        array.add(json);
                        System.out.println(" [x] Received '" + message + "'");
                    }
                };
                channel.basicConsume(Aggregator.QUEUENAME, true, consumer);

        }


               private  class GetBaseScore implements Callable
                {
                    private String ssn;
                    public GetBaseScore(String ssn)
                    {
                        this.ssn = ssn;
                    }

                    @Override
                    public Integer call() throws InterruptedException  
                    {
//                         try
//                            {
                                int coeficient = getScore(ssn);//THIS IS OUR CREDITSCORE
                                System.out.println("The coeeficient is "+coeficient);                
                                if (coeficient > 300)
                                {                               
                                   return coeficient; // create a messaging service and continue;
                                }else
                                {
                                    return -1;
                                }
                   }

                    private int getScore(String ssn)
                    {
                        System.out.println("I am inn getScore ");
                        System.out.println("The ssn is "+ssn);
//                        if(ssn.isEmpty() || ssn == null || !ssn.matches("\\d{6}-\\d{4}"))
//                        {
//                            throw new IllegalArgumentException("Provide valid social secutrity number following the number !!!");
//                        }
                        CreditScoreService_Service sf = new CreditScoreService_Service();
                        int bankcoeficient = sf.getCreditScoreServicePort().creditScore(ssn);
                        System.out.println("The coef in getScore "+bankcoeficient);
                        return bankcoeficient;
                    }
                }
       
       
       
}


        //    public static void main(String[] args) throws InterruptedException, ExecutionException
//            {
//
//                    String[] ssns = new String[]{"260790-4084","220291-4147","260790-4083","220291-4146"};
//                     //String[] ssns = new String[]{"260790-4084"};
//                    for (int i = 0; i < ssns.length; i++) 
//                    {
//                       // Future future = threadpool.submit(task);
//                        try
//                        {
//                             String result = getYourScore(ssns[i]);
//                             System.out.println("The result is "+result);
//
//                        }catch(Exception e)
//                        {
//                            System.out.println("Sorry bank crashed because "+e);
//                        }
//                    }
//    
//            }