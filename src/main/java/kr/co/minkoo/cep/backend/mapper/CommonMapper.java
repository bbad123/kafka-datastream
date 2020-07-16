package kr.co.minkoo.cep.backend.mapper;

import java.util.List;

import kr.co.minkoo.cep.backend.vo.*;
import kr.co.minkoo.cep.backend.model.RuleMo;
import kr.co.sptek.cep.backend.vo.*;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CommonMapper {
  List<TestVo> test();

  void insertRule(RuleVo rule);

  void insertMoRules(MoRuleVo rule);

  void insertMoTriggers(MoRuleTriggerVo rule);

  List<MoRuleVo> allMoRules();

  int getRuleId();

  List<RuleMo> getRule(Integer id);

  List<MoRuleVo> getRules();

  void deleteMoRule(MoRuleVo vo);

  void deleteMoRuleTrigger(MoRuleVo vo);

  List<MoRuleVo> getRulesId();

  List<TriggerItemVo> getTriggers(Integer ruleId);
}
