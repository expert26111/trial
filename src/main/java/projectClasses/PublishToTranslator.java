
package projectClasses;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.util.concurrent.TimeoutException;

public class PublishToTranslator 
{
    private static BankLoan loan;
    private static final String EXCHANGE_NAME = "publishToTranslators";
     
    public PublishToTranslator(BankLoan loan)
        {
            this.loan = loan;
        }
    
    
    public void sendToTRanslators()
    {
         try
            {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("datdb.cphbusiness.dk");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel(); 
               // boolean durable = true;
                //channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                byte[] message = getBytesForOBject(loan);
                channel.basicPublish(EXCHANGE_NAME, "", null, message);
                System.out.println(" [x] Sent '" + message + "'");
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
    

    
   private  byte[] getBytesForOBject(BankLoan loan) throws IOException
            {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutput out = null;
                        byte[] yourBytes = null;
                    try 
                    {
                        out = new ObjectOutputStream(bos);   
                        out.writeObject(loan);
                        out.flush();
                        yourBytes = bos.toByteArray();

                    } finally
                    {
                        try 
                        {
                          bos.close();
                        } catch (IOException ex) 
                        {
                          ex.printStackTrace();
                        }
                   }
                    return yourBytes;
         }
   
    
}
