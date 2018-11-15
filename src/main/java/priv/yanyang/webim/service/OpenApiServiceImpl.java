package priv.yanyang.webim.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import priv.yanyang.webim.common.JWTUtils;
import pub.yanyang.common.ResponseBody;

import java.util.HashMap;

@Service
public class OpenApiServiceImpl implements OpenApiService {

    private HashMap<String,String> openApiMap = new HashMap<>();

    public OpenApiServiceImpl(){
        openApiMap.put("webIM_ApiKey","webIM_SecretKey");
    }

    public ResponseBody auth(String token, String apiKey){

        String secretKey = openApiMap.get(apiKey);
        String errmsg = null;
        ResponseBody ret = null;

        if(null == secretKey){
            errmsg = "Invalid apiKey";
        }else{
            DecodedJWT decodedJWT = JWTUtils.getDecodeJWT(token,secretKey);
            errmsg = null == decodedJWT ? "Authorized Failed" : null;
        }

        if(null == errmsg){
            ret = ResponseBody.success(null);
        }else{
            ret = ResponseBody.error(errmsg);
        }
        return ret;
    }




}
