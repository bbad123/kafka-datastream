package kr.co.sptek.cep.backend.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoRuleTriggerVo {

  private Integer ruleId;
  private String eventKey;
  private String expression;
  private String useYn;
}
