package jnshu.taskeight.tag;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import javax.servlet.jsp.JspException;import javax.servlet.jsp.tagext.TagSupport;import java.io.IOException;import java.text.SimpleDateFormat;import java.util.Calendar;/** * 该类配合自定义Tag标签date对时间进行转换 * * @author Administrator * @date 10/8/2017 */public class DateTag extends TagSupport {    private static Logger loggerDateTage= LoggerFactory.getLogger(DateTag.class);   // private static final long serialVersionUID = 6464168398214506236L;    private String value;    private String name;    public void setName(String name) {        this.name = name;    }    public void setValue(String value) {        this.value = value;    }    @Override    public int doStartTag() throws JspException {        if(value != null){        }        //获取时间戳        String vv = "" + value;        /*把String的时间戳换为long型 标签获取到的属性都为String*/        long time = Long.valueOf(vv);        Calendar c = Calendar.getInstance();        //把时间戳换位毫秒级        c.setTimeInMillis(time);        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        //把毫秒级时间戳换为指定格式的时间        String s = dateformat.format(c.getTime());        //当name为空时不输出        if (name != null) {            s = "\"" + name + "\": " + s;        }        /* 在value输出s */        try {            pageContext.getOut().write(s);        } catch (IOException e) {            e.printStackTrace();            loggerDateTage.error("e.getMessage() " + e.getMessage());        }        return super.doStartTag();    }}