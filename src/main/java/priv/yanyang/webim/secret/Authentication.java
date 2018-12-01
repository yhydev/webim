package priv.yanyang.webim.secret;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import priv.yanyang.webim.service.OpenApiService;
import pub.yanyang.common.ResponseBody;

import javax.servlet.*;
import java.io.IOException;

/**
 * 认证Filter
 */
@Controller
public class Authentication implements Filter {

    @Autowired
    private OpenApiService openApiService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String authToken = servletRequest.getParameter("authToken");
        String apiKey = servletRequest.getParameter("apiKey");
        ResponseBody resp = openApiService.auth(apiKey,authToken);

        if(resp.getStatus() == 1){
            filterChain.doFilter(servletRequest,servletResponse);
        }else{
            String forwardUrl = "/warn?msg="+resp.getMsg()+"&httpStatus="+ HttpStatus.UNAUTHORIZED;
            servletRequest.getRequestDispatcher(forwardUrl).forward(servletRequest,servletResponse);
        }

    }



    @Override
    public void destroy() {

    }
}

