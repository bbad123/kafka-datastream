package kr.co.minkoo.cep.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class RuleThreshold {

  private String eventKey;
  private String expressions;
  //  private Condition condition;
}
