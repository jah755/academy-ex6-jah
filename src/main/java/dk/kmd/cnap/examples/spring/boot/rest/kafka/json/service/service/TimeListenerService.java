package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaTopicName;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static org.slf4j.event.Level.INFO;

@Service
public class TimeListenerService implements KafkaTopicName {

    @KafkaListener(topics = TOPIC_TIME, containerFactory = "timeKafkaListenerContainerFactory", groupId = "group-time")
    public void consumeTime(Instant time) {
        logBuilder()
                .loggerName(getClass())
                .level(INFO)
                .message("New time has arrived!")
                .parameter("time", time.toString())
                .build();

        System.out.println("time: " + time);
    }

}
