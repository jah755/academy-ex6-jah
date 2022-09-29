package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Instant;

/** The Spring Boot Application class */
@SpringBootApplication
@EnableScheduling // https://reflectoring.io/spring-scheduler/
public class SpringBootRestKafkaJsonApplication /* implements CommandLineRunner */ {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootRestKafkaJsonApplication.class, args);
  }

  /*
  // works, but why not scheduling?
  @Override
  public void run(String... args) {
    Thread t1 = new Thread(() -> {
      while(true) {
        try {
          Instant instant = Instant.now();
          System.out.println(instant);
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    t1.start();
  }
  */

}
