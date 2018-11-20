package priv.yanyang.webim.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {




    public static JWTCreator.Builder builder(){
        Map<String,Object> header  = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        JWTCreator.Builder builder = JWT.create().withHeader(header);
        return builder;

    }

    /**
     *
     * @param token
     * @param key
     * @return  = null,authorized failed. != null, authorized success
     */
    public static DecodedJWT getDecodeJWT(String token,String key){

        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).build();
            jwt = verifier.verify(token);

        } catch (Exception e) {
            return null;
        }

        return jwt;
    }


}
