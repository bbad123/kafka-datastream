package kr.co.sptek.cep.backend.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public class TriggerItemVo {

  private String eventKey;
  private String expression;
}
