package com.scb.location.kafka.consumer;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Durations;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.scb.location.LocationServiceApplication;
import com.scb.location.model.RiderLocationEntity;
import com.scb.location.repository.rider.RiderLocationRespository;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocationServiceApplication.class)
@DirtiesContext
@EmbeddedKafka(topics = {"rider-location"})
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"})
@ActiveProfiles("test")
@Slf4j
public class ListenerIntegrationTest {

  private static final String TOPIC_EXAMPLE = "rider-location";

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired
  private RiderLocationRespository riderLocationRepository;

  @Test
  public void testMessage() throws InterruptedException, ExecutionException {

    Set<String> rider_Ids = new HashSet<>();
    Map<String, Object> producerProps =
        KafkaTestUtils.producerProps(embeddedKafkaBroker.getBrokersAsString());

    Producer<String, String> producerTest =
        new KafkaProducer(producerProps, new StringSerializer(), new StringSerializer());

    for (String message : riderLocations()) {
      JSONObject object = new JSONObject(message);
      producerTest.send(new ProducerRecord(TOPIC_EXAMPLE, "rider-location-group", message)).get();
      rider_Ids.add(object.get("riderId").toString());
    }
    producerTest.flush();

    await().atMost(Durations.FIVE_SECONDS).untilAsserted(() -> {
      rider_Ids.stream().forEach(rider_id -> {
        Optional<RiderLocationEntity> riderLocation = riderLocationRepository.findById(rider_id);
        if (!riderLocation.isPresent()) {
          log.error(String.format("Rider %s not found in DB", rider_id));
          assertFalse(true);
        } else {
          log.info(String.format("Rider %s is found in DB", rider_id));
        }

      });
    });
    producerTest.close();
  }

  public List<String> riderLocations() {
    List<String> ridersLocation = new ArrayList<>();
    String message =
        "{\"riderId\": \"%s\", \"lat\": \"1230.1\", \"lon\": \"111233.1\", \"dateTime\": \"2020-07-14T14:31:30+0530\"}";
    for (int i = 0; i < 4; i++) {
      String riderId = "TEST_ID_" + UUID.randomUUID().toString();
      ridersLocation.add(String.format(message, riderId));
    }
    return ridersLocation;
  }
}
