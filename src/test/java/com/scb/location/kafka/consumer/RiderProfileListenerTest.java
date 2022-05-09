package com.scb.location.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.model.RiderLocation;
import com.scb.location.model.RiderProfileEventModel;
import com.scb.location.service.rider.RiderLocationService;
import com.scb.location.service.rider.RiderProfileUpdateService;
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
public class RiderProfileListenerTest {

  private RiderProfileListener listener;

  @Mock
  private RiderProfileUpdateService riderProfileUpdateService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setup(){
    listener = new RiderProfileListener(objectMapper, riderProfileUpdateService);

  }

  @Test
  public void listenerTest() throws IOException {

    RiderProfileEventModel riderProfileEventModel = RiderProfileEventModel.builder().riderId("riderId")
            .build();
    listener.receive(objectMapper.writeValueAsString(riderProfileEventModel));
    verify(riderProfileUpdateService, times(1))
        .updateRiderProfileData(any(RiderProfileEventModel.class));

  }

}
