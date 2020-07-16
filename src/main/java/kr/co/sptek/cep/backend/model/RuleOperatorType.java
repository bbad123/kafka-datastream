package kr.co.sptek.cep.backend.model;

public enum RuleOperatorType {
  EQUAL("="),
  NOT_EQUAL("!="),
  GREATER_EQUAL(">="),
  LESS_EQUAL("<="),
  GREATER(">"),
  LESS("<");

  String operator;

  RuleOperatorType(String operator) {
    this.operator = operator;
  }

  public static RuleOperatorType fromString(String text) {
    for (RuleOperatorType b : RuleOperatorType.values()) {
      if (b.operator.equals(text)) {
        return b;
      }
    }
    return null;
  }
}
