package kr.co.sptek.cep.backend.services;

import java.util.List;
import kr.co.sptek.cep.backend.mapper.CommonMapper;
import kr.co.sptek.cep.backend.model.RuleMo;
import kr.co.sptek.cep.backend.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CepRulesService {

  @Autowired public CommonMapper commonMapper;

  public List<TestVo> test() {
    return commonMapper.test();
  }

  public void insertTest(RuleVo rule) {
    commonMapper.insertRule(rule);
  }

  public void insertMoRules(MoRuleVo rule) {
    commonMapper.insertMoRules(rule);
  }

  public void insertMoTriggers(MoRuleTriggerVo rule) {
    commonMapper.insertMoTriggers(rule);
  }

  public List<MoRuleVo> allMoRules() {
    return commonMapper.getRules();
  }

  public int getRuleId() {
    return commonMapper.getRuleId();
  }

  public List<RuleMo> getRule(Integer id) {
    return commonMapper.getRule(id);
  }

  public List<MoRuleVo> getRules() {
    return commonMapper.getRules();
  }

  public void deleteMoRule(MoRuleVo vo) {
    commonMapper.deleteMoRule(vo);
    commonMapper.deleteMoRuleTrigger(vo);
  }

  public List<MoRuleVo> getRulesId() {
    return commonMapper.getRulesId();
  }

  public List<TriggerItemVo> getTriggers(Integer ruleId) {
    return commonMapper.getTriggers(ruleId);
  }
}
