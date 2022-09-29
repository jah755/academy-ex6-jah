package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.api;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static org.slf4j.event.Level.INFO;

/** The Rest API controller of the Application */
@RestController
public class Controller {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private GreetingClientService greetingClientService;

  public Controller(GreetingClientService greetingClientService) {
    this.greetingClientService = greetingClientService;
  }
  /*
  Inputs: firstName, lastName

  Invokes greetingClientService.requestGreeting(firstName, lastName) which
  1. creates GreetingCreateRequest (in the end returned in the API response)
  2. sends GreetingCreateRequest on TOPIC_GREETING_CREATE_REQUEST topic

  And that would be it, if not for GreetingService.
  GreetingService listens on the TOPIC_GREETING_CREATE_REQUEST
  ... and because something has just come there, it processes it by:
  1. creating GreetingCreateResponse (which is almost the same as GreetingCreateRequest, but has an additional field 'greeting')
  2. sending the GreetingCreateResponse to TOPIC_GREETING_CREATE_RESPONSE topic

  GreetingClientService listens on TOPIC_GREETING_CREATE_RESPONSE, but only logs incoming GreetingCreateResponses

  */
  @GetMapping(value = "/api/request")
  public ResponseEntity<GreetingCreateRequest> request(@RequestParam String firstName, @RequestParam String lastName) {
    GreetingCreateRequest greetingCreateRequest = greetingClientService.requestGreeting(firstName, lastName);

    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Create greeting create request")
            .parameter("transactionId", greetingCreateRequest.getTransactionId())
            .parameter("requestTime", greetingCreateRequest.getRequestTime())
            .parameter("firstName", greetingCreateRequest.getFirstName())
            .parameter("lastName", greetingCreateRequest.getLastName())
            .build();

    return ResponseEntity.ok(greetingCreateRequest);
  }

  // query by key, what was retrieved from TOPIC_GREETING_CREATE_RESPONSE
  // convinience endpoint
  @GetMapping(value = "/api/response")
  public ResponseEntity<GreetingCreateResponse> response(@RequestParam String key) {
    GreetingCreateResponse greetingCreateResponse = greetingClientService.greetingCreateResponseByKey(key);

    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Lookup greeting create response by key")
            .parameter("key", key)
            .build();

    return ResponseEntity.ok(greetingCreateResponse);
  }


}
