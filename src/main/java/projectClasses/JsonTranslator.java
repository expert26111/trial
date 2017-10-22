
package projectClasses;

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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;


public class JsonTranslator
{
      private static final String EXCHANGE_NAME = "publishToTranslators";
      private BankLoan loan;
      private JSONObject json;
      
      public static String CORRID = "jsonBank";
      public static String REPLYQUEUENAME = "Manish&Yoana";
      
      private String replyQueueName;
      private Connection connection;
      private Channel channel;
      
      public JsonTranslator() throws IOException, TimeoutException 
      {
          
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            connection = factory.newConnection();
            channel = connection.createChannel();
         //   replyQueueName = channel.queueDeclare().getQueue();
//
//            try
//            {
//              receiveObject(); 
//
//            }catch(Exception ex)
//            {
//                ex.printStackTrace();
//                System.out.println("Could not create a JsonTranslator object");
//            }
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
                                         AMQP.BasicProperties properties, byte[] body) throws IOException 
                {
                     // String message = new String(body, "UTF-8");
                    //  System.out.println(" [x] Received '" + message + "'");
                    try
                    {
                     Object obj = getObjectForBytes(body);
                     json = returnJson(obj);
                     System.out.println("The json is "+json.toString());
                        
                    } catch (ClassNotFoundException ex)
                    {
                      Logger.getLogger(JsonTranslator.class.getName()).log(Level.SEVERE, null, ex);
                    }finally 
                           {
                        System.out.println(" [x] Done");
                        publishJsonData();//VERY INOVATIVE
                       //DDO NOT KILL THE 
                       // channel.basicAck(envelope.getDeliveryTag(), false);
                           }
                    
                }
            };
            channel.basicConsume(queueName, true, consumer);
            
      }
      
      //in progress
      public void publishJsonData() throws UnsupportedEncodingException, IOException
      {
          //this is how we will recognise this bank
       
//          JSONObject addvalue = json;
//          addvalue
          
           AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(CORRID)
                        .replyTo(REPLYQUEUENAME)
                        .build();
           
           channel.basicPublish("cphbusiness.bankJSON", "" , props, json.toString().getBytes("UTF-8"));
           
           
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
      
      private JSONObject returnJson(Object o)
      {          
                JSONObject json = null;
                if(o != null)
                {
                        if(o instanceof BankLoan)
                        {
                              BankLoan loan = (BankLoan) o;
                               json = new JSONObject();
                              json.put("ssn", loan.getSsn());
                              json.put("creditScore", loan.getCreaditScore());
                              json.put("loanAmount", loan.getLoanAmount());
                              json.put("loanDuration", loan.getLoanDuration());
                        }              
                }

                return json;
      }
              
      public static void main(String[] args) throws IOException, TimeoutException, Exception 
      {
         JsonTranslator  translator = new JsonTranslator();
         translator.receiveObject();
        // translator.publishJsonData();
         
         
      }
      
      
      
      
      
   }
