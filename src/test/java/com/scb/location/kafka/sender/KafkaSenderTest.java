package com.scb.location.kafka.sender;

import com.scb.location.kafka.producer.Sender;
import com.scb.location.model.RiderLocation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.messaging.Message;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaSenderTest {

    @Mock
    private KafkaTemplate<String, RiderLocation> kafkaTemplate;

    private Sender sender;

    @BeforeEach
    public void setup() {
        sender = new Sender(kafkaTemplate, "topic");
    }

    @Test
    public void sender(){
        RiderLocation riderLocation = RiderLocation.builder()
                .channel("api")
                .lat(13.2)
                .lon(100.23)
                .riderId("test-rider-id")
                .build();
        ListenableFuture mockFuture = Mockito.mock(ListenableFuture.class);

        when(kafkaTemplate.send((Message<?>) any(Message.class))).thenReturn(mockFuture);
        sender.send(riderLocation);
        verify(kafkaTemplate, times(1))
                .send((Message<?>) any(Message.class));

    }
}
