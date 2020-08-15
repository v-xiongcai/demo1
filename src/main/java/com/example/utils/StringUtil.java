package com.example.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作类
 * 
 * @ClassName: StringUtil
 * @Description: TODO
 * @author jiangsonggui
 * @date 2015年11月23日 上午11:09:14
 *
 */
public final class StringUtil {
	private static char chars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private static int char_length = chars.length;

	private StringUtil() {
		throw new AssertionError();
	}

	/**
	 * <p>
	 * isEmpty(null) true<br/>
	 * isEmpty("") true<br/>
	 * isEmpty("null") false<br/>
	 * isEmpty(" ") false<br/>
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return null == str || str.length() == 0;
	}

	/**
	 * <p>
	 * isEmpty(null) false<br/>
	 * isEmpty("") false<br/>
	 * isEmpty("null") true<br/>
	 * isEmpty(" ") true<br/>
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * <p>
	 * isEmpty(null) true<br/>
	 * isEmpty("") true<br/>
	 * isEmpty("null") false<br/>
	 * isEmpty(" ") true<br/>
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (null == str || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * isEmpty(null) false<br/>
	 * isEmpty("") false<br/>
	 * isEmpty("null") true<br/>
	 * isEmpty(" ") false<br/>
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * lcb 字符串检测
	 **/
	public static boolean isNotBlank(String... str) {
		boolean flag = true;
		for (String string : str) {
			if (isBlank(string)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 判断字符是否为中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 是否包含中文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取错误信息
	 * @author 张宏利
	 * @since 2017年4月1日
	 * @param e
	 * @return
	 */
	public static String getException(Throwable e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.toString();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * 判断是否是整数或者小数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^\\d+$|-\\d+$"); // 就是判断是否为整数
		Pattern patternPoint = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");// 判断是否为小数
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		isNum = patternPoint.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是整数数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^\\d+$|-\\d+$"); // 就是判断是否为整数
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 将第一个字符转换成大写<br/>
	 * 如：abc转换后：Abc,1ab转换后:1ab
	 * 
	 * @param str
	 *            传入的字符串
	 * @return 转换后的字符串
	 */
	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1))
				.toString();
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String randomString(int length) {
		StringBuilder builder = new StringBuilder(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			builder.append(random.nextInt(char_length));
		}
		return builder.toString();
	}

	/**
	 * 将字符串转换成整形
	 * 
	 * @param str
	 * @param defVal
	 * @return
	 */
	public static Integer parseInteger(String str, Integer defVal) {
		if (isEmpty(str)) {
			return defVal;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * @see StringUtil#parseInteger(String, int)
	 * @param str
	 * @return
	 */
	public static Integer parseInteger(String str) {
		return parseInteger(str, 0);
	}

	/**
	 * 将字符串转换成短整形
	 * 
	 * @param str
	 * @param defVal
	 * @return
	 */
	public static Short parseShort(String str, Short defVal) {
		if (isEmpty(str)) {
			return defVal;
		}
		try {
			return Short.parseShort(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * @see StringUtil#parseShort(String, int)
	 * @param str
	 * @return
	 */
	public static Short parseShort(String str) {
		return parseShort(str, (short) 0);
	}

	/**
	 * 字符串转换成double
	 * 
	 * @param str
	 * @param defVal
	 *            转换失败默认值
	 * @return
	 */
	public static Double parseDouble(String str, double defVal) {
		if (isEmpty(str)) {
			return defVal;
		}
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * @see StringUtil#parseDouble(String, double)
	 * @param str
	 * @return
	 */
	public static Double parseDouble(String str) {
		return parseDouble(str, 0.0);
	}

	public static BigDecimal parseBigDecimal(String str) {
		return parseBigDecimal(str, new BigDecimal(0.0));
	}

	public static BigDecimal parseBigDecimal(String str, BigDecimal def) {
		try {
			return BigDecimal.valueOf(parseDouble(str));
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 获取uuid 并去掉-
	 * 
	 * @return
	 */
	public static String getUUIDBySub() {
		String result = getUUID();
		return result.replace("-", "");
	}

	/**
	 * 获取uuid
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static Long parseLong(String str, long defVal) {
		if (isEmpty(str)) {
			return defVal;
		}
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	public static String getFileSub(String fileName) {
		String result = "";
		if (isNotEmpty(fileName)) {
			int index = fileName.lastIndexOf(".");
			if (index >= 0) {
				result = fileName.substring(index + 1, fileName.length());
			}
		}
		return result;
	}

	public static Long parseLong(String str) {
		return parseLong(str, 0);
	}

	public static Byte parseByte(String str) {
		return parseByte(str, (byte) 0);
	}

	public static Byte parseByte(String str, Byte def) {
		try {
			return Byte.parseByte(str);
		} catch (Exception e) {
			return def;
		}
	}

	public static boolean containerValue(Byte[] array, Byte value) {
		if (array != null && array.length > 0) {
			for (Byte val : array) {
				if (val == value) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 将一个给定的字符串，用给定的分隔符字符串（或字符串模式）分拆成字符串数组。 不使用中正则表达式
	 * 
	 * @param list
	 *            给定的字符串
	 * @param seperator
	 *            分隔符字符串
	 * @return 返回分隔成功后的字符串数组，如果字符串无法用给定的分隔符分拆，返回一 个大小为0的字符串数组。
	 */
	public static String[] split(String list, String seperator) {
		return split(list, seperator, false);
	}

	/**
	 * 将一个给定的字符串，用给定的分隔符字符串（或字符串模式）分拆成字符串数组。 不使用中正则表达式
	 * 
	 * @param list
	 *            给定的字符串
	 * @param seperator
	 *            分隔符字符串
	 * @param include
	 *            在新生成的数组中是否包含seperator串
	 * @return 返回分隔成功后的字符串数组，如果字符串无法用给定的分隔符分拆，返回一 个大小为0的字符串数组。
	 */
	public static String[] split(String list, String seperator, boolean include) {
		StringTokenizer tokens = new StringTokenizer(list, seperator, include);
		String[] result = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			result[i++] = tokens.nextToken();
		}
		return result;
	}

	/**
	 * 转json
	 * 
	 * @param <T>
	 * @param success
	 * @param msg
	 * @param data
	 * @return
	 */
	public static <T> String StringToJson(int success, String msg, List<T> data) {
		return "{\"code\" : \"" + success + "\", \"msg\" : \"" + msg + "\", \"data \": \"" + data + "\"}";
	}

	public static <T> String StringToJson(int success, String msg, List<T> data, String flag) {
		return "{\"code\" : \"" + success + "\", \"msg\" : \"" + msg + "\", \"data \": \"" + data + "\", \"flag \": \""
				+ flag + "\"}";
	}

	public static String htmlspecialchars(String str) {
		if (isEmpty(str)) {
			return str;
		}
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

	/**
	 * 将异常信息转化为String
	 * 
	 * @param e
	 * @return
	 */
	public static String exceptionStackToString(Throwable e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return sw.toString();
	}

	/**
	 * 地址重复判断补位 参考： Constants_DIC.DIC_ROOF_REPEAT_LENGTH_LIMIT
	 * Constants_DIC.DIC_UNIT_REPEAT_LENGTH_LIMIT
	 * Constants_DIC.DIC_FLOOR_REPEAT_LENGTH_LIMIT
	 * Constants_DIC.DIC_NUM_REPEAT_LENGTH_LIMIT
	 * 
	 * @author 陈文超
	 * @date Dec 19, 2015 2:09:38 PM
	 */
	public static String convertStr(String sourceStr, int length) {
		if (sourceStr == null)
			return null;
		// 如果全是不复合规则的值，直接返回。
		if (!sourceStr.matches("^[A-Z0-9]+$")) {
			// 不满足规则的 举例：提交上来的数据全部是中文或者不是全部由字母和数字组成。符合规则的栋座号：ABCD1231
			return sourceStr;
		}
		while (sourceStr.length() < length) {
			sourceStr = "0" + sourceStr;
		}
		char[] sourceChars = sourceStr.toCharArray();
		for (int i = 0; i < sourceChars.length; i++) {
			if (sourceChars[i] >= 65 && sourceChars[i] <= 73) {
				sourceChars[i] = (char) ((int) sourceChars[i] - 16);
			}
		}
		return new String(sourceChars);
	}

	public static Object Copy(Object source, Object dest) throws Exception {
		// 获取属性
		BeanInfo sourceBean = Introspector.getBeanInfo(source.getClass(), Object.class);
		PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();

		BeanInfo destBean = Introspector.getBeanInfo(dest.getClass(), Object.class);
		PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();

		try {
			for (int i = 0; i < sourceProperty.length; i++) {

				for (int j = 0; j < destProperty.length; j++) {

					if (sourceProperty[i].getName().equals(destProperty[j].getName())) {
						// 调用source的getter方法和dest的setter方法
						destProperty[j].getWriteMethod().invoke(dest, sourceProperty[i].getReadMethod().invoke(source));
						break;
					}
				}
			}
			return dest;
		} catch (Exception e) {
			throw new Exception("属性复制失败:" + e.getMessage());
		}
	}

	public static String replaceString(String source, String oldstring, String newstring) {
		Matcher m = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE).matcher(source);
		String result = m.replaceAll(newstring);
		return result;
	}

	/**
	 * 转换字符串，为空时返回defVal !=null && != "" && != "  "
	 * 
	 * @param source
	 *            需要判断的字段
	 * @param defVal
	 *            为空时返回的默认值
	 * @return
	 */
	public static String getBlankStr(Object source, String defVal) {
		if (source == null) {
			return defVal;
		}
		String val = String.valueOf(source);
		if (isBlank(val)) {
			return defVal;
		}
		return val;
	}

	public static String parseHTML(String source) {
		if (source != null && !"".equals(source)) {
			source = source.replaceAll("&lt;", "<");
			source = source.replaceAll("&gt;", ">");
			// source = source.replaceAll("&nbsp;"," ");//取消此项的原因是：网页中的半角空格会被忽略

			source = source.replaceAll("&quot;", "\"");
			source = source.replaceAll("&#39;", "'");
			source = source.replaceAll("&#34;", "\"");
			source = source.replaceAll("<BR><P>[\\s]*(?:&nbsp;)*[\\s]*</P><BR>|<br><p>[\\s]*(?:&nbsp;)*[\\s]*</p><br>",
					"");
			source = source.replaceAll("</P><BR>|</p><br>", "</p>");
			source = source.replaceAll("<P><BR>|<p><br>", "<p>");
			source = source.replaceAll("(<BR>|<BR/>|<br>|<br/>)+", "<br>");
		} else {
			source = "";
		}
		return source;
	}

	/**
	 * 将map翻译成字典序利
	 * 
	 * @param map
	 * @return
	 */
	public static String getMapSequenceUtil(HashMap<String, Object> map) {
		Collection<String> keyset = map.keySet();
		List<String> list = new ArrayList<String>(keyset);

		// 对key键值按字典升序排序
		Collections.sort(list);

		String param = "";
		for (int i = 0; i < list.size(); i++) {
			param += list.get(i) + "=" + map.get(list.get(i)) + "&";
		}
		if (!param.equals("")) {
			param = param.substring(0, param.length() - 1);
		}
		return param;
	}

	public static String getRStr(String text, int len) {
		StringBuilder result = new StringBuilder();
		try {
			int strLen = text.getBytes("GBK").length;
			if (strLen >= len) {
				return text;
			}

			int leftLen = len - strLen;
			if (leftLen > 2) {
				result.append("&nbsp;&nbsp;");
				leftLen -= 2;
			}
			result.append(text);
			for (int i = 0; i < leftLen; i++) {
				result.append("&nbsp;");
			}
			return result.toString();
		} catch (UnsupportedEncodingException e) {
			return "&nbsp;";
		}
	}

	/**
	 * 金额保留两位小数
	 * 
	 * @param doubleVal
	 * @return
	 */
	public static double doubleFormat(double doubleVal) {
		DecimalFormat df = new DecimalFormat("######0.00");
		String str = df.format(doubleVal);
		return parseDouble(str);
	}

	/**
	 * 生成安装密码
	 */
	public static String generateInstallPwd(int strLength) throws Exception {
		// 因1与l不容易分清楚，所以去除

		String strChar = "1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,";
		String[] aryChar = strChar.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strLength; i++) {
			sb.append(aryChar[(int) (Math.random() * strChar.length() / 2)]);
		}
		return sb.toString();
	}

	/**
	 * 占位符替换
	 * 
	 * @param replaceStr
	 *            需要替换的占位符 %s
	 * @param oldStr
	 *            需要替换的字符串
	 * @param replaceValue
	 *            替换的值
	 * @author 张卓
	 * @return
	 */
	public static String formatStr(String replaceStr, String oldStr, String[] replaceValue) {
		int num = 0;
		boolean isStart = true;
		int index = oldStr.indexOf(replaceStr);
		int end = index + replaceStr.length();
		while (isStart) {
			if (index != -1) {
				oldStr = oldStr.substring(0, index) + replaceValue[num] + oldStr.substring(end, oldStr.length());
			}
			index = oldStr.indexOf(replaceStr);
			end = index + replaceStr.length();
			if (index == -1) {
				isStart = false;
			}
			num++;
		}
		return oldStr;
	}

	/**
	 * 判断字段从是否是英文
	 * 
	 * @param charaString
	 * @return
	 */
	public static boolean isEnglish(String charaString) {
		return charaString.matches("^[a-zA-Z]*");
	}

	/**
	 * 计算两端文字之间的相似度
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static float levenshtein(String str1, String str2) {
		// 计算两个字符串的长度。
		int len1 = str1.length();
		int len2 = str2.length();
		// 建立上面说的数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		// 赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		// 计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 取三个值中最小的
				dif[i][j] = Math.min(dif[i - 1][j] + 1, Math.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1));
			}
		}
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		return similarity;
	}

	/**
	 * 验证是否是手机号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobilePhone(String mobile) {
		try {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(18[0-9])|(14[0-9])|(17[0-9])|(16[0-9])|(19[0-9]))\\d{8}$");
			Matcher m = p.matcher(mobile);
			return m.matches();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 解析sql占位符
	 * 
	 * @param sql
	 * @return
	 */
	public static Map<String, Object> parseSqlHolder(String sql) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isEmpty(sql)) {
			return resultMap;
		}

		Pattern p = Pattern.compile("#\\{(.*?[^\\}])\\}");
		Matcher m = p.matcher(sql);
		ArrayList<String> strs = new ArrayList<String>();
		while (m.find()) {
			strs.add(m.group(1));
		}
		for (String s : strs) {
			resultMap.put(s, null);
		}
		return resultMap;
	}

	/**
	 * 获取随机字符串 字母和数据组合
	 * @param witdh
	 * @returnasdfasdf
	 */
	public static String getRandomString(int witdh){
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < witdh; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	}

	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @author HeWei
	 * @since 2018年10月12日 上午9:14:19
	 * @param str
	 * @return
	 * Modified XXX HeWei 2018年10月12日
	 */
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
