package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import priv.yanyang.webim.common.JWTUtils;
import pub.yanyang.common.ResponseBody;

import java.util.HashMap;

@Service
public class OpenApiServiceImpl implements OpenApiService {

    private HashMap<String,String> openApiMap = new HashMap<>();

    public OpenApiServiceImpl(){
        openApiMap.put("webIM_ApiKey","webIM_SecretKey");
    }

    public ResponseBody auth(String apiKey, String authToken){

        String secretKey = openApiMap.get(apiKey);
        String errmsg = null;
        ResponseBody ret = null;

        if( StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(secretKey) ||StringUtils.isEmpty(authToken) ){
            errmsg = "Invalid apiKey or authToken";
        }else{
            DecodedJWT decodedJWT = null;
            try {
                decodedJWT = JWTUtils.getDecodeJWT(authToken,secretKey);

            }finally {

                if(decodedJWT == null || authToken.equals(decodedJWT.getClaim("authToken"))){
                    errmsg = "Authorized Failed";
                }
            }
        }

        if(null == errmsg){
            ret = ResponseBody.success(null);
        }else{
            ret = ResponseBody.error(errmsg);
        }
        return ret;
    }




}
