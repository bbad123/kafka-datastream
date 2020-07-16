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

package kr.co.minkoo.cep.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;

import kr.co.minkoo.cep.backend.entities.Rule;
import kr.co.minkoo.cep.backend.model.RuleMo;
import kr.co.minkoo.cep.backend.model.RulePayload;
import kr.co.minkoo.cep.backend.model.TriggerItem;
import kr.co.minkoo.cep.backend.vo.MoRuleTriggerVo;
import kr.co.minkoo.cep.backend.vo.MoRuleVo;
import kr.co.minkoo.cep.backend.vo.RuleVo;
import kr.co.minkoo.cep.backend.vo.TriggerItemVo;
import kr.co.minkoo.cep.backend.exceptions.RuleNotFoundException;
import kr.co.sptek.cep.backend.model.*;
import kr.co.minkoo.cep.backend.repositories.MoRuleRepository;
import kr.co.minkoo.cep.backend.repositories.RuleRepository;
import kr.co.minkoo.cep.backend.responese.BaseResponse;
import kr.co.minkoo.cep.backend.responese.ResponseCode;
import kr.co.minkoo.cep.backend.services.FlinkRulesService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
class RuleRestController {

  private final RuleRepository repository;
  private final MoRuleRepository morepository;
  private final FlinkRulesService flinkRulesService;
  private static final Logger LOGGER = LoggerFactory.getLogger(RuleRestController.class);
  private static final BaseResponse response = new BaseResponse();

  @Autowired
  kr.co.minkoo.cep.backend.services.CepRulesService CepRulesService;

  RuleRestController(
      RuleRepository repository,
      MoRuleRepository morepository,
      FlinkRulesService flinkRulesService) {
    this.repository = repository;
    this.morepository = morepository;
    this.flinkRulesService = flinkRulesService;
  }

  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping("/rules")
  List<Rule> all() {
    return repository.findAll();
  }

