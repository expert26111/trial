
package project;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import projectClasses.Aggregator;
import projectClasses.BankLoan;
import projectClasses.PublishToTranslator;


public class Starter
{
    
        public static void main(String[] args) throws IOException, TimeoutException
        {
              //idealy loan information comes from front request
            
            
            
            
            BankLoan loan = new BankLoan( "1607904084", 598,10.00, 360);
            PublishToTranslator publish = new PublishToTranslator(loan);
            publish.sendToTRanslators();
            getFinalAnswers();
            //make a consumer
           

        }
        
        public static void getFinalAnswers() throws IOException, TimeoutException
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
                        throws IOException {
                      String message = new String(body, "UTF-8");
                      System.out.println(" [x] Received '" + message + "'");
                    }
                };
                channel.basicConsume(Aggregator.QUEUENAME, true, consumer);

        }
        
        
        
}
