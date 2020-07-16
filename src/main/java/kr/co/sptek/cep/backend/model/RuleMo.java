package kr.co.sptek.cep.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleMo {

  private String ruleId;
  private RuleState ruleState;
  private String keyNames;
  private String aggregateFieldName;
  private Integer interval;
  private AlertState alertState;
  private String descrion;
  private List<TriggerItem> triggerItems;
  private ControlType controlType;

  public enum RuleState {
    ACTIVE,
    PAUSE,
    DELETE,
    CONTROL
  }

  public enum ControlType {
    CLEAR_STATE_ALL,
    CLEAR_STATE_ALL_STOP,
    DELETE_RULES_ALL,
    EXPORT_RULES_CURRENT
  }

  public enum AlertState {
    INFO,
    MINOR,
    MAJOR,
    CRITICAL
  }
}
