package wacky.storagesign.Logging;

import ch.qos.logback.classic.Level;
import org.bukkit.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SSLoggerFactory {
    private static String _LOGGERNAME;

    public SSLoggerFactory(String level){

        if(Objects.isNull(_LOGGERNAME)){
            _LOGGERNAME = "SSLoggerFactory" + level;
            Logger log = LoggerFactory.getLogger(_LOGGERNAME);

        }

    }

    public Logger getLogger(){
        return LoggerFactory.getLogger(SSLoggerFactory.class + _LOGGERNAME);
    }
}
