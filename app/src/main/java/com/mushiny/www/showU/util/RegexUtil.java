package com.mushiny.www.showU.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配工具类
 */
public class RegexUtil {

    public static final String PATTERN_URL = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+" +
            "([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";

    /**
     * 根据匹配规则过滤内容
     * @param content
     * @param pattern
     * @return
     */
    public static boolean match(String content, String pattern){

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(content);
        if (matcher.matches()){
            return  true;
        }

        return false;
    }

}
