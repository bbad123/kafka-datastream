package kr.co.sptek.cep.backend.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RuleVo {
  private int ruleId;
  private String ruleState;
  private String groupingKeyNames;
  private String aggregateFieldName;
  private String aggregatorFunctionType;
  private String limitOperatorType;
  private BigDecimal limited;
  private int windowMinutes;
  private String controlType;
}
