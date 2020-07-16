package kr.co.sptek.cep.backend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class TriggerItem {

  private String eventKey;
  private String expression;
}
