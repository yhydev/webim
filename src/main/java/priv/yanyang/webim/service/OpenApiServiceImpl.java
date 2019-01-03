package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import priv.yanyang.webim.common.AppProperties;
import priv.yanyang.webim.common.JWTUtils;
import pub.yanyang.common.ResponseBody;
import java.util.Base64;
import java.util.HashMap;

@Service
public class OpenApiServiceImpl implements OpenApiService,AppProperties {

    private HashMap<String,String> openApiMap = new HashMap<>();

    public OpenApiServiceImpl(){
        openApiMap.put("webIM_ApiKey","webIM_SecretKey");
    }

    public ResponseBody auth(String authToken){
        String errmsg = null;
        String authFail =  "Authentication failure";
        ResponseBody ret = null;
        String apiKey = null;

        if(StringUtils.isEmpty(authToken)){
            errmsg = "invalid authToken";
        }else{
            int ch = '.';
            int start = authToken.indexOf(ch) + 1;
            int end = authToken.indexOf(ch,start);

            if(0 < start && start < end ){
                String payload = authToken.substring(start,end);
                String payloadStr = new String(Base64.getDecoder().decode(payload));
                apiKey = (String) JSON.parseObject(payloadStr).get(API_KEY);
                String secretKey = openApiMap.get(apiKey);

                try {
                    if(StringUtils.isEmpty(secretKey) || null == JWTUtils.getDecodeJWT(authToken,secretKey)){
                        errmsg = "Authorized Failed";
                    }
                }catch (Exception e){
                    errmsg = authFail;
                }
            }
        }

        if(null == errmsg){
            ret = ResponseBody.success(apiKey);
        }else{
            ret = ResponseBody.error(errmsg);
        }
        return ret;
    }

}
