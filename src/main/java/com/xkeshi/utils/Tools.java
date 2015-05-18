package com.xkeshi.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	private Tools() {
	}


    public static String getUUID(){
        java.util.UUID uuid  =  java.util.UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }


	private static int getYearDiff(Calendar cal, Calendar cal1)
			throws Exception {
		int m = (cal.get(cal.MONTH)+1) - (cal1.get(cal1.MONTH));
		int y = (cal.get(cal.YEAR)) - (cal1.get(cal1.YEAR));
		return (y * 12 + m) / 12;
	}


	public static Integer stringToInteger(String strDate) {
		try {
			return Integer.valueOf(strDate);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 通过身份证号获得性别
	 *
	 * @param identifyNo
	 * @return
	 */
	public static Integer getGenderValue(String identifyNo) {
		try {
			if (identifyNo != null) {
				if (Integer.parseInt(identifyNo.substring(16, 17)) % 2 == 0) {
					return 1;
				} else {
					return 2;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 通过身份证号获得年龄
	 *
	 * @param identifyNo
	 * @return
	 */
	public static Integer getAge(String identifyNo) {
		try {
			if (StringUtils.length(identifyNo) != 18) {
				return null;
			}
			Calendar cal1 = Calendar.getInstance();
			Calendar today = Calendar.getInstance();
			cal1.set(Integer.parseInt(identifyNo.substring(6, 10)),
					Integer.parseInt(identifyNo.substring(10, 12)),
					Integer.parseInt(identifyNo.substring(12, 14)));
			return getYearDiff(today, cal1);
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer getBoolean(String value) {
		switch (value) {
		case "是":
			return 1;
		case "否":
			return 2;
		default: // 其他
			return 0;
		}
	}

	public static Integer getInteger(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}


	/**
	 * 字符串转换为日期
	 * （支持格式：yyyy/MM/dd，  yyyy-MM-dd ， yyyy.MM.dd，  yyyy年MM月dd日）
	 */
	public static Date setDate(String strDate) {
		try {
			return DateUtils.parseDateStrictly(strDate, "yyyy/MM/dd",
					"yyyy-MM-dd", "yyyy.MM.dd", "yyyy年MM月dd日");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 字符串转换为BigDecimal
	 */
	public static BigDecimal getDecimal(String value) {
		try {
			BigDecimal bigDecimal = new BigDecimal(value);
			return bigDecimal;
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * 数字转换为BigDecimal
     */
    public static BigDecimal getDecimal(int value) {
        try {
            BigDecimal bigDecimal = new BigDecimal(value);
            return bigDecimal;
        } catch (Exception e) {
            return null;
        }
    }

	/**
	 * 如果大于等于1 则除以100后返回（用于数据导入）
	 *
	 * @param value
	 * @return
	 */
	public static BigDecimal getDecimalPercent(String value) {
		try {
			BigDecimal bigDecimal = new BigDecimal(value);
			if (bigDecimal.compareTo(new BigDecimal(1)) >= 0) {
				return bigDecimal.divide(new BigDecimal(100));
			}
			return bigDecimal;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将万元转换为元
	 */
	public static BigDecimal getDecimalTenThousandFromOne(String value) {
		try {
			BigDecimal bigDecimal = new BigDecimal(value);
			return bigDecimal.multiply(new BigDecimal(10000));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将元转换为万元，精度四舍五入到元
	 */
	public static BigDecimal getDecimalOneFromTenThousand(BigDecimal bigDecimal) {
		try {
			return bigDecimal.divide(new BigDecimal(10000),4,BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 转换为百分比值（如1.5%==>0.015 ）
	 */
	public static BigDecimal getDecimalProportion(String value) {
		try {
			BigDecimal bigDecimal = new BigDecimal(value);
			return bigDecimal.divide(new BigDecimal(100));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 数字字符串去零
	 */
	public static String trimZero(String str) {
		if (str.indexOf(".") != -1 && str.charAt(str.length() - 1) == '0') {
			return trimZero(str.substring(0, str.length() - 1));
		} else {
			return str.charAt(str.length() - 1) == '.' ? str.substring(0,
					str.length() - 1) : str;
		}
	}


	/**
	 * 数字字符串去零
	 */
	public static String trimZero(BigDecimal bigDecimal) {
		if (bigDecimal == null) {
            bigDecimal.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
			return "0";
		}
		String str = bigDecimal.toPlainString();
		if (str.indexOf(".") != -1 && str.charAt(str.length() - 1) == '0') {
			return trimZero(str.substring(0, str.length() - 1));
		} else {
			return str.charAt(str.length() - 1) == '.' ? str.substring(0,
					str.length() - 1) : str;
		}
	}

	/**
	 * 获取最大值
	 */
	public static Integer getMax(Integer ...numbers) {
		Integer max = numbers[0];
		for(int i=1;i<numbers.length;++i)
		{
		   if(max<numbers[i])
		   {
			   max = numbers[i];
		   }
		}
      return max;
    }

	public static int getMax(int ...numbers) {
		int max = numbers[0];
		for(int i=1;i<numbers.length;++i)
		{
		   if(max<numbers[i])
		   {
			   max = numbers[i];
		   }
		}
      return max;
    }


    public static BigDecimal getMin(String ...numbers) {
        BigDecimal min = null;
        for (int j = 0; j < numbers.length; j++) {
            BigDecimal number;
            try {
                number = new BigDecimal(numbers[j]);
            } catch (Exception e) {
                continue;
            }
            if (min == null || number.compareTo(min) == -1 ){
                min = number;
            }
        }
        return min;
    }


	public static boolean isMobileNo(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 将字符串进行MD5加密
	 * @param str
	 * @return
	 * @throws java.security.NoSuchAlgorithmException
	 */
	public static String md5(String str) throws NoSuchAlgorithmException {
	    MessageDigest digester = MessageDigest.getInstance("MD5");
	    digester.update(str.getBytes());
	    byte[] hash = digester.digest();
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	        if ((0xff & hash[i]) < 0x10) {
	            hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
	        } else {
	            hexString.append(Integer.toHexString(0xFF & hash[i]));
	        }
	    }
	    return hexString.toString();
	}
	
	/**
	 * 判断是否是电信的号码
	 * @param mobile
	 * @return
	 */
	public boolean isTelecom(String mobile) {
		Pattern p = Pattern.compile("^(133|153|1349|180|181|189)\\d+$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

    /**
     * 分转元
     *
     * Get yuan string.
     *
     * @param decimal the decimal
     * @return the string
     */
    public static String getYuanString(BigDecimal decimal){
        if(null==decimal){
            return null;
        }
        return trimZero(decimal.divide(BigDecimal.valueOf(100).setScale(2,BigDecimal.ROUND_HALF_UP)).toPlainString());
    }

    /**
     * 分转元
     *
     * Get yuan string.
     *
     * @param value the decimal
     * @return the string
     */
    public static BigDecimal getYuanDecimal(String value){
        if(StringUtils.isEmpty(value)){
            return null;
        }
        BigDecimal decimal = getDecimal(value);
        String val = trimZero(decimal.divide(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP)).toPlainString());
        return getDecimal(val);
    }

    /**
     * 元转分
     *
     * Get cent string.
     *
     * @param cent the cent
     * @return the string
     */
    public static String getCentString(String cent){
        if(StringUtils.isEmpty(cent)){
            return null;
        }
        BigDecimal centVal = getDecimal(cent);
        return centVal.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    public static String getLastString(String str,int num){
        if(StringUtils.isEmpty(str)||str.length()<num){
            return "";
        }
        return str.substring(str.length()-num,str.length());
    }

}
