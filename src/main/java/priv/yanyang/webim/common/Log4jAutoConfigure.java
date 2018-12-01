package priv.yanyang.webim.common;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;
import priv.yanyang.webim.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class Log4jAutoConfigure {

    @Autowired
    public Log4jAutoConfigure(ApplicationArguments arguments) throws IOException {
        String option = "spring.profiles.active";
        String active = "dev";

        boolean argu = arguments.containsOption(option);
        if(argu){
            List<String> actives = arguments.getOptionValues(option);
            active = actives.get(0);
        }

        String logCfgFile = String.format("/log4j-%s.properties",active);
        InputStream cfg = Application.class.getResourceAsStream(logCfgFile);
        PropertyConfigurator.configure(cfg);
        cfg.close();
    }

}
