package kr.co.minkoo.cep.backend.responese;

import lombok.Data;

@Data
public class ResponseCode {
  public static final int Success = 0; // Success
  public static final int InsertFail = 100; // Data Insert Failed

  protected int code;
  protected String message;

  public static String getMessage(int code) {
    switch (code) {
      case Success:
        return "Success";
      case InsertFail:
        return "Data Insert Failed";

      default:
        return "Unknown Error";
    }
  }
}
