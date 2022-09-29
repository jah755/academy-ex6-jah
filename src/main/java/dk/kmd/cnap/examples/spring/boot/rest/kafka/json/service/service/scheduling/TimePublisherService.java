package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.scheduling;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaTopicName;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;

@Service
public class TimePublisherService {
    private final KafkaProducer<String, Instant> instantTimeProducer;

    public TimePublisherService(KafkaProducer<String, Instant> instantTimeProducer) {
        this.instantTimeProducer = instantTimeProducer;
    }

    @Scheduled(fixedDelay = 10000)
    public void keepOnSendingTime() {
        Instant instant = Instant.now();
        String generatedMessageKey = UUID.randomUUID().toString();

        final ProducerRecord<String, Instant> record = new ProducerRecord(KafkaTopicName.TOPIC_TIME, generatedMessageKey, instant);
        instantTimeProducer.send(record, (recordMetadata, e) -> {
            if (e != null)
                logBuilder()
                        .loggerName(getClass())
                        .level(ERROR)
                        .message("Failed to send time!")
                        .parameter("time", instant)
                        .parameter("kafkaTopic", KafkaTopicName.TOPIC_TIME)
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .parameter("exceptionMessage", e.getMessage())
                        .build();
            else
                logBuilder()
                        .loggerName(getClass())
                        .level(INFO)
                        .message("Sent time on Kafka!")
                        .parameter("transactionId", generatedMessageKey)
                        .parameter("kafkaTopic", recordMetadata.topic())
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .build();
        });
    }
}
