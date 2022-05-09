package com.scb.location.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.model.AvailabilityStatus;
import com.scb.location.model.RiderLocation;
import com.scb.location.service.rider.RiderLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class kafkaListenerTest {

  private Listener listener;

  @Mock
  private RiderLocationService riderLocationService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setup(){
    listener = new Listener(objectMapper, riderLocationService);

  }

  @Test
  public void listenerTest() throws IOException {

    RiderLocation riderLocation = new RiderLocation("riderid",13.10, 100.10, "2020-12-10T10:10:10+05:30", "2020-12-10T10:10:10+05:30", "API", AvailabilityStatus.Active);
    doNothing().when(riderLocationService)
        .updateInsertRiderLocationEntity("riderid", 100.10, 13.10, "2020-12-10T10:10:10+05:30");
    listener.receive(objectMapper.writeValueAsString(riderLocation));
    verify(riderLocationService, times(1))
        .updateInsertRiderLocationEntity("riderid", 100.10, 13.10, "2020-12-10T10:10:10+05:30");

  }


  @Test
  public void testListenerConstraintViolations(){
    RiderLocation riderLocation = new RiderLocation(null, null,null, null, null,null,null);
    assertThrows(ConstraintViolationException.class, () ->
            listener.receive(objectMapper.writeValueAsString(riderLocation)));
  }

}
