package kr.co.sptek.cep.backend.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import kr.co.sptek.cep.backend.model.RuleMo;
import kr.co.sptek.cep.backend.model.TriggerItem;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoRuleVo {

  private Integer ruleId;
  private RuleMo.RuleState ruleState;
  private String keyNames;
  private String aggregateFieldName;
  private Integer avgInterval;
  private RuleMo.AlertState alertState;
  private String descrion;
  private List<TriggerItem> triggerItems;
  private String eventKey;
  private String expression;
  private RuleMo.ControlType controlType;
  private String useYn;
  private String gubun;
}
