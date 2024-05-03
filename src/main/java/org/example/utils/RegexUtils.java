package org.example.utils;


import cn.hutool.core.util.StrUtil;

public class RegexUtils {

    /**
     * Whether the phone format is invalid
     * @param phone
     * @return
     */
    public  static boolean isPhoneInvalid(String phone) {
     return    mismatch(phone, RegexPatterns.PHONE_REGEX);
    }

    /**
     * Whether the email format is invalid
     * @param email
     * @return
     */
    public  static boolean isEmailInvalid(String email) {
      return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }

    /**
     * Whether the code(验证码) format is invalid
     * @param code
     * @return
     */

    public  static boolean isCodeInvalid(String code) {
     return    mismatch(code, RegexPatterns.VERIFY_CODE_REGEX);
    }


    /**
     *  Whether the Regex format is invalid
     * @param phone
     * @param regex
     * @return
     */
    private static boolean mismatch(String phone, String regex) {
        if(StrUtil.isBlank(phone)){
            return  true;
        }

        return !phone.matches(regex);
    }





}