  @GetMapping("/morules")
  List<RuleMo> allMo() {
    List<RuleMo> moList = new ArrayList<>();
    List<MoRuleVo> rules = CepRulesService.getRules();
    try {
      for (MoRuleVo rule : rules) {

        RuleMo ruleMo = new RuleMo();
        ruleMo.setRuleId(String.valueOf(rule.getRuleId()));
        ruleMo.setRuleState(rule.getRuleState());
        ruleMo.setAggregateFieldName(rule.getAggregateFieldName());
        ruleMo.setAlertState(rule.getAlertState());
        ruleMo.setControlType(rule.getControlType());
        ruleMo.setDescrion(rule.getDescrion());
        ruleMo.setInterval(rule.getAvgInterval());
        ruleMo.setKeyNames(rule.getKeyNames());

        List<TriggerItemVo> triggers = CepRulesService.getTriggers(rule.getRuleId());
        List<TriggerItem> list = new ArrayList<>();

        for (TriggerItemVo t : triggers) {

          TriggerItem triggerItem = new TriggerItem();
          triggerItem.setEventKey(t.getEventKey());
          triggerItem.setExpression(t.getExpression());

          list.add(triggerItem);
        }
        ruleMo.setTriggerItems(list);

        moList.add(ruleMo);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return moList;
  }

  @PostMapping("/rules")
  Rule newRule(@RequestBody Rule newRule) throws IOException {

    Rule savedRule = repository.save(newRule);
    Integer id = savedRule.getId();
    RulePayload payload = mapper.readValue(savedRule.getRulePayload(), RulePayload.class);
    payload.setRuleId(id);
    String payloadJson = mapper.writeValueAsString(payload);
    savedRule.setRulePayload(payloadJson);
    Rule result = repository.save(savedRule);
    flinkRulesService.addRule(result);

    /*rules 등록 후 DB저장*/
    RuleVo vo = new RuleVo();

    vo.setRuleId(newRule.getId());
    vo.setAggregateFieldName(payload.getAggregateFieldName());
    vo.setAggregatorFunctionType(String.valueOf(payload.getAggregatorFunctionType()));
    vo.setControlType(String.valueOf(payload.getControlType()));
    vo.setGroupingKeyNames(String.valueOf(payload.getGroupingKeyNames()));
    vo.setLimited(payload.getLimit());
    vo.setLimitOperatorType(String.valueOf(payload.getLimitOperatorType()));
    vo.setRuleState(String.valueOf(payload.getRuleState()));
    vo.setWindowMinutes(payload.getWindowMinutes());

    /*rule insert*/
    CepRulesService.insertTest(vo);

    return result;
  }

  @Transactional
  @SneakyThrows
  @PostMapping("/morules")
  public ResponseEntity<RuleMo> newMoRules(@RequestBody RuleMo newRule) throws RuntimeException {

    LOGGER.info("newMoRules  start =: " + newRule);
    MoRuleVo vo = new MoRuleVo();

    List<TriggerItem> triggerItems = newRule.getTriggerItems();

    try {

      vo.setAggregateFieldName(newRule.getAggregateFieldName());
      vo.setAlertState(newRule.getAlertState());
      vo.setControlType(newRule.getControlType());
      vo.setDescrion(newRule.getDescrion());
      vo.setAvgInterval(newRule.getInterval());
      vo.setKeyNames(newRule.getKeyNames());
      vo.setRuleState(newRule.getRuleState());
      vo.setUseYn("Y");

      CepRulesService.insertMoRules(vo);

      final int ruleId = CepRulesService.getRuleId();

      newRule.setRuleId(String.valueOf(ruleId));
      String moPayloadJson = mapper.writeValueAsString(newRule);
      flinkRulesService.addMoRules(moPayloadJson);

      for (TriggerItem t : triggerItems) {
        MoRuleTriggerVo triggerVo = new MoRuleTriggerVo();

        triggerVo.setRuleId(ruleId);
        triggerVo.setUseYn("Y");
        triggerVo.setEventKey(t.getEventKey());
        triggerVo.setExpression(t.getExpression());

        CepRulesService.insertMoTriggers(triggerVo);
        response.setCode(ResponseCode.Success);
      }
    } catch (Exception e) {
      e.printStackTrace();
      response.setCode(ResponseCode.InsertFail);
      throw new RuntimeException("RuntimeException for rollback!!");
    }
    LOGGER.info("newMoRules end");
    return new ResponseEntity(response, HttpStatus.OK);
  }

  @GetMapping("/morules/pushToFlink")
  void pushMoruleToFlink() {
    List<MoRuleVo> rules = CepRulesService.getRules();
    try {
      for (MoRuleVo rule : rules) {

        RuleMo ruleMo = new RuleMo();
        ruleMo.setRuleId(String.valueOf(rule.getRuleId()));
        ruleMo.setRuleState(rule.getRuleState());
        ruleMo.setAggregateFieldName(rule.getAggregateFieldName());
        ruleMo.setAlertState(rule.getAlertState());
        ruleMo.setControlType(rule.getControlType());
        ruleMo.setDescrion(rule.getDescrion());
        ruleMo.setInterval(rule.getAvgInterval());
        ruleMo.setKeyNames(rule.getKeyNames());

        List<TriggerItemVo> triggers = CepRulesService.getTriggers(rule.getRuleId());
        List<TriggerItem> list = new ArrayList<>();

        for (TriggerItemVo t : triggers) {

          TriggerItem triggerItem = new TriggerItem();
          triggerItem.setEventKey(t.getEventKey());
          triggerItem.setExpression(t.getExpression());

          list.add(triggerItem);
        }
        ruleMo.setTriggerItems(list);

        String push = mapper.writeValueAsString(ruleMo);
        flinkRulesService.addMoRulePush(push);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/rules/pushToFlink")
  void pushToFlink() {
    List<Rule> rules = repository.findAll();
    System.out.println("pushToFlink :::::::::: rules >>>" + rules);
    for (Rule rule : rules) {
      flinkRulesService.addRule(rule);
    }
  }

  @GetMapping("/morules/{id}")
  List<RuleMo> getMorule(@PathVariable Integer id) {
    List<RuleMo> rule = CepRulesService.getRule(id);
    return rule;
    //    return morepository.findById(id).orElseThrow(() -> new RuleNotFoundException(id));
  }

  @GetMapping("/rules/{id}")
  Rule getRule(@PathVariable Integer id) {
    return repository.findById(id).orElseThrow(() -> new RuleNotFoundException(id));
  }

  @DeleteMapping("/rules/{id}")
  void deleteRule(@PathVariable Integer id) throws JsonProcessingException {
    repository.deleteById(id);
    flinkRulesService.deleteRule(id);
  }

  @DeleteMapping("/rules")
  void deleteAllRules() throws JsonProcessingException {
    List<Rule> rules = repository.findAll();
    for (Rule rule : rules) {
      repository.deleteById(rule.getId());
      flinkRulesService.deleteRule(rule.getId());
    }
  }

  @DeleteMapping("/morules/{id}")
  void deleteMoRule(@PathVariable Integer id) {
    try {
      MoRuleVo vo = new MoRuleVo();
      vo.setRuleId(id);
      vo.setRuleState(RuleMo.RuleState.DELETE);
      CepRulesService.deleteMoRule(vo);
      flinkRulesService.deleteMoRule(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @DeleteMapping("/morules")
  void deleteAllMoRules() throws JsonProcessingException {
    List<MoRuleVo> rules = CepRulesService.getRulesId();
    try {
      for (MoRuleVo v : rules) {
        v.setRuleState(RuleMo.RuleState.DELETE);
        CepRulesService.deleteMoRule(v);
        flinkRulesService.deleteMoRule(v.getRuleId());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
