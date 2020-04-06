package com.wp.app.resource.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    /**
     * 金额格式化
     */
    public static final String MONEY_FORMAT = "#,###,##0.00";

    private FormatUtils() {
    }

    /**
     * 格式化金额
     *
     * @param money 金额
     * @return 格式化金额
     */
    public static String formatMoneyTheme(long money) {
        return formatMoneyWithSymbol(String.valueOf(money / 100.0F));
    }

    /**
     * 格式化金额
     *
     * @param money 金额
     * @return 格式化金额
     */
    public static String formatMoney(long money) {
        return formatMoney(String.valueOf(money / 100.0F));
    }

    /**
     * 格式化金额
     *
     * @param money 金额
     * @return 格式化金额
     */
    public static String formatMoneyWithSymbol(long money) {
        return formatMoneyWithSymbol(String.valueOf(money));
    }

    /**
     * 格式化金额
     *
     * @param money 金额
     * @return 格式化金额
     */
    public static String formatMoneyWithSymbol(String money) {
        if ("null".equals(money)) {
            money = "0";
        }
        return "￥" + formatMoney(money);
    }

    /**
     * 格式化金额
     *
     * @param money 金额
     * @return 格式化金额
     */
    public static String formatMoney(String money) {
        if ("null".equals(money)) {
            money = "0";
        }
        return formatDecimal(money, MONEY_FORMAT);
    }

    public static String format2Digit(int num) {
        return String.format(Locale.CHINESE, "%02d", num);
    }

    /**
     * 格式化手机号码
     *
     * @param mobile 手机号码
     * @return 格式化手机号码
     */
    public static String formatMobile(String mobile) {
        try {
            return mobile.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
        } catch (Exception ignored) {
        }
        return mobile;
    }

    /**
     * @return 名字末尾*
     */
    public static String formatUserName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        name = name.replace(name.charAt(name.length() - 1) + "", "*");
        return name;
    }

    /**
     * @return 名字末尾*
     */
    public static String formatUserName2(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() == 1) {
            name = "*刚刚参团了";
        } else {
            name = name.substring(0, 1) + "*刚刚参团了";
        }
        return name;
    }

    /**
     * 格式化距离
     *
     * @param distance 距离
     * @return 格式化距离
     */
    public static String formatDistance(String distance) {
        double distanceNumber = Double.parseDouble(distance);
        try {
            if (distanceNumber > 1000) {
                return String.format(Locale.CHINA, "%.2fkm", distanceNumber / 1000);
            }
            return String.format(Locale.CHINA, "%.2fm", distanceNumber);
        } catch (Exception ignored) {
        }
        return distance;
    }

    private static SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA);

    public static String formatTime(String time) {
        try {
            Date date = FULL_FORMAT.parse(time);
            return FULL_FORMAT.format(date);
        } catch (Exception ignored) {
        }
        return time;
    }

    /**
     * GMT(格林威治标准时间)转换当前北京时间
     * 比如：1526217409 -->2018/5/13 21:16:49 与北京时间相差8个小时，调用下面的方法，是在1526217409加上8*3600秒
     *
     * @param GMT 秒单部位
     * @return
     */
    public static String stampToDate(String GMT) {
        if (TextUtils.isEmpty(GMT)) {
            return GMT;
        }
        // long lt = Long.parseLong(GMT) + 8 * 3600;
        long lt = Long.parseLong(GMT);
        String res = null;
        try {
            res = FULL_FORMAT.format(lt * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public static String stampToDate(String GMT, String format) {
        if (TextUtils.isEmpty(GMT)) {
            return GMT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        // long lt = Long.parseLong(GMT) + 8 * 3600;
        long lt = Long.parseLong(GMT);
        String res = null;
        try {
            res = simpleDateFormat.format(lt * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate2(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 格式化最近时间段
     *
     * @param time 时间格式
     * @return 最近时间段
     */
    public static String formatRecentTime(String time) {
        try {
            Date date = FULL_FORMAT.parse(time);
            return RECENT_FORMAT.format(date);
        } catch (Exception ignored) {
        }
        return time;
    }

    /**
     * 格式化银行卡账号
     *
     * @param bankCardNumber 银行卡账号
     * @return 银行卡账号
     */
    public static String formatBankCardNumber(String bankCardNumber) {
        int length = bankCardNumber != null ? bankCardNumber.length() : 0;
        if (length > 4) {
            return "**** **** **** " + bankCardNumber.substring(length - 4, length);
        }
        return "**** **** **** " + bankCardNumber;
    }

    public static String formatBankCardNumber4NoStart(String bankCardNumber) {
        int length = bankCardNumber != null ? bankCardNumber.length() : 0;
        if (length > 4) {
            return "(" + bankCardNumber.substring(length - 4, length) + ")";
        }
        return "(" + bankCardNumber + ")";
    }

    /**
     * 格式数字
     *
     * @param number 数字
     * @return 格式化后字符串
     */
    public static String formatDecimal(double number, String pattern) {
        return formatDecimal(String.valueOf(number), pattern, RoundingMode.DOWN);
    }

    /**
     * 格式数字
     *
     * @param number 数字
     * @return 格式化后字符串
     */
    public static String formatDecimal(String number, String pattern) {
        return formatDecimal(number, pattern, RoundingMode.DOWN);
    }

    /**
     * 格式数字
     *
     * @param number 数字
     * @return 格式化后字符串
     */
    public static String formatDecimal(double number, String pattern, RoundingMode roundingMode) {
        return formatDecimal(String.valueOf(number), pattern, roundingMode);
    }

    /**
     * 格式数字
     *
     * @param number 数字
     * @return 格式化后字符串
     */
    public static String formatDecimal(String number, String pattern, RoundingMode roundingMode) {
        try {
            DecimalFormat format = new DecimalFormat(pattern);
            format.setRoundingMode(roundingMode);
            return format.format(new BigDecimal(number));
        } catch (Exception e) {
            return number;
        }
    }

    public static double parseDecimal(String text, String pattern) {
        DecimalFormat format = new DecimalFormat(pattern);
        try {
            return format.parse(text).doubleValue();
        } catch (Exception ignored) {
        }
        return 0;
    }

    /**
     * 去掉多余的0
     */
    public static String formatUseless0(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * sales.
     *
     * @param sales
     * @return
     */
    public static String formatSaleNum(int sales) {
        String saleStr = String.valueOf(sales);
        if (sales > 10000) {
            saleStr = String.format(Locale.CHINA, "%.2f万", sales / 10000.0);
        }
        return String.format(Locale.CHINA, "销量%s", formatFloatNum(saleStr));
    }

    public static String formatFloatNum(String numStr) {
        if (numStr != null && numStr.indexOf(".") > 0) {
            numStr = numStr.replaceAll("0+?$", "");//去掉后面无用的零
            numStr = numStr.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return numStr;
    }

    /**
     * 名字加*号
     */
    public static String getStarString(String content, int begin, int end) {

        if (content == null) {
            return "";
        }
        if (begin > content.length() || begin < 0) {
            return content;
        }
        if (end > content.length() || end < 0) {
            return content;
        }
        if (begin > end) {
            return content;
        }
        String starStr = "";
        for (int i = begin; i < end; i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, begin) + starStr + content.substring(end, content.length());

    }

    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
     */
    public static String getStarString2(String content, int frontNum, int endNum) {

        if (content == null) {
            return "";
        }
        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, frontNum) + starStr
                + content.substring(content.length() - endNum, content.length());

    }
}
