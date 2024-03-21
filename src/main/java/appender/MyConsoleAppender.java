/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appender;

//import IO_LIB.SQL_OPE;
import SocketTest.SQL_OPE;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 *
 * @author keita
 */
@Plugin(
  name = "MyConsoleAppender", 
  category = Core.CATEGORY_NAME, 
  elementType = Appender.ELEMENT_TYPE)
public class MyConsoleAppender extends AbstractAppender{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
//    private final SQL_OPE sql;
    
//    H2consoleのappenderは以下の様にするっぽいが後ほど解析します。
//    https://howtodoinjava.com/log4j2/jdbcappender-example/

    protected MyConsoleAppender(String name, Filter filter) {
        super(name, filter, null);
        
//        sql = new SQL_OPE("jdbc:h2:tcp://localhost/./serverlog","junapp","");
//        sql.executeUpdate("create table if not exists log("
//                + "id INT auto_increment PRIMARY KEY,"
//                + "logtime timestamp,"
//                + "level varchar,"
//                + "msg varchar "
//                + ")"
//        );
    }
    
    @PluginFactory
    public static MyConsoleAppender createAppender(
      @PluginAttribute("name") String name, 
      @PluginElement("Filter") Filter filter) {
        return new MyConsoleAppender(name, filter);
    }
    
    @Override
    public void append(LogEvent le) {
        DateFormat obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // we create instance of the Date and pass milliseconds to the constructor
        Date res = new Date(le.getTimeMillis());
        System.out.format("%-80s ", le.getMessage().getFormattedMessage());
        System.out.println(le.getLevel().toString() 
                + "  " + le.getLoggerName() + " line:" 
                + le.getSource().getLineNumber());
//        sql.executeUpdate("insert into log(logtime,level,msg)values("
//                + "'"+ obj.format(res)+ "',"
//                + "'"+ le.getLevel().toString() + "',"
//                + "'"+ le.getMessage().getFormattedMessage() + "' "
//                + ")");
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
