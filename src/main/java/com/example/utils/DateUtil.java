package com.example.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 时间转换帮助类
 * 
 * @ClassName: DateUtil
 * @Description: TODO
 * @author jiangsonggui
 * @date 2015年11月23日 上午10:51:28
 *
 */
public class DateUtil {

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 获取日期中的某数值。如获取月份
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            日期格式
	 * @return 数值
	 */
	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期字符串
	 */
	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	/**
	 * 获取精确的日期
	 * 
	 * @param timestamps
	 *            时间long集合
	 * @return 日期
	 */
	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0;
		Map<Long, long[]> map = new HashMap<Long, long[]>();
		List<Long> absoluteValues = new ArrayList<Long>();

		if (timestamps != null && timestamps.size() > 0) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
						absoluteValues.add(absoluteValue);
						long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };
						map.put(absoluteValue, timestampTmp);
					}
				}

				// 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
				// 因此不能将minAbsoluteValue取默认值0
				long minAbsoluteValue = -1;
				if (!absoluteValues.isEmpty()) {
					minAbsoluteValue = absoluteValues.get(0);
					for (int i = 1; i < absoluteValues.size(); i++) {
						if (minAbsoluteValue > absoluteValues.get(i)) {
							minAbsoluteValue = absoluteValues.get(i);
						}
					}
				}

				if (minAbsoluteValue != -1) {
					long[] timestampsLastTmp = map.get(minAbsoluteValue);

					long dateOne = timestampsLastTmp[0];
					long dateTwo = timestampsLastTmp[1];
					if (absoluteValues.size() > 1) {
						timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;
					}
				}
			} else {
				timestamp = timestamps.get(0);
			}
		}

		if (timestamp != 0) {
			date = new Date(timestamp);
		}
		return date;
	}

	/**
	 * 判断字符串是否为日期字符串
	 * 
	 * @param date
	 *            日期字符串
	 * @return true or false
	 */
	public static boolean isDate(String date) {
		boolean isDate = false;
		if (date != null) {
			if (getDateStyle(date) != null) {
				isDate = true;
			}
		}
		return isDate;
	}

	/**
	 * 获取日期字符串的日期风格。失敗返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期风格
	 */
	public static DateStyle getDateStyle(String date) {
		DateStyle dateStyle = null;
		Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
		List<Long> timestamps = new ArrayList<Long>();
		for (DateStyle style : DateStyle.values()) {
			if (style.isShowOnly()) {
				continue;
			}
			Date dateTmp = null;
			if (date != null) {
				try {
					ParsePosition pos = new ParsePosition(0);
					dateTmp = getDateFormat(style.getValue()).parse(date, pos);
					if (pos.getIndex() != date.length()) {
						dateTmp = null;
					}
				} catch (Exception e) {
				}
			}
			if (dateTmp != null) {
				timestamps.add(dateTmp.getTime());
				map.put(dateTmp.getTime(), style);
			}
		}
		Date accurateDate = getAccurateDate(timestamps);
		if (accurateDate != null) {
			dateStyle = map.get(accurateDate.getTime());
		}
		return dateStyle;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static Date StringToDate(String date) {
		DateStyle dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return 日期
	 */
	public static Date StringToDate(String date, String pattern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateStyle
	 *            日期风格
	 * @return 日期
	 */
	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle != null) {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, String pattern) {
		String dateString = null;
		if (date != null) {
			try {
				dateString = getDateFormat(pattern).format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dateString;
	}

	public static String DateToString(Date date) {
		String dateString = null;
		if (date != null) {
			try {
				dateString = getDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			} catch (Exception e) {
			}
		}
		return dateString;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dateStyle
	 *            日期风格
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, DateStyle dateStyle) {
		String dateString = null;
		if (dateStyle != null) {
			dateString = DateToString(date, dateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String newPattern) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle newDateStyle) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newDateStyle);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, String newPattern) {
		return DateToString(StringToDate(date, olddPattern), newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newParttern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, String newParttern) {
		String dateString = null;
		if (olddDteStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newParttern);
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, DateStyle newDateStyle) {
		String dateString = null;
		if (newDateStyle != null) {
			dateString = StringToString(date, olddPattern, newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
		String dateString = null;
		if (olddDteStyle != null && newDateStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期字符串
	 */
	public static String addYear(String date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期
	 */
	public static Date addYear(Date date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期字符串
	 */
	public static String addMonth(String date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期
	 */
	public static Date addMonth(Date date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期字符串
	 */
	public static String addDay(String date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param hourAmount
	 *            增加数量。可为负数
	 * @return 增加小时后的日期字符串
	 */
	public static String addHour(String date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param hourAmount
	 *            增加数量。可为负数
	 * @return 增加小时后的日期
	 */
	public static Date addHour(Date date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param minuteAmount
	 *            增加数量。可为负数
	 * @return 增加分钟后的日期字符串
	 */
	public static String addMinute(String date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加分钟后的日期
	 */
	public static Date addMinute(Date date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加秒钟后的日期字符串
	 */
	public static String addSecond(String date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加秒钟后的日期
	 */
	public static Date addSecond(Date date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 年份
	 */
	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 年份
	 */
	public static int getYear(Date date) {
		return getInteger(date, Calendar.YEAR);
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 月份
	 */
	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 月份
	 */
	public static int getMonth(Date date) {
		return getInteger(date, Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 天
	 */
	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 天
	 */
	public static int getDay(Date date) {
		return getInteger(date, Calendar.DATE);
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 小时
	 */
	public static int getHour(String date) {
		return getHour(StringToDate(date));
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 小时
	 */
	public static int getHour(Date date) {
		return getInteger(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 分钟
	 */
	public static int getMinute(String date) {
		return getMinute(StringToDate(date));
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 分钟
	 */
	public static int getMinute(Date date) {
		return getInteger(date, Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 秒钟
	 */
	public static int getSecond(String date) {
		return getSecond(StringToDate(date));
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 秒钟
	 */
	public static int getSecond(Date date) {
		return getInteger(date, Calendar.SECOND);
	}

	/**
	 * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static String getDate(String date) {
		return StringToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 日期
	 */
	public static String getDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 时间
	 */
	public static String getTime(String date) {
		return StringToString(date, DateStyle.HH_MM_SS);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 时间
	 */
	public static String getTime(Date date) {
		return DateToString(date, DateStyle.HH_MM_SS);
	}

	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 星期
	 */
	public static Week getWeek(String date) {
		Week week = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			week = getWeek(myDate);
		}
		return week;
	}

	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 星期
	 */
	public static Week getWeek(Date date) {
		Week week = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (weekNumber) {
		case 0:
			week = Week.SUNDAY;
			break;
		case 1:
			week = Week.MONDAY;
			break;
		case 2:
			week = Week.TUESDAY;
			break;
		case 3:
			week = Week.WEDNESDAY;
			break;
		case 4:
			week = Week.THURSDAY;
			break;
		case 5:
			week = Week.FRIDAY;
			break;
		case 6:
			week = Week.SATURDAY;
			break;
		}
		return week;
	}

	/**
	 * 获取两个日期相差的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param otherDate
	 *            另一个日期字符串
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	/**
	 * @param date
	 *            日期
	 * @param otherDate
	 *            另一个日期
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = DateUtil.StringToDate(DateUtil.getDate(date), DateStyle.YYYY_MM_DD);
		Date otherDateTmp = DateUtil.StringToDate(DateUtil.getDate(otherDate), DateStyle.YYYY_MM_DD);
		if (dateTmp != null && otherDateTmp != null) {
			long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
			num = (int) (time / (24 * 60 * 60 * 1000));
		}
		return num;
	}
	
   /**
    * 获取某月天数
    * @author ZhuYuanDong
    * @since 2017年12月2日 上午11:59:57
    * @param date
    * @return
    * Modified XXX ZhuYuanDong 2017年12月2日
    */
   public static int getMonthCountDay(Date date){
	   Calendar a = Calendar.getInstance();  
	   a.setTime(date); 
	   a.set(Calendar.DATE, 1);//把日期设置为当月第一天  
	   a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天  
	   a.get(Calendar.DATE);
	   return  a.get(Calendar.DATE);
   }
	 
	
	/**
	 * 当天设置时间<br/>
	 * today+09:00
	 * 
	 * @param time
	 */
	public static Date setTodayTime(String time) {
		String todayDate = DateToString(new Date(), DateStyle.YYYY_MM_DD);
		todayDate += " " + time;
		return StringToDate(todayDate);
	}

	public enum DateStyle {

		YYYY_MM("yyyy-MM", false), YYYY_MM_DD("yyyy-MM-dd", false), YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm",
				false), YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss", false),

		YYYY_MM_EN("yyyy/MM", false), YYYY_MM_DD_EN("yyyy/MM/dd", false), YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm",
				false), YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss", false),

		YYYY_MM_CN("yyyy年MM月", false), YYYY_MM_DD_CN("yyyy年MM月dd日", false), YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm",
				false), YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss", false),

		HH_MM("HH:mm", true), HH_MM_SS("HH:mm:ss", true),

		MM_DD("MM-dd", true), MM_DD_HH_MM("MM-dd HH:mm", true), MM_DD_HH_MM_SS("MM-dd HH:mm:ss", true),

		MM_DD_EN("MM/dd", true), MM_DD_HH_MM_EN("MM/dd HH:mm", true), MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss", true),

		MM_DD_CN("MM月dd日", true), MM_DD_HH_MM_CN("MM月dd日 HH:mm", true), MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss", true);

		private String value;

		private boolean isShowOnly;

		DateStyle(String value, boolean isShowOnly) {
			this.value = value;
			this.isShowOnly = isShowOnly;
		}

		public String getValue() {
			return value;
		}

		public boolean isShowOnly() {
			return isShowOnly;
		}
	}

	public enum Week {

		MONDAY("星期一", "Monday", "Mon.", 1), TUESDAY("星期二", "Tuesday", "Tues.", 2), WEDNESDAY("星期三", "Wednesday", "Wed.",
				3), THURSDAY("星期四", "Thursday", "Thur.", 4), FRIDAY("星期五", "Friday", "Fri.", 5), SATURDAY("星期六",
						"Saturday", "Sat.", 6), SUNDAY("星期日", "Sunday", "Sun.", 7);

		String name_cn;
		String name_en;
		String name_enShort;
		int number;

		Week(String name_cn, String name_en, String name_enShort, int number) {
			this.name_cn = name_cn;
			this.name_en = name_en;
			this.name_enShort = name_enShort;
			this.number = number;
		}

		public String getChineseName() {
			return name_cn;
		}

		public String getName() {
			return name_en;
		}

		public String getShortName() {
			return name_enShort;
		}

		public int getNumber() {
			return number;
		}
	}

	/**
	 * 获取2个时间相差多少条 张卓
	 * 
	 * @param destTime
	 * @return
	 */
	public static String getSubTime(String destTime) {
		Date nowDate = new Date();
		Date inDate = DateUtil.StringToDate(destTime);
		
		double dateSub = (nowDate.getTime() - inDate.getTime())/(1000*60);
		if (dateSub < 24 * 60D) {
			if (destTime.indexOf("00:00:00") >= 0 && DateUtil.getMonth(inDate) == DateUtil.getMonth(nowDate)) {
				return "当天";
			} else if (destTime.indexOf("00:00:00") >= 0) {
				return "昨天";
			} else {
				if (dateSub < 60D) {
					return (int) dateSub + "分钟前";
				} else {
					return (int) dateSub / 60 + "小时前";
				}
			}
		} else if (dateSub < 30 * 24 * 60D) {
			return (int) (dateSub / 60 / 24) + "天前";
		} else if (dateSub < 6 * 30 * 24 * 60D) {
			return (int) (dateSub / 60 / 24 / 30) + "个月前";
		} else if (dateSub < 12 * 30 * 24 * 60D) {
			return "半年前";
		} else {
			return (int) (dateSub / 60 / 24 / 30 / 12) + "年前";
		}
	}
	
	/**
	 * 获取时间间隔
	 * @return
	 */
	public static String[] getDtateTimes(String dateStr){
		if(dateStr == null || dateStr.equals("")){
			return new String[]{};
		}
		String date = DateToString(new Date(), "yyyy-MM-dd");
		if("0".equals(dateStr)){
			String now = addDay(date, -7);
			return new String[]{now+" 00:00:00", date+" 23:59:59"};
		}else if("1".equals(dateStr)){
			String now = addDay(date, -30);
			return new String[]{now+" 00:00:00", date+" 23:59:59"};
		}else if("2".equals(dateStr)){
			String now = addDay(date, -90);
			return new String[]{now+" 00:00:00", date+" 23:59:59"};
		}else if("3".equals(dateStr)){
			String now = addDay(date, -180);
			return new String[]{now+" 00:00:00", date+" 23:59:59"};
		}else if("4".equals(dateStr)){
			String now = addDay(date, -365);
			return new String[]{now+" 00:00:00", date+" 23:59:59"};
		}
		return new String[]{};
	}

	/**
	 * 返回两个日期之间的毫秒数的差距
	 * @param d1
	 * @param d2
	 * @return 二者至1970年1.1后的毫秒数的差值
	 */
	public static long getMillisecondsOfTwoDate(Date d1,Date d2){
		if(d1==null || d2==null){
			throw new IllegalArgumentException("参数d1或d2不能是null对象!");
		}
		long dI1 = d1.getTime();
		long dI2 = d2.getTime();
		return (dI1-dI2);
	}

	/**
	 * 获得两个日期之间相差的秒数
	 * @param d1
	 * @param d2
	 * @return 两日期之间相差的秒数
	 */
	public static Double getSecondsOfTwoDate(Date d1,Date d2){
		if(d1==null || d2==null){
			throw new IllegalArgumentException("参数d1或d2不能是null对象!");
		}
		long i = getMillisecondsOfTwoDate(d1,d2);

		return (double)i/1000;
	}

	/**
	 * 获得两个日期之间相差的分钟数
	 * @param d1
	 * @param d2
	 * @return 两日期之间相差的分钟数
	 */
	public static double getMinutesOfTwoDate(Date d1,Date d2){
		if(d1==null || d2==null){
			throw new IllegalArgumentException("参数d1或d2不能是null对象!");
		}
		long millions = getMillisecondsOfTwoDate(d1,d2);
		return (double)millions/60/1000;
	}

	/**
	 * 获得两个日期之间相差的小时数
	 * @param d1
	 * @param d2
	 * @return 两日期之间相差的小时数
	 */
	public static double getHoursOfTwoDate(Date d1,Date d2){
		if(d1==null || d2==null){
			throw new IllegalArgumentException("参数d1或d2不能是null对象!");
		}
		long millions = getMillisecondsOfTwoDate(d1,d2);
		return (double)millions/60/60/1000;
	}

	/**
	 * 获得两个日期之间相差的天数
	 * @param d1
	 * @param d2
	 * @return 
	 */
	public static double getDaysOfTwoDate(Date d1,Date d2){
		if(d1==null || d2==null){
			throw new IllegalArgumentException("参数d1或d2不能是null对象!");
		}
		long millions = getMillisecondsOfTwoDate(d1,d2);
		return (double)millions/24/60/60/1000;
	}
	
	/**
	 * 获取两个时间相差月数
	 * @author HeWei
	 * @since 2018年12月30日 下午1:40:19
	 * @param d1
	 * @param d2
	 * @return
	 * Modified XXX HeWei 2018年12月30日
	 */
	public static int getMonthDiff(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		int year1 = c1.get(Calendar.YEAR);
		int year2 = c2.get(Calendar.YEAR);
		int month1 = c1.get(Calendar.MONTH);
		int month2 = c2.get(Calendar.MONTH);
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH);
		// 获取年的差值 
		int yearInterval = year1 - year2;
		// 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
		if (month1 < month2 || month1 == month2 && day1 < day2)
		yearInterval--;
		// 获取月数差值
		int monthInterval = (month1 + 12) - month2;
		if (day1 < day2)
		monthInterval--;
		monthInterval %= 12;
		int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
		return monthsDiff;
	}
	
	/**
	 * 时间比较
	 * @return Integer -5:参数空 -1 d1小于d2 0:相等 1:d1大于d2
	 * **/
	public static Integer dateCompare(String dateStr1, String dateStr2, String pattern) {
		Integer res = -5;
		if(StringUtil.isNotBlank(dateStr1, dateStr2)) {
			Date d1 = StringToDate(dateStr1, pattern);
			Date d2 = StringToDate(dateStr2, pattern);
			return dateCompare(d1, d2);
		}
		return res;
	}
	/**
	 * 比较两个日期大小
	 * @param dateStr1
	 * @param dateStr2
	 * @return -1:d1小于d2 		0:相等	 	1:d1大于d2
	 */
	public static Integer dateCompare(Date dateStr1, Date dateStr2) {
		Integer res = -5;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStr1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(dateStr2);
		
		if(calendar.before(calendar2)) {
			res = -1;
		}else if(calendar.after(calendar2)) {
			res = 1;
		}else{
			res = 0;
		}
		return res;
	}
	
	/**
	 * 获取当月第一天
	 * @return
	 */
	public static Date getMonthFirstDate(){
		Calendar calendar = Calendar.getInstance();
		calendar = Calendar.getInstance();  
		calendar.add(Calendar.MONTH, 0);  
		calendar.set(Calendar.DAY_OF_MONTH, 1);  
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
        return calendar.getTime();  
	}
	

	/**
	 * 获取当月最后一天
	 * @return
	 */
	public static Date getMonthLastDate(){
		Calendar calendar = Calendar.getInstance();
		calendar = Calendar.getInstance();  
		calendar.add(Calendar.MONTH, 1);  
		calendar.set(Calendar.DAY_OF_MONTH, 0);  
        return calendar.getTime();  
	}
	
	
	/**
	 * 获取某一个月的第一天
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String endTime = sdf.format(calendar.getTime());
		StringToDate(endTime, "yyyy-MM-dd");
		return StringToDate(endTime, "yyyy-MM-dd");
	}
	
	/**
	 * 获取本周一
	 * @author XIONG CAI
	 * @since 2019年5月21日 下午3:01:12
	 * @param date
	 * @return
	 * Modified XXX XIONG CAI 2019年5月21日
	 */
	public static Date getThisWeekMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		return cal.getTime();
	}
	
	/**
	 * 获取下周一
	 * @author XIONG CAI
	 * @since 2019年5月21日 下午3:01:47
	 * @param date
	 * @return
	 * Modified XXX XIONG CAI 2019年5月21日
	 */
	public static Date getNextWeekMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getThisWeekMonday(date));
		cal.add(Calendar.DATE, 7);
		return cal.getTime();
	}
	
	/**
	 * 获取本周日
	 * @author XIONG CAI
	 * @since 2019年5月21日 下午3:02:33
	 * @param date
	 * @return
	 * Modified XXX XIONG CAI 2019年5月21日
	 */
	public static Date getThisWeekSunday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getThisWeekMonday(date));
		cal.add(Calendar.DATE, 6);
		return cal.getTime();
	}
	
	/**
	 * 获取某个时间是当月的第几周
	 * @author XIONG CAI
	 * @since 2019年6月5日 上午9:38:26
	 * @param date
	 * @return
	 * Modified XXX XIONG CAI 2019年6月5日
	 */
	public static int getWeekOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_MONTH);
	}

	public static void main(String[] args) {
		Date date =DateUtil.StringToDate("2019-4-29");
		int a = DateUtil.getWeekOfMonth(date);
		System.out.println(a);
		
		Date date1 =DateUtil.StringToDate("2019-05-8");
		int a1 = DateUtil.getWeekOfMonth(date1);
		System.out.println(a1);
	}
	
	/**
	 * 获取某一个月的最后一天
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}
	
	/**
	 * 获取某一天的最后一秒  23:59:59
	 * @param date
	 * @return
	 */
	public static Date getLastSecondOfDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DATE, 1);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}
	
	/**
	 * 获取某一天的第一秒 00:00:00
	 * @param date
	 * @return
	 */
	public static Date getFirstSecondOfDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	 public static String getTime(int count) {
		  int days = 7;
		  if(count==1){
			  days = 30;
		  }else if(count>=2){
			  return "2099-01-01 00:00:00";
		  }
		  Calendar day=Calendar.getInstance();
		  day.add(Calendar.DATE,days);
		  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		  String endTime = sdf.format(day.getTime());
		  return endTime;
	}
	 
	 /**
	  * 获取某年某月的最后一天
	  * @param year
	  * @param month
	  * @return
	  */
	 public static String getLastDayOfMonth(String yearStr,String monthStr)
		{
		 	int year = StringUtil.parseInteger(yearStr);
		 	int month = StringUtil.parseInteger(monthStr);
			Calendar cal = Calendar.getInstance();
			//设置年份
			cal.set(Calendar.YEAR,year);
			//设置月份
			cal.set(Calendar.MONTH, month-1);
			//获取某月最大天数
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			//设置日历中月份的最大天数
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
			//格式化日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String lastDayOfMonth = sdf.format(cal.getTime());
			return lastDayOfMonth;
		}
	 
	 /**
	  * 获取本月的第一天和最后一天
	  * @return
	  */
	public static String[] getFirstAndLastDay(){
		try{
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
//			//获取当前月第一天：
	        Calendar c = Calendar.getInstance();    
	        //Date time = c.getTime();
	        c.add(Calendar.SECOND, 1);
	        //Date time2 = c.getTime();
	        c.add(Calendar.MONTH, 0);
	        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
	        String first = format.format(c.getTime());
	        
	        //获取当前月最后一天
	        Calendar ca = Calendar.getInstance();    
	        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
	        String last = format.format(ca.getTime());
	        return new String[]{first, last};
		}catch(Exception e){
			 return new String[]{"", ""};
		}
		
	}
	
	/**张卓
     * 将10 or 13 位时间戳转为时间字符串
     * convert the number 1407449951 1407499055617 to date/time format timestamp
     */
    public static String timestamp2Date(String str_num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (str_num.length() == 13) {
            String date = sdf.format(new Date(toLong(str_num)));
            return date;
        } else {
            String date = sdf.format(new Date(toInt(str_num) * 1000L));
            return date;
        }
    }
	 
    /**
     * String转long
     * @param obj
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }
    
    /**
     * 对象转整
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return StringUtil.parseInteger(obj.toString());
    }  
    public static List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
        List<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
          result.add(sdf.format(curr.getTime()));
          curr.add(Calendar.MONTH, 1);
        }
        return result;
      }

	/**
	 * 时间段格式化
	 * @param stringDate
	 * @return lcb
	 * @throws Exception
	 */
      public static Date[] formateStringDate(String stringDate) {
    	  try {
			  Date[] date= new Date[]{null,null};
			  if(StringUtils.isNotBlank(stringDate) && stringDate.contains(" - ")){
				  String[] time = stringDate.split(" - ");
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  /***起始时间***/
				  Date date1 = sdf.parse(time[0]+" 00:00:00" );
				  /***结束时间***/
				  Date date2 = sdf.parse(time[1]+" 23:59:59"); 
				  date = new Date[] {date1, date2};
			  }
			  return date;
    	  } catch (Exception e) {
				throw new RuntimeException();
			}
	  }
      
      /**
  	 * 时间段格式化
  	 * @param stringDate
  	 * @return lcb
  	 * @throws Exception
  	 */
        public static Date[] formateStringDateNew(String stringDate){
	        try {
	        	 Date[] date= new Date[]{null,null};
	     		  if(StringUtils.isNotBlank(stringDate) && stringDate.contains(" - ")){
	     			  String[] time = stringDate.split(" - ");
	     			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	     			  /***起始时间***/
	     			  Date date1 = sdf.parse(time[0]);
	     			  /***结束时间***/
	     			  Date date2 = sdf.parse(time[1]); 
	     			  date = new Date[] {date1, date2};
	     		  }
	     		  return date;
			} catch (Exception e) {
				throw new RuntimeException();
			}
  		 
  	  }
        
	  /**
	   *
	   * 查询一个月指定周几列表
	   * lcb
	   * ***/
	  public static List<Date> getSpeciftDateList(Integer month, Integer speciftDay) {
		 List<Date> resList = new ArrayList<>();
		  Calendar cal = Calendar.getInstance();
		  cal.set(Calendar.MONTH, month - 1);
		  cal.set(Calendar.DAY_OF_MONTH, 1); // 设为第一天

		  while(cal.get(Calendar.MONTH) < month ) {

			  if(cal.get(Calendar.DAY_OF_WEEK) == speciftDay) {
				  resList.add(cal.getTime());
			  }
			  cal.add(Calendar.DAY_OF_MONTH, 1);
		  }
		  return resList;
	  }
	  
	  /**
     * 把类似于yyyy-MM 的时间字符串转换为 map<start,yyyy-MM-01 00:00:00><end,yyyy-MM-28 | 29 | 30 | 31 23:59:59>
     * @param timeString
     * @return
     * @throws ParseException
     */
    public static Map<String, Date> processTime(String timeString){
        String year = timeString.split("-")[0];
        Integer yearI = StringUtil.parseInteger(year, 2016);
        if (yearI > 2099 || yearI < 2016) {
            throw new RuntimeException("时间选择错误");
        }else {
            String endDate = "";
            String startDate = timeString+"-01 00:00:00";
            String mouth = timeString.split("-")[1];
            if (mouth.equals("01") || mouth.equals("03") || mouth.equals("05") || mouth.equals("07") || mouth.equals("08") || mouth.equals("10") || mouth.equals("12")) {
                 endDate = timeString+"-31 23:59:59";
            }else if (mouth.equals("04") || mouth.equals("06") || mouth.equals("09") || mouth.equals("11")) {
                 endDate = timeString+"-30 23:59:59";
            }else if (mouth.equals("02")) {
                if (yearI % 4 == 0 && yearI % 100 != 0 || yearI % 400 == 0) {
                     endDate = timeString+"-29 23:59:59";
                }else {
                     endDate = timeString+"-28 23:59:59";
                }
            }else {
                 endDate = timeString+"-28 23:59:59";   
            }
            Date start = DateUtil.StringToDate(startDate, "yyyy-MM-dd HH:mm:ss");
            Date end =  DateUtil.StringToDate(endDate, "yyyy-MM-dd HH:mm:ss");
            Map<String, Date> map = new HashMap<>();
            map.put("start", start);
            map.put("end", end);
            return map;
        }
    }
    
    /**
     * 将时间转换为UNIX时间戳（13位）
     * @author XIONG CAI
     * @since 2019年2月22日 下午6:06:00
     * @param date
     * @return
     * Modified XXX XIONG CAI 2019年2月22日
     */
	public static Number dateToUnixTime(Date date){
	    long epoch = 0;
		try {
			String dateTime = DateToString(date, "yyyy-MM-dd HH:mm:ss");
			DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			epoch = df.parse(dateTime).getTime();
		} catch (ParseException e) {
			throw new RuntimeException();
		}
		return epoch;
	}
}