/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.sptek.cep.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.co.sptek.cep.backend.datasource.DemoTransactionsGenerator;
import kr.co.sptek.cep.backend.datasource.MoDataGenerator;
import kr.co.sptek.cep.backend.datasource.TransactionsGenerator;
import kr.co.sptek.cep.backend.services.KafkaTransactionsPusher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@ComponentScan({"kr.co.sptek"})
public class DataGenerationController {

  private static Logger logger = LoggerFactory.getLogger(DataGenerationController.class);

  private TransactionsGenerator transactionsGenerator;
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
  private MoDataGenerator moDataGenerator;

  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private boolean generatingTransactions = false;
  private boolean generatingModata = false;
  private boolean listenerContainerRunning = true;

  @Value("${kafka.listeners.transactions.id}")
  private String transactionListenerId;

  @Value("${transactionsRateDisplayLimit}")
  private int transactionsRateDisplayLimit;

  @Autowired
  public DataGenerationController(
      KafkaTransactionsPusher transactionsPusher,
      KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
    transactionsGenerator = new DemoTransactionsGenerator(transactionsPusher, 1);
    moDataGenerator = new MoDataGenerator();
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
  }

  @Autowired private MoDataGenerator moDataGen;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private Producer<Integer, String> producer;

  @GetMapping("/api/startTransactionsGeneration")
  public void startTransactionsGeneration(boolean startStop) throws Exception {

    log.info("{}", "start generateMoDataGenerations called");

    generatingModata = startStop;

    while (!generatingModata) {

      generateMoDataGenerations();
    }
  }

  private void generateTransactions() {
    if (!generatingTransactions) {
      executor.submit(transactionsGenerator);
      generatingTransactions = true;
    }
  }

  private void generateMoDataGenerations() {

    String jsonMoData = "";
    int key = 0;

    try {
      jsonMoData = objectMapper.writeValueAsString(moDataGen.randomEvent(0));

      Sleep();
      // logger.info("MoData Json == > " + jsonMoData);

    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ProducerRecord<Integer, String> record =
        new ProducerRecord<>("livetransactions", key++, jsonMoData);

    producer.send(
        record,
        new Callback() {
          @Override
          public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (metadata != null) {
              //            	logger.info("[moData]Success Send partitiction :" +
              // metadata.partition() + " offset :" + metadata.offset());
            } else {
              //                String msg = "현재 시간 : "+ new Date();
              //            	logger.error("[moData]Faild :" + msg);
            }
          }
        });
  }

  @GetMapping("/api/stopTransactionsGeneration")
  public void stopTransactionsGeneration() {
    //    transactionsGenerator.cancel();
    //    generatingTransactions = true;
    try {
      startTransactionsGeneration(true);
    } catch (Exception e) {
      e.printStackTrace();
    }

    log.info("{}", "stopTransactionsGeneration called");
  }

  @GetMapping("/api/generatorSpeed/{speed}")
  public void setGeneratorSpeed(@PathVariable Long speed) {
    log.info("Generator speed change request: " + speed);
    if (speed <= 0) {
      transactionsGenerator.cancel();
      generatingTransactions = false;
      return;
    } else {
      generateTransactions();
    }

    MessageListenerContainer listenerContainer =
        kafkaListenerEndpointRegistry.getListenerContainer(transactionListenerId);
    if (speed > transactionsRateDisplayLimit) {
      listenerContainer.stop();
      listenerContainerRunning = false;
    } else if (!listenerContainerRunning) {
      listenerContainer.start();
    }

    if (transactionsGenerator != null) {
      transactionsGenerator.adjustMaxRecordsPerSecond(speed);
    }
  }

  public void Sleep() {
    try {
      Thread.sleep(2000); // 1초 대기
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
