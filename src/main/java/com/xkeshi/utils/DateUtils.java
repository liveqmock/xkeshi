package com.xkeshi.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The type Date utils.
 */
public class DateUtils {

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1123 format.
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1036 format.
     */
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in ANSI C 
     * <code>asctime()</code> format.
     */
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    /**
     * The constant PATTERN_CHINESE_NORMAL.
     */
    public static final String PATTERN_CHINESE_NORMAL = "yyyy-MM-dd HH:mm:ss";
    /**
     * The constant PATTERN_CHINESE_NOSEC.
     */
    public static final String PATTERN_CHINESE_NOSEC = "yyyy-MM-dd HH:mm";
    

    private static final Collection DEFAULT_PATTERNS = Arrays.asList(
            new String[] { PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123 } );
    
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime(); 
    }
    
//    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    /**
     * Parses a date value.  The formats used for parsing the date value are retrieved from
     * the default http params.
     *
     * @param dateValue the date value to parse
     * @return the parsed date
     * @throws DateParseException if the value could not be parsed using any of the
     * supported date formats
     */
    public static Date parseDate(String dateValue) throws Exception {
        return parseDate(dateValue, null, null);
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue the date value to parse
     * @param dateFormats the date formats to use
     * @return the parsed date
     * @throws DateParseException if none of the dataFormats could parse the dateValue
     */
    public static Date parseDate(String dateValue, Collection dateFormats) 
        throws Exception {
        return parseDate(dateValue, dateFormats, null);
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue the date value to parse
     * @param dateFormats the date formats to use
     * @param startDate During parsing, two digit years will be placed in the range
     * <code>startDate</code> to <code>startDate + 100 years</code>. This value may
     * be <code>null</code>. When <code>null</code> is given as a parameter, year
     * <code>2000</code> will be used.
     * @return the parsed date
     * @throws DateParseException if none of the dataFormats could parse the dateValue
     */
    public static Date parseDate(
        String dateValue, 
        Collection dateFormats,
        Date startDate 
    ) throws Exception {
        
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        // trim single quotes around date if present
        // see issue #5279
        if (dateValue.length() > 1 
            && dateValue.startsWith("'") 
            && dateValue.endsWith("'")
        ) {
            dateValue = dateValue.substring (1, dateValue.length() - 1);
        }
        
        SimpleDateFormat dateParser = null;        
        Iterator formatIter = dateFormats.iterator();
        
        while (formatIter.hasNext()) {
            String format = (String) formatIter.next();            
            if (dateParser == null) {
                dateParser = new SimpleDateFormat(format, Locale.US);
                dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateParser.set2DigitYearStart(startDate);
            } else {
                dateParser.applyPattern(format);                    
            }
            try {
                return dateParser.parse(dateValue);
            } catch (ParseException pe) {
                // ignore this exception, we will try the next format
            }                
        }
        
        // we were unable to parse the date
        throw new Exception("Unable to parse the date " + dateValue);        
    }

    /**
     * Formats the given date according to the RFC 1123 pattern.
     *
     * @param date The date to format.
     * @return An RFC 1123 formatted date string.
     * @see #PATTERN_RFC1123
     */
    public static String formatDate(Date date) {
        return formatDate(date, PATTERN_RFC1123);
    }

    /**
     * Formats the given date according to the specified pattern.  The pattern
     * must conform to that used by the {@link java.text.SimpleDateFormat simple date
     * format}* class.
     *
     * @param date The date to format.
     * @param pattern The pattern to use for formatting the date.
     * @return A formatted date string.
     * @throws IllegalArgumentException If the given date pattern is invalid.
     * @see
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) return "";
        if (pattern == null) return "";
        
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
//        formatter.setTimeZone(GMT);
        return formatter.format(date);
    }


    /**
     *
     * 计算日期的间隔天数
     * @param begin the begin
     * @param end the end
     * @return int
     */
    public static int getDatePeriod(Date begin,Date end){
    	if (begin == null || end == null) {
			return 0;
		}
    	DateTime beginDateTime = new DateTime(begin.getTime());
    	DateTime endDateTime = new DateTime(end.getTime());
    	Period p = new Period(beginDateTime, endDateTime, PeriodType.days());
    	return p.getDays();  
    }

    /**
     *
     * 计算日期的间隔天数
     * @param beginStr the begin str
     * @param endStr the end str
     * @return the int
     */
    public static int getDatePeriod(String beginStr,String endStr){
    	Date begin = Tools.setDate(beginStr);
    	Date end = Tools.setDate(endStr);
    	if (begin == null || end == null) {
    		return 0;
    	}
    	DateTime beginDateTime = new DateTime(begin.getTime());
    	DateTime endDateTime = new DateTime(end.getTime());
    	Period p = new Period(beginDateTime, endDateTime, PeriodType.days());
    	return p.getDays();  
    }


    /**
     * 获取间隔的自然月后的天数
     * @param begin 开始日期
     * @param intervalMonthNum 间隔月数
     * @return day num by interval month
     */
	public static int getDayNumByIntervalMonth(Date begin,int intervalMonthNum) {
		if (begin == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(begin);
		cal.add(Calendar.MONTH, intervalMonthNum);
		return getDatePeriod(begin , cal.getTime());
	}

    /**
     * 获取间隔天数后的日期
     * @param begin 开始日期
     * @param intervalDayNum 间隔天数
     * @return date by interval day
     */
	public static Date getDateByIntervalDay(Date begin , int intervalDayNum) {
		if (begin == null) {
			return null;
		}
		DateTime beginDateTime = new DateTime(begin.getTime());
		return beginDateTime.plusDays(intervalDayNum).toDate();
	}

    /**
     * 获取间隔天数后的日期
     * @param beginStr 开始日期
     * @param intervalDayNum 间隔天数
     * @return date by interval day
     */
	public static Date getDateByIntervalDay(String beginStr , int intervalDayNum) {
		Date begin = Tools.setDate(beginStr);
		if (begin == null) {
			return null;
		}
		DateTime beginDateTime = new DateTime(begin.getTime());
		return beginDateTime.plusDays(intervalDayNum).toDate();
	}

    /**
     * 获取间隔自然月后的日期
     * @param begin 开始日期
     * @param intervalMonthNum 间隔月数
     * @return date by interval month
     */
	public static Date getDateByIntervalMonth(Date begin , int intervalMonthNum) {
		if (begin == null) {
			return null;
		}
		DateTime beginDateTime = new DateTime(begin.getTime());
		return beginDateTime.plusMonths(intervalMonthNum).toDate();
	}

    /**
     * 获取两个时间的分钟差
     *
     * Get minutes between.
     *
     * @param beginDate the begin date
     * @param endDate the end date
     * @return the int
     */
    public static int getMinutesBetween(Date beginDate,Date endDate){
        if(null == beginDate || null == endDate){
            return 0;
        }
        return Minutes.minutesBetween(new DateTime(beginDate), new DateTime(endDate)).getMinutes();
    }

    /**
     * Get seconds between.
     *
     * @param beginDate the begin date
     * @param endDate the end date
     * @return the int
     */
    public static int getSecondsBetween(Date beginDate,Date endDate){
        if(null == beginDate || null == endDate){
            return 0;
        }
        return Seconds.secondsBetween(new DateTime(beginDate), new DateTime(endDate)).getSeconds();
    }


    /** This class should not be instantiated. */    
    private DateUtils() { }

    /**
     * 去除时间中的时分秒
     *
     * Format date without h hmmss.
     *
     * @param arrivalTime the arrival time
     * @return the string
     * @throws java.text.ParseException the parse exception
     */
    public static String formatDateWithoutHHmmss(String arrivalTime) {
        if(StringUtils.isEmpty(arrivalTime)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(arrivalTime);
        } catch (ParseException e) {
            return null;
        }
        return sdf.format(date);
    }

    public static String formatChineseTime(String date) {
        if(StringUtils.isEmpty(date)){
            return null;
        }
        String[] split = date.split("-");
        if(split!=null&&split.length==3){
            StringBuilder sb = new StringBuilder();
            sb.append(split[0]);
            sb.append("年");
            sb.append(split[1]);
            sb.append("月");
            sb.append(split[2]);
            sb.append("日");
            return sb.toString();
        }
        return null;
    }
}
