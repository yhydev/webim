package priv.yanyang.webim.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice("priv.yanyang.webim.controller")
public class JsonpControllerAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonpControllerAdvice(){
        super("callback");
    }

}
