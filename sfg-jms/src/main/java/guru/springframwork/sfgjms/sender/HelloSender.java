package guru.springframwork.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframwork.sfgjms.config.JmsConfig;
import guru.springframwork.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloSender
{
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage()
    {
        //System.out.println("I'm Sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
        //System.out.println("Message Sent!");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndRecieveMessage() throws JMSException
    {
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();


        Message receivedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator()
        {
            @Override
            public Message createMessage(Session session) throws JMSException
            {
                Message helloMessage = null;
                try
                {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "guru.springframwork.sfgjms.model.HelloWorldMessage");

                    System.out.println("Sending Hello");
                    return helloMessage;
                }
                catch (JsonProcessingException e)
                {
                    throw new JMSException("message failed");
                    //e.printStackTrace();
                }
            }

        });
        System.out.println(receivedMsg.getBody(String.class));
    }
}
