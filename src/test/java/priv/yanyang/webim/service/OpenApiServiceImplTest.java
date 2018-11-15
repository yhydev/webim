package priv.yanyang.webim.service;

import org.junit.Test;
import pub.yanyang.common.ResponseBody;


public class OpenApiServiceImplTest {

    OpenApiService openApiService  = new OpenApiServiceImpl();

    @Test
    public void auth() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.ZEopzBQxH0-f_avegt2VBcwK7njBd58nNz08Lz5WJd4";
        ResponseBody resp = openApiService.auth(token, "webIM_ApiKey");
        System.out.println("resp = " + resp);


    }
}