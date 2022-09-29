package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config;

// Holds topic names, which will be created on application startup, if they don't already exist
public interface KafkaTopicName {
    String TOPIC_GREETING_CREATE_REQUEST = "public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.request";
    String TOPIC_GREETING_CREATE_RESPONSE = "public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.response";

    String TOPIC_TIME = "topic_time-jah";
}
