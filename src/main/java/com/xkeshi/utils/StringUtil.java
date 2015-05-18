package com.xkeshi.utils;

/**
 * @author yc
 * @date 2015/4/14
 * @description
 */
public final class StringUtil {

    private StringUtil(){}

    /**
     *  Whether words are Chinese character
     * @param text
     * @return
     */
    public static boolean isChinese(String text){
        if(text == null || text.length() == 0){
            throw new NullPointerException("text == null");
        }

        char[] chars = text.toCharArray();
        boolean isChinese = false;

        for(char c : chars ){
            if(!(isChinese= isChinese(c)))
                break;
        }
        return isChinese;
    }

    /**
     * whether character is chinese
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

}
