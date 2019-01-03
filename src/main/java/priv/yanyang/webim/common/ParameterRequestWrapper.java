package priv.yanyang.webim.common;

import org.apache.catalina.servlet4preview.http.HttpServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String,String[]> params = new HashMap<String,String[]>();

    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);
        params.putAll(request.getParameterMap());
    }

    public String[] getParameterValues(String name) {
        return params.get(name);
    }


    public String getParameter(String name) {
        String[] values = params.get(name);
        if(values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    public void addParameter(String name,String value){
        params.put(name , new String[] {(String)value});
    }
}
