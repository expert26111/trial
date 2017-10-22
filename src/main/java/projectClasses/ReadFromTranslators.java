
package projectClasses;

//import com.mycompany.systemintegrationproject.NewConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
/**
 *
 * @author Yoana 
 */
public class ReadFromTranslators 
{
   
   
//private final static String QUEUE_NAME = "yoanaDurable";
 
  public static JSONArray myArray = new JSONArray(); 
  public static String QUEUENAMEAGGREGATOR = "aggregator";
  
  
public void readFromJsonTranslator() throws Exception 
    {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("datdb.cphbusiness.dk");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            boolean durable = true;
            channel.queueDeclare(JsonTranslator.REPLYQUEUENAME, durable, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicQos(1);
            Consumer consumer = new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                  throws IOException, UnsupportedEncodingException 
              {
                //String message = new String(body, "UTF-8");
               // System.out.println(" [x] Received '" + message + "'");
                  System.out.println("THE CORREID IS "+properties.getCorrelationId());
                  if (properties.getCorrelationId().equals(JsonTranslator.CORRID)) 
                        {         
                            JSONObject json = null;
                                try 
                                {
                                  //  System.out.println("The string is "+body.toString());
                                 json = doWork(body);

                                } catch (InterruptedException ex) 
                                {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (ParseException ex)
                                {                
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                }   finally 
                                {
                                     if(json != null)
                                    {
                                        System.out.println("to aggregator is "+json);
                                        sendToAggregator(json);
                                    }
                                    
                                }               
                        }else if(properties.getCorrelationId().equals(XMLTranslator.CORRID))
                        {
                                // System.out.println(" XML bank is here !!! ");
                               JSONObject json = null;
                                try 
                                {
                                  json = doWorkXML(body);
                                } catch (InterruptedException ex)
                                {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (ParseException ex) 
                                {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (ParserConfigurationException ex) {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SAXException ex) {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (XPathExpressionException ex) {
                                    Logger.getLogger(ReadFromTranslators.class.getName()).log(Level.SEVERE, null, ex);
                                }finally 
                                {
                                    if(json != null)
                                    {
                                        System.out.println("to aggregator is "+json);
                                        sendToAggregator(json);
                                    }
                                }
                        }else
                        {
                            System.out.println("Sorry dude no banks like that...");
                        }
              }
            };
            //we want to send proper acknowledment
            boolean autoAck = false; // acknowledgment is covered below
            //deleted autoAck property that was in the middle
            channel.basicConsume(JsonTranslator.REPLYQUEUENAME,true, consumer);

      }

        private static JSONObject doWorkXML(byte[] task) throws InterruptedException, ParseException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException 
        {
             String message = new String(task, "UTF-8");
//              System.out.println(" [x] Received '" + message + "'");  
//              String json = readXMLString(message);
              //LETS READ THE BYTES
             DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder builder = builderFactory.newDocumentBuilder();
             Document loanRequestXml = builder.parse(new ByteArrayInputStream(message.getBytes()));
             XPath xPath = XPathFactory.newInstance().newXPath();
             Element loanDetailsElement = (Element) xPath.compile("/LoanResponse").evaluate(loanRequestXml, XPathConstants.NODE);
             String ssn = loanDetailsElement.getElementsByTagName("ssn").item(0).getTextContent();
             double interestRate = Double.parseDouble(loanDetailsElement.getElementsByTagName("interestRate").item(0).getTextContent());
            // String bank = loanDetailsElement.getElementsByTagName("bank").item(0).getTextContent();
//             System.out.println("Print Values from "+interestRate);
//             System.out.println("Print Values from ssn "+ssn);\
//             JSONParser parser = new JSONParser();
//             JSONObject json = (JSONObject) parser.parse(message);
//             json.put ("bank","bankJson");
//             System.out.println("The new json object is "+json.toString()); 
            
        
               JSONObject json = new JSONObject();
               json.put("ssn",ssn);
               json.put("interestRate",interestRate);
               json.put("bank","bankXMl");
               
               return json;
               
             //  myArray.add(json);
               
//              
        }
       
            public static void sendToAggregator(JSONObject json)
            {
                     try
                        {
                            ConnectionFactory factory = new ConnectionFactory();
                            factory.setHost("datdb.cphbusiness.dk");
                            Connection connection = factory.newConnection();
                            Channel channel = connection.createChannel(); 

                            channel.queueDeclare(QUEUENAMEAGGREGATOR, false, false, false, null);
                            //it useses default
                            channel.basicPublish("", QUEUENAMEAGGREGATOR, null, json.toString().getBytes());
                           // myArray = new JSONArray();
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


        private static JSONObject doWork(byte[] task) throws InterruptedException, ParseException, UnsupportedEncodingException 
        {
            
            
             String message = new String(task, "UTF-8");
           // System.out.println("THE MESSAGE IS "+message);
//             JsonElement element = new JsonPrimitive(yourString);
//JsonObject result = element.getAsJsonObject();
             //String data = task.toString();
//             System.out.println("the data is "+data);
             JSONParser parser = new JSONParser();
             JSONObject json = (JSONObject) parser.parse(message);
             json.put ("bank","bankJson");
             
             return json;
             
             //myArray.add(json);
             
            // System.out.println("The new json object is "+json.toString());
            
        }  
        
        public static void main(String[] args) throws Exception 
        {
            ReadFromTranslators  tr = new ReadFromTranslators();
            tr.readFromJsonTranslator();
            //GOOD IDEA FOR THREADS USE
//            Thread.sleep(10000);
//            if(!myArray.isEmpty())
//            {
//              tr.sendToAggregator();
//                
//            }else
//            {
//             Thread.sleep(5000);
//            }
            
            
        }
     
}
