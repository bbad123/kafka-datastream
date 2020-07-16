package kr.co.sptek.cep.backend.controllers;

import java.util.List;
import kr.co.sptek.cep.backend.services.CepRulesService;
import kr.co.sptek.cep.backend.vo.TestVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

  @Autowired CepRulesService CepRulesService;

  @GetMapping(path = "/test")
  public List<TestVo> test() {
    List<TestVo> test = CepRulesService.test();

    LOGGER.info(String.valueOf(test));

    return test;
  }
}
