package kr.co.minkoo.cep.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class RuleExpression {

  private RuleOperatorType operator;
  private Integer threshold;
  private Condition condition;

  public RuleExpression() {}
}
