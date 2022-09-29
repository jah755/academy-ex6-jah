package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

// @Configuration - we will define some @Bean s here
@Configuration
// @EnableKafka enables detection of KafkaListener annotations on any Spring-managed bean in the container.
// For more info check KafkaAnnotationDrivenConfiguration class
@EnableKafka
public class KafkaConfig implements KafkaTopicName {

  // with @Value we inject application properties (application.yml, application-openshift.yml) depending on active profile
  // and if a property is unavailable, we have default values defined after ':'
  @Value("${spring.kafka.bootstrap.servers}")
  private String kafkaBootstrapServers;

  @Value("${spring.kafka.replicas:1}")
  private Integer replicas;

  @Value("${spring.kafka.partitions:1}")
  private Integer partitions;

  // Shared Kafka properties

  @Bean
  public Map kafkaProperties() {
    // .of(K k1, V v1, K k2, V v2)
    return Map.of(
        BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers,
            /* JsonDeserializer.TRUSTED_PACKAGES (default java.util, java.lang): Comma-delimited list of
            package patterns allowed for deserialization. * means deserialize all. */
        JsonDeserializer.TRUSTED_PACKAGES, "*"
    );
  }

  // KafkaAdmin bean will automatically add topics for all beans of type NewTopic
  // see -> 2 beans below
  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic topicCreateMessageRequest() {
    return TopicBuilder.name(TOPIC_GREETING_CREATE_REQUEST)
        .partitions(partitions)
        .replicas(replicas)
        .build();
  }

  @Bean
  public NewTopic topicCreateMessageResponse() {
    return TopicBuilder.name(TOPIC_GREETING_CREATE_RESPONSE)
        .partitions(partitions)
        .replicas(replicas)
        .build();
  }

  @Bean
  public NewTopic topicForTime() {
    return TopicBuilder.name(TOPIC_TIME)
            .partitions(partitions)
            .replicas(replicas)
            .build();
  }

  // todo read this! https://www.stackchief.com/blog/%40KafkaListener%20%7C%20Spring%20Boot%20Example
  // Kafka factories for listeners
  @Bean("greetingCreateRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, GreetingCreateRequest>
  greetingCreateRequestKafkaListenerContainerFactory(final Map kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, GreetingCreateRequest>
        concurrentContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    // enables spinning as many threads as many partitions we have
    concurrentContainerFactory.setConcurrency(partitions);
    DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(kafkaProperties, new StringDeserializer(), new JsonSerde<GreetingCreateRequest>().deserializer());
    concurrentContainerFactory.setConsumerFactory(consumerFactory);
    return concurrentContainerFactory;
  }

  @Bean("greetingCreateResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, GreetingCreateResponse>
  greetingCreateResponseKafkaListenerContainerFactory(final Map kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, GreetingCreateResponse>
        concurrentContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    // enables spinning as many threads as many partitions we have
    concurrentContainerFactory.setConcurrency(partitions);
    DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(kafkaProperties, new StringDeserializer(), new JsonSerde<GreetingCreateResponse>().deserializer());
    concurrentContainerFactory.setConsumerFactory(consumerFactory);
    return concurrentContainerFactory;
  }

  @Bean("timeKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, Instant> timeKafkaListenerContainerFactory(final Map kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, Instant>
            concurrentContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentContainerFactory.setConcurrency(partitions);
    DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(kafkaProperties, new StringDeserializer(), new JsonSerde<Instant>().deserializer());
    concurrentContainerFactory.setConsumerFactory(consumerFactory);
    return concurrentContainerFactory;
  }

  // Kafka producers

  @Bean
  public KafkaProducer<String, GreetingCreateRequest> greetingCreateRequestKafkaProducer(
      final Map kafkaProperties) {
    return new KafkaProducer(kafkaProperties, new StringSerializer(), new JsonSerde<GreetingCreateRequest>().serializer());
  }

  @Bean
  public KafkaProducer<String, GreetingCreateResponse> greetingCreateResponseKafkaProducer(
      final Map kafkaProperties) {
    return new KafkaProducer(kafkaProperties, new StringSerializer(), new JsonSerde<GreetingCreateResponse>().serializer());
  }

  @Bean
  public KafkaProducer<String, Instant> instantTimeProducer(final Map kafkaProperties) {
    return new KafkaProducer(kafkaProperties, new StringSerializer(), new JsonSerde<Instant>().serializer());
  }

}
