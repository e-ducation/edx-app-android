package org.edx.mobile.eliteu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidationUtil {

    /**
     * 检测邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        Pattern p = Pattern
                .compile("^[a-zA-Z0-9]([a-zA-Z0-9_.-]*)@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检测手机号是否合法
     *
     * @param phone
     * @return
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null)
            return false;
        Pattern p = Pattern
                .compile("^((\\+?[0-9]{1,4})|(\\(\\+86\\)))?(13[0-9]|14[56789]|15[0-9]|16[56]|17[0-9]|18[0-9]|19[89])\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

}
