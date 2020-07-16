package kr.co.minkoo.cep.backend.responese;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse {

  protected int code;
  protected String message;

  public void setCode(int code) {
    this.code = code;
    this.message = ResponseCode.getMessage(code);
  }

  public static BaseResponse of(HttpStatus badRequest) {
    BaseResponse res = new BaseResponse();
    res.code = badRequest.value();
    res.message = badRequest.getReasonPhrase();
    return res;
  }
}
