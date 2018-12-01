package priv.yanyang.webim.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pub.yanyang.common.ResponseBody;

@RestController
@RequestMapping("/warn")
public class WarnController {



    @RequestMapping
    public HttpEntity get(String msg,int httpStatus){
        return new ResponseEntity(msg,HttpStatus.valueOf(httpStatus));
    }

}
