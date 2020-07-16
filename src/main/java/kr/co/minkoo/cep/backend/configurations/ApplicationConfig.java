package kr.co.minkoo.cep.backend.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  private Producer<Integer, String> producer;

  private ObjectMapper objectMapper = new ObjectMapper();

  public ApplicationConfig() {

    // 카푸카 프로듀서 초기화 및 생성
    Properties conf = new Properties();
    conf.setProperty("bootstrap.servers", "localhost:9092");
    conf.setProperty("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    conf.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    producer = new KafkaProducer<>(conf);
    objectMapper = new ObjectMapper();
  }

  @Bean
  public Producer<Integer, String> getProducer() {
    return producer;
  }

  @Bean
  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
