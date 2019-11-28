package guru.springframwork.sfgjms.listener;

import guru.springframwork.sfgjms.config.JmsConfig;
import guru.springframwork.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloMessageListener
{
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, Message message)
    {
        //System.out.println("I got a message ");
        //System.out.println(helloWorldMessage);

        // throw new RuntimeException("foo");
    }

    //Spring implementation
    // public void listenForHello(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, org.springframework.messaging.Message message) throws JMSException
    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHello(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, Message message) throws JMSException
    {
        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!")
                .build();
        //Spring implementation
        // jmsTemplate.convertAndSend((Destination) message.getHeaders().get("jms_replyTo"), "got it");
        //use jms message
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payloadMsg);
        // throw new RuntimeException("foo");
    }
}
