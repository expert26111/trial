/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectClasses;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
/**
 *
 * @author Yoana
 */
public class XMLTranslator
{
     
      private static final String EXCHANGE_NAME = "publishToTranslators";
      private BankLoan loan;
      //private JSONObject json;
      
      public static String CORRID = "xmlBank";
      public static String REPLYQUEUENAME = "Manish&Yoana";
      
    
      private Connection connection;
      private Channel channel;
   
      public XMLTranslator() throws IOException, TimeoutException
      {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            connection = factory.newConnection();
            channel = connection.createChannel();
      }
      
      
      
          public void receiveObject()  throws Exception 
      {
            
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicQos(1);
            
            Consumer consumer = new DefaultConsumer(channel) 
            {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope,
                                         AMQP.BasicProperties properties, byte[] body) throws IOException, UnsupportedEncodingException 
                {
                     // String message = new String(body, "UTF-8");
                    //  System.out.println(" [x] Received '" + message + "'");
                    byte[] xmlResponse = null;
                    String xmlString = null;
                    try
                    {
                        Object obj = getObjectForBytes(body);
                        System.out.println("the object is "+obj.toString());
                         xmlResponse = returnXML(obj);
                         System.out.println("the body is "+Arrays.toString(xmlResponse));
                        // xmlString = returnXML2(obj);
                
                        
                    } catch (ClassNotFoundException ex)
                    {
                        
                       ex.printStackTrace();
                      
                    }finally 
                           {
                  
                         System.out.println(" [x] Done");
                         publishXMLData(xmlResponse);//VERY INOVATIVE
                       //DDO NOT KILL THE 
                       // channel.basicAck(envelope.getDeliveryTag(), false);
                           }
                    
                }
            };
            channel.basicConsume(queueName, true, consumer);
            
      }
         
          
          public void publishXMLData(byte[] xml) throws IOException
          {
                String corrId = "xmlBank";
            
                 AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .correlationId(corrId)
                    .replyTo(REPLYQUEUENAME)
                    .build();

                 //exchange is cphbusiness.bankJSON
                channel.basicPublish("cphbusiness.bankXML", "", props, xml);
          }
          
          
          
           public static String returnXML2(Object o) throws IOException, TimeoutException, UnsupportedEncodingException, InterruptedException
    {
                String response = "";

    //            Customer customer = new Customer();
    //            customer.setCreditScore(450);
    //            customer.setLoanAmount(1000);
    //            customer.setLoanDuration(43200000);
    //            customer.setSsn(3607904084L);

                BankLoan customer =  (BankLoan) o;
                SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s zzz");

                           byte[] message = String.format("<LoanRequest>   "
                            + "<ssn>%1$s</ssn>   "
                            + "<creditScore>%2$d</creditScore>   "
                            + "<loanAmount>%3$d</loanAmount>   "
                            + "<loanDuration>%4$s</loanDuration> "
                            + "</LoanRequest>", customer.getSsn(), customer.getCreaditScore(), customer.getLoanAmount(), ddf.format(customer.getLoanDuration())).getBytes();


               // response  = bank.call(message);

               // response = bank.publishMessage(cust);

                System.out.println(" [.] Got '" + response + "'");
                return response;
        
    }
          
          
          
          
          
            private byte[] returnXML(Object o) throws UnsupportedEncodingException
      {          
          
                        byte[] message = null;
                        if(o != null)
                        {
                            System.out.println("I am in returnXML dif null");
                                if(o instanceof BankLoan)
                                 {
                                       System.out.println("I am in returnXML o like BankLoan");
                                       
                                       BankLoan customer =  (BankLoan) o;
                                       System.out.println("the sutomer object is  "+ customer);
                                       SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s zzz");
                                       
                                     
                                     message= String.format("<LoanRequest>   "
                                     + "<ssn>%1$s</ssn>   "
                                     + "<creditScore>%2$d</creditScore>   "
                                     + "<loanAmount>%3$.2f</loanAmount>   "
                                     + "<loanDuration>%4$s</loanDuration> "
                                     + "</LoanRequest>", customer.getSsn(), customer.getCreaditScore(), customer.getLoanAmount(), ddf.format(customer.getLoanDuration())).getBytes();
                                        
                                        
                                           System.out.println("I AM HERE "+message);
                                       
                                       //System.out.println("The message is "+message);
                                       
//                                       return message;
                                }              
                        }

                        return message;
                        
      }
      
        public static void main(String[] args) throws IOException, TimeoutException, Exception 
      {
         XMLTranslator  translator = new XMLTranslator();
         
         translator.receiveObject();
        // translator.publishJsonData();
         
         
      }
        
        
        
         private  Object getObjectForBytes(byte[] loan) throws ClassNotFoundException
            {
                
                            ByteArrayInputStream bis = new ByteArrayInputStream(loan);
                            ObjectInput in = null;
                            Object o = null;
                            try 
                            {
                              in = new ObjectInputStream(bis);
                              o = in.readObject(); 

                            }  catch (IOException ex ) 
                              {
                                ex.printStackTrace();
                              }
                            finally 
                            {
                                   try 
                                   {
                                     if (in != null) 
                                     {
                                       in.close();
                                     }
                                   }catch(Exception e)
                                   {
                                       e.printStackTrace();
                                   }
                             }

                         return o;
              }
      
      
      
}
