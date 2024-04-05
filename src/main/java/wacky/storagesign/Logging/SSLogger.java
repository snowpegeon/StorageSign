package wacky.storagesign.Logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Objects;


public class SSLogger {
    private static String _LOGLEVELSTR;
    private static int _LOGLEVELWEIGHT;

    private static final String _LEVELFATAL = "SERVER";
    private static final String _LEVELWARN = "WARN";
    private static final String _LEVELERROR = "ERROR";
    private static final String _LEVELINFO = "INFO";
    private static final String _LEVELDEBUG = "DEBUG";
    private static final String _LEVELTRACE = "TRACE";
    private static final int _LEVELWEIGHTFATAL = 6;
    private static final int _LEVELWEIGHTERROR = 5;
    private static final int _LEVELWEIGHTWARN = 4;
    private static final int _LEVELWEIGHTINFO = 3;
    private static final int _LEVELWEIGHTDEBUG = 2;
    private static final int _LEVELWEIGHTTRACE = 1;

    private static final String _LOG_FORMAT = "【StorageSign】 %s";
    private static final String _LOG_FORMAT_LEVELSET = "【StorageSign】 [%s] %s";
    private final Logger _logger;
    private SimpleDateFormat _dateFormat;

    public SSLogger(String level){

        // 初期化メソッド内で呼び出し想定。初回のみ、レベルセットを実施
        if(Objects.isNull(_LOGLEVELSTR)){
            if(level.equals(_LEVELFATAL)){
                _LOGLEVELSTR = _LEVELFATAL;
                _LOGLEVELWEIGHT = _LEVELWEIGHTFATAL;
            } else if(level.equals(_LEVELERROR)){
                _LOGLEVELSTR = _LEVELERROR;
                _LOGLEVELWEIGHT = _LEVELWEIGHTERROR;
            } else if(level.equals(_LEVELWARN)){
                _LOGLEVELSTR = _LEVELWARN;
                _LOGLEVELWEIGHT = _LEVELWEIGHTWARN;
            } else if(level.equals(_LEVELDEBUG)){
                _LOGLEVELSTR = _LEVELDEBUG;
                _LOGLEVELWEIGHT = _LEVELWEIGHTDEBUG;
            } else if(level.equals(_LEVELTRACE)){
                _LOGLEVELSTR = _LEVELTRACE;
                _LOGLEVELWEIGHT = _LEVELWEIGHTTRACE;
            } else{
                _LOGLEVELSTR = _LEVELINFO;
                _LOGLEVELWEIGHT = _LEVELWEIGHTINFO;
            }
        }
        _logger = LogManager.getLogger(SSLogger.class);
    }

    public void fatal(String msg){
        if(_LEVELWEIGHTFATAL >= _LOGLEVELWEIGHT){
            _logger.fatal(_LOG_FORMAT.formatted( msg));
        }
    }

    public void fatal(String msg, Exception e){
        if(_LEVELWEIGHTFATAL >= _LOGLEVELWEIGHT){
            _logger.fatal(_LOG_FORMAT.formatted( msg), e);
        }
    }

    public void error(String msg){
        if(_LEVELWEIGHTERROR >= _LOGLEVELWEIGHT){
            _logger.error(_LOG_FORMAT.formatted( msg));
        }
    }

    public void error(String msg, Exception e){
        if(_LEVELWEIGHTERROR >= _LOGLEVELWEIGHT){
            _logger.error(_LOG_FORMAT.formatted( msg), e);
        }
    }

    public void warn(String msg){
        if(_LEVELWEIGHTWARN >= _LOGLEVELWEIGHT){
            _logger.warn(_LOG_FORMAT.formatted( msg));
        }
    }

    public void warn(String msg, Exception e){
        if(_LEVELWEIGHTWARN >= _LOGLEVELWEIGHT){
            _logger.warn(_LOG_FORMAT.formatted( msg), e);
        }
    }

    public void info(String msg){
        if(_LEVELWEIGHTINFO >= _LOGLEVELWEIGHT){
            _logger.info(_LOG_FORMAT.formatted( msg));
        }
    }

    public void info(String msg, Exception e){
        if(_LEVELWEIGHTINFO >= _LOGLEVELWEIGHT){
            _logger.info(_LOG_FORMAT.formatted( msg), e);
        }
    }

    public void debug(String msg){
        if(_LEVELWEIGHTDEBUG >= _LOGLEVELWEIGHT){
            // Debugレベルはマイクラで出してくれないので、INFOでだす
            _logger.info(_LOG_FORMAT_LEVELSET.formatted(_LEVELDEBUG, msg));
        }
    }

    public void debug(String msg, Exception e){
        if(_LEVELWEIGHTDEBUG >= _LOGLEVELWEIGHT){
            // Debugレベルはマイクラで出してくれないので、INFOでだす
            _logger.info(_LOG_FORMAT_LEVELSET.formatted(_LEVELDEBUG, msg), e);
        }
    }

    public void trace(String msg){
        if(_LEVELWEIGHTTRACE >= _LOGLEVELWEIGHT){
            // Traceレベルはマイクラで出してくれないので、INFOでだす
            _logger.info(_LOG_FORMAT_LEVELSET.formatted(_LEVELTRACE, msg));
        }
    }

    public void trace(String msg, Exception e){
        if(_LEVELWEIGHTTRACE >= _LOGLEVELWEIGHT){
            // Traceレベルはマイクラで出してくれないので、INFOでだす
            _logger.info(_LOG_FORMAT_LEVELSET.formatted(_LEVELTRACE, msg), e);
        }
    }
}
