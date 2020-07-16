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

package kr.co.minkoo.cep.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.minkoo.cep.backend.entities.Rule;
import kr.co.minkoo.cep.backend.model.RuleMo;
import kr.co.minkoo.cep.backend.model.RulePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FlinkRulesService {

  private KafkaTemplate<String, String> kafkaTemplate;

  @Value("${kafka.topic.rules}")
  private String topic;

  @Value("${kafka.topic.morules}")
  private String motopic;

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  public FlinkRulesService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void addRule(Rule rule) {
    String payload = rule.getRulePayload();
    kafkaTemplate.send(topic, payload);
  }

  public void addMoRulePush(String rule) {
    kafkaTemplate.send(motopic, rule);
  }

  public void addMoRules(String rule) {
    kafkaTemplate.send(motopic, rule);
  }

  public void deleteRule(int ruleId) throws JsonProcessingException {
    RulePayload payload = new RulePayload();
    payload.setRuleId(ruleId);
    payload.setRuleState(RulePayload.RuleState.DELETE);
    String payloadJson = mapper.writeValueAsString(payload);
    kafkaTemplate.send(topic, payloadJson);
  }

  public void deleteMoRule(int ruleId) throws JsonProcessingException {
    RuleMo ruleMo = new RuleMo();
    ruleMo.setRuleId(String.valueOf(ruleId));
    ruleMo.setRuleState(RuleMo.RuleState.DELETE);
    String payloadJson = mapper.writeValueAsString(ruleMo);
    kafkaTemplate.send(motopic, payloadJson);
  }
}
