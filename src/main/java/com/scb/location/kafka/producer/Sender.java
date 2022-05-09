package com.scb.location.kafka.producer;

import com.scb.location.model.RiderLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Service
@Slf4j
public class Sender {


    private KafkaTemplate<String, RiderLocation> kafkaTemplate;

    private String topic;

    @Autowired
    public Sender(
        KafkaTemplate<String, RiderLocation> kafkaTemplate,
        @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(RiderLocation data) {
        log.info("sending data to topic='{}'", topic);

        Message<RiderLocation> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, data.getRiderId())
                .build();
        ListenableFuture<SendResult<String, RiderLocation>> future = kafkaTemplate.send(message);
        future.addCallback(callback());
    }

    private ListenableFutureCallback<? super SendResult<String, RiderLocation>> callback() {
        return new ListenableFutureCallback<SendResult<String, RiderLocation>>() {

            @Override
            public void onSuccess(SendResult<String, RiderLocation> result) {
                log.info("Message published successfully");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while publishing message.", ex);
            }


        };

    }
}