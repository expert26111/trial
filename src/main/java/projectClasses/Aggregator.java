
package projectClasses;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.json.Json;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import static projectClasses.JsonTranslator.CORRID;
import static projectClasses.JsonTranslator.REPLYQUEUENAME;

/**
 *
 * @author Yoana
 */
public class Aggregator 
{
    //public static JSONArray myArray = new JSONArray(); 
   public static String QUEUENAME = "finalanswer";
   private Connection connection;
   private Channel channel;
   
   
   public Aggregator() throws IOException, TimeoutException
   {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            connection = factory.newConnection();
            channel = connection.createChannel();
   }
   
       public static void main(String[] args) throws IOException, TimeoutException 
    {
           Aggregator ag = new Aggregator();
           ag.receiveObject();
           
    }
   
   
   public void receiveObject() throws IOException
   {
        boolean durable = false;
        channel.queueDeclare(ReadFromTranslators.QUEUENAMEAGGREGATOR, durable, false, false, null);
           System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
          channel.basicQos(1);
           Consumer consumer = new DefaultConsumer(channel) 
           {
               @Override
               public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                   throws IOException 
               {
                   
                            String message = new String(body, "UTF-8");
                            System.out.println("message is "+message);
                            //String result = "";
                            String result = message.substring(message.indexOf(":") + 1, message.indexOf(","));
                            System.out.println("result "+result);
                             double result4e = Double.parseDouble(result);
                             if(result4e < 13)
                             {
                                  publishData(message);
                                 
                             }
                   
                             
                             
//                            double result;
//                            try {
//                                result = doWork(message);
//                                     System.out.println("result "+result);
//                            } catch (ParseException ex) {
//                                Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                   
//                          JSONObject json =new JSONObject(new String(body));
//                         String intrest = json.getString("interestRate");
//                         double result = Double.parseDouble(intrest);
                         //System.out.println("Result is "+result);
                    
                   
                   
                   
                   
//                        System.out.println("the consumed message is "+message);
//                        JSONParser parser = new JSONParser();
//                        JSONObject json = (JSONObject) parser.parse(message);
//                        String intrest = json.getString("interestRate");
//                        double result = result = Double.parseDouble(intrest);
//                        System.out.println("the result is "+result);
                            //double result =  doWork(message);
//                            if(result < 12)
//                            {
//                                System.out.println("yes it is less ");
//                          /      // publishData(json);
//                            }
                  
               }
           };
           channel.basicConsume(ReadFromTranslators.QUEUENAMEAGGREGATOR, true, consumer);
   }
   
   
            

    
    public static double doWork(String message) throws ParseException
    {
        
                   System.out.println("The string in doWork  is "+message);
                   JSONParser parser = new JSONParser();
                   double result = -1;                
                  
                      JSONObject json = (JSONObject) parser.parse(message);
                      System.out.println("the json object is "+json.toString());
                      String intrest = json.getString("interestRate");
                      
                      
                      
                 return     result = Double.parseDouble(intrest);
//                      System.out.println("The json object is "+json.toString());
                      
                       //publish to front 
                      // myArray.add(json);
                       //System.out.println(" [x] Received '" + message + "'");
                
//                   System.out.println("ther esult is "+result);
//                   return result;
                   
    }
    
    
         public static void publishData(String object) throws UnsupportedEncodingException, IOException
      {
          //this is how we will recognise this bank
       
//          JSONObject addvalue = json;
//          addvalue
          
//           AMQP.BasicProperties props = new AMQP.BasicProperties
//                        .Builder()
//                        .correlationId(CORRID)
//                        .replyTo(REPLYQUEUENAME)
//                        .build();
//           
//           channel.basicPublish("cphbusiness.bankJSON", "" , props, object.toString().getBytes("UTF-8"));
          
          
          //////////////////////////////////////
             try
            {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("datdb.cphbusiness.dk");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel(); 
                channel.queueDeclare(QUEUENAME, false, false, false, null);
               // String message = "Hello in Yoana's World!";
                //it useses default
                channel.basicPublish("", QUEUENAME, null, object.getBytes());
                //System.out.println(" [x] Sent '" + object.toString().getBytes() + "'");
                channel.close();
                connection.close();

            }catch(IOException e)
            {
                e.printStackTrace();

            }catch(TimeoutException ex)
            {
                 ex.printStackTrace();
            }
       
           
      }
 
}



//    public static void main(String[] args) throws IOException, TimeoutException 
//    {
//           ConnectionFactory factory = new ConnectionFactory();
//           factory.setHost("datdb.cphbusiness.dk");
//           Connection connection = factory.newConnection();
//           Channel channel = connection.createChannel();
//
//           channel.queueDeclare(ReadFromTranslators.QUEUENAMEAGGREGATOR, false, false, false, null);
//           System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//
//           Consumer consumer = new DefaultConsumer(channel) 
//           {
//               @Override
//               public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                   throws IOException 
//               {
//                   
//                   String message = new String(body, "UTF-8");
//                   double result = doWork(message);
//                   System.out.println("result "+result);
////                        System.out.println("the consumed message is "+message);
////                        JSONParser parser = new JSONParser();
////                        JSONObject json = (JSONObject) parser.parse(message);
////                        String intrest = json.getString("interestRate");
////                        double result = result = Double.parseDouble(intrest);
////                        System.out.println("the result is "+result);
//                            //double result =  doWork(message);
//                            if(result < 12)
//                            {
//                                System.out.println("yes it is less ");
//                                // publishData(json);
//                            }
//                  
//               }
//           };
//           channel.basicConsume(ReadFromTranslators.QUEUENAMEAGGREGATOR, true, consumer);
//    }
