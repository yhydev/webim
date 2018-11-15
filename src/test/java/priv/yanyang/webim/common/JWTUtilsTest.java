package priv.yanyang.webim.common;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.junit.Assert.*;

public class JWTUtilsTest {


    String key = "testsecret";


    @Test
    public void builder() {

        Long now = System.currentTimeMillis();
        Date issueAt = new Date(now);
        try {
            String token = JWTUtils.builder().withClaim("type","OpenAPI")
                    .withClaim("sub","cee88ab0bc69435784b7db0545e85647")
                    .withExpiresAt(new Date(now + 1))
                    .withIssuedAt(issueAt)
                    .withClaim("nonce",1527665262168391000L).sign(Algorithm.HMAC256(key));


          //  token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiT3BlbkFQSSIsInN1YiI6ImNlZTg4YWIwYmM2OTQzNTc4NGI3ZGIwNTQ1ZTg1NjQ3Iiwibm9uY2UiOjE1Mjc2NjUyNjIxNjgzOTEwMDB9.YNpae4v_-OU7h2sknRPa3XPhDcC3p-To1WxbWV4Vpro";

            System.out.println(JWTUtils.getDecodeJWT(token,key).getClaim("nonce").asLong());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void getDecodeJWT() {

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.keH6T3x1z7mmhKL1T3r9sQdAxxdzB6siemGMr_6ZOwU";
        String key = "123456";
        DecodedJWT decodedJWT = JWTUtils.getDecodeJWT(token,key);
//        decodedJWT

    }
}