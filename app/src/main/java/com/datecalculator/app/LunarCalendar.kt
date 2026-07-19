package com.datecalculator.app

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * 农历转换工具类
 * 支持公历与农历互转，范围 1900-2100 年
 * 包含天干地支、生肖、闰月处理
 */
object LunarCalendar {

    // ========== 农历数据表 (1900-2100) ==========
    // 编码规则：
    // 高4位 = 闰月月份 (0表示无闰月)
    // 低12位 = 1-12月大小月信息 (1=30天, 0=29天)
    // 0x10000位 = 闰月大小 (1=30天, 0=29天)
    private val LUNAR_INFO = longArrayOf(
        0x04bd8L, 0x04ae0L, 0x0a570L, 0x054d5L, 0x0d260L,  // 1900-1904
        0x0d950L, 0x16554L, 0x056a0L, 0x09ad0L, 0x055d2L,  // 1905-1909
        0x04ae0L, 0x0a5b6L, 0x0a4d0L, 0x0d250L, 0x1d255L,  // 1910-1914
        0x0b540L, 0x0d6a0L, 0x0ada2L, 0x095b0L, 0x14977L,  // 1915-1919
        0x04970L, 0x0a4b0L, 0x0b4b5L, 0x06a50L, 0x06d40L,  // 1920-1924
        0x1ab54L, 0x02b60L, 0x09570L, 0x052f2L, 0x04970L,  // 1925-1929
        0x06566L, 0x0d4a0L, 0x0ea50L, 0x16a95L, 0x05ad0L,  // 1930-1934
        0x02b60L, 0x186e3L, 0x092e0L, 0x1c8d7L, 0x0c950L,  // 1935-1939
        0x0d4a0L, 0x1d8a6L, 0x0b550L, 0x056a0L, 0x1a5b4L,  // 1940-1944
        0x025d0L, 0x092d0L, 0x0d2b2L, 0x0a950L, 0x0b557L,  // 1945-1949
        0x06ca0L, 0x0b550L, 0x15355L, 0x04da0L, 0x0a5b0L,  // 1950-1954
        0x14573L, 0x052b0L, 0x0a9a8L, 0x0e950L, 0x06aa0L,  // 1955-1959
        0x0aea6L, 0x0ab50L, 0x04b60L, 0x0aae4L, 0x0a570L,  // 1960-1964
        0x05260L, 0x0f263L, 0x0d950L, 0x05b57L, 0x056a0L,  // 1965-1969
        0x096d0L, 0x04dd5L, 0x04ad0L, 0x0a4d0L, 0x0d4d4L,  // 1970-1974
        0x0d250L, 0x0d558L, 0x0b540L, 0x0b6a0L, 0x195a6L,  // 1975-1979
        0x095b0L, 0x049b0L, 0x0a974L, 0x0a4b0L, 0x0b27aL,  // 1980-1984
        0x06a50L, 0x06d40L, 0x0af46L, 0x0ab60L, 0x09570L,  // 1985-1989
        0x04af5L, 0x04970L, 0x064b0L, 0x074a3L, 0x0ea50L,  // 1990-1994
        0x06b58L, 0x05ac0L, 0x0ab60L, 0x096d5L, 0x092e0L,  // 1995-1999
        0x0c960L, 0x0d954L, 0x0d4a0L, 0x0da50L, 0x07552L,  // 2000-2004
        0x056a0L, 0x0abb7L, 0x025d0L, 0x092d0L, 0x0cab5L,  // 2005-2009
        0x0a950L, 0x0b4a0L, 0x0baa4L, 0x0ad50L, 0x055d9L,  // 2010-2014
        0x04ba0L, 0x0a5b0L, 0x15176L, 0x052b0L, 0x0a930L,  // 2015-2019
        0x07954L, 0x06aa0L, 0x0ad50L, 0x05b52L, 0x04b60L,  // 2020-2024
        0x0a6e6L, 0x0a4e0L, 0x0d260L, 0x0ea65L, 0x0d530L,  // 2025-2029
        0x05aa0L, 0x076a3L, 0x096d0L, 0x04afbL, 0x04ad0L,  // 2030-2034
        0x0a4d0L, 0x1d0b6L, 0x0d250L, 0x0d520L, 0x0dd45L,  // 2035-2039
        0x0b5a0L, 0x056d0L, 0x055b2L, 0x049b0L, 0x0a577L,  // 2040-2044
        0x0a4b0L, 0x0aa50L, 0x1b255L, 0x06d20L, 0x0ada0L,  // 2045-2049
        0x14b63L, 0x09370L, 0x049f8L, 0x04970L, 0x064b0L,  // 2050-2054
        0x168a6L, 0x0ea50L, 0x06aa0L, 0x1a6c4L, 0x0aae0L,  // 2055-2059
        0x092e0L, 0x0d2e3L, 0x0c960L, 0x0d557L, 0x0d4a0L,  // 2060-2064
        0x0da50L, 0x05d55L, 0x056a0L, 0x0a6d0L, 0x055d4L,  // 2065-2069
        0x052d0L, 0x0a9b8L, 0x0a950L, 0x0b4a0L, 0x0b6a6L,  // 2070-2074
        0x0ad50L, 0x055a0L, 0x0aba4L, 0x0a5b0L, 0x052b0L,  // 2075-2079
        0x0b273L, 0x06930L, 0x07337L, 0x06aa0L, 0x0ad50L,  // 2080-2084
        0x14b55L, 0x04b60L, 0x0a570L, 0x054e4L, 0x0d160L,  // 2085-2089
        0x0e968L, 0x0d520L, 0x0daa0L, 0x16aa6L, 0x056d0L,  // 2090-2094
        0x04ae0L, 0x0a9d4L, 0x0a4d0L, 0x0d150L, 0x0f252L,  // 2095-2099
        0x0d520L                                            // 2100
    )

    // 农历天干
    private val TIAN_GAN = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

    // 农历地支
    private val DI_ZHI = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    // 生肖
    private val ZODIAC = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")

    // 农历月份名称
    private val LUNAR_MONTH_NAME = arrayOf(
        "正", "二", "三", "四", "五", "六",
        "七", "八", "九", "十", "冬", "腊"
    )

    // 农历日期名称
    private val LUNAR_DAY_NAME = arrayOf(
        "初一", "初二", "初三", "初四", "初五",
        "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五",
        "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五",
        "廿六", "廿七", "廿八", "廿九", "三十"
    )

    // 1900年1月31日对应的儒略日数（农历1900年正月初一）
    private const val LUNAR_EPOCH_YEAR = 1900
    private const val LUNAR_EPOCH_MONTH = 1
    private const val LUNAR_EPOCH_DAY = 31

    // ========== 基础数据查询方法 ==========

    /**
     * 获取某年的闰月月份，0表示无闰月
     */
    fun leapMonth(year: Int): Int {
        return (LUNAR_INFO[year - LUNAR_EPOCH_YEAR] and 0xf).toInt()
    }

    /**
     * 获取某年农历全年总天数
     */
    fun lunarYearDays(year: Int): Int {
        var sum = 348 // 12个月 * 29天 = 348（基础天数）
        val info = LUNAR_INFO[year - LUNAR_EPOCH_YEAR]
        // 遍历12个月的大小月标志位 (bit 15 ~ bit 3)
        var mask = 0x8000
        while (mask >= 0x8) {
            if (info and mask.toLong() != 0L) {
                sum += 1 // 大月多1天
            }
            mask = mask ushr 1
        }
        return sum + leapMonthDays(year)
    }

    /**
     * 获取某年闰月的天数，0表示无闰月
     */
    fun leapMonthDays(year: Int): Int {
        return if (leapMonth(year) != 0) {
            if (LUNAR_INFO[year - LUNAR_EPOCH_YEAR] and 0x10000L != 0L) 30 else 29
        } else 0
    }

    /**
     * 获取某年某月的天数
     * @param year 农历年
     * @param month 农历月 (1-12)
     */
    fun monthDays(year: Int, month: Int): Int {
        return if (LUNAR_INFO[year - LUNAR_EPOCH_YEAR] and (0x10000L shr month) != 0L) 30 else 29
    }

    // ========== 天干地支与生肖 ==========

    /**
     * 获取天干地支纪年字符串
     * @param lunarYear 农历年份
     * @return 如 "甲子"
     */
    fun getGanZhi(lunarYear: Int): String {
        val ganIndex = (lunarYear - 4) % 10
        val zhiIndex = (lunarYear - 4) % 12
        return TIAN_GAN[ganIndex] + DI_ZHI[zhiIndex]
    }

    /**
     * 获取生肖
     * @param lunarYear 农历年份
     * @return 如 "鼠"
     */
    fun getZodiac(lunarYear: Int): String {
        val index = (lunarYear - 4) % 12
        return ZODIAC[index]
    }

    /**
     * 获取农历月份名称
     * @param month 月份 (1-12)
     * @param isLeap 是否闰月
     */
    fun getMonthName(month: Int, isLeap: Boolean = false): String {
        val prefix = if (isLeap) "闰" else ""
        return prefix + LUNAR_MONTH_NAME[month - 1] + "月"
    }

    /**
     * 获取农历日期名称
     * @param day 日期 (1-30)
     */
    fun getDayName(day: Int): String {
        return LUNAR_DAY_NAME[day - 1]
    }

    // ========== 公历转农历 ==========

    /**
     * 公历日期转农历
     * @param solarYear 公历年
     * @param solarMonth 公历月 (1-12)
     * @param solarDay 公历日
     * @return LunarDate 农历日期对象
     */
    fun solarToLunar(solarYear: Int, solarMonth: Int, solarDay: Int): LunarDate {
        // 计算距离基准日（1900年1月31日）的天数
        val baseDate = GregorianCalendar(LUNAR_EPOCH_YEAR, 0, LUNAR_EPOCH_DAY)
        val targetDate = GregorianCalendar(solarYear, solarMonth - 1, solarDay)

        val offset = daysBetween(baseDate.timeInMillis, targetDate.timeInMillis).toInt()

        if (offset < 0) {
            throw IllegalArgumentException("日期超出农历范围（需 >= 1900年1月31日）")
        }

        // 从1900年开始逐年减去天数
        var temp = offset
        var lunarYear = LUNAR_EPOCH_YEAR
        var yearDays: Int

        while (temp >= 0) {
            yearDays = lunarYearDays(lunarYear)
            if (temp < yearDays) break
            temp -= yearDays
            lunarYear++
        }

        if (lunarYear > 2100) {
            throw IllegalArgumentException("日期超出农历范围（需 <= 2100年）")
        }

        // 确定闰月
        val leap = leapMonth(lunarYear)
        var isLeap = false

        // 逐月减去天数
        var lunarMonth = 1
        var monthDays: Int

        while (lunarMonth <= 12) {
            // 普通月天数
            monthDays = monthDays(lunarYear, lunarMonth)
            if (temp < monthDays) break
            temp -= monthDays

            // 如果有闰月且当前月已过
            if (leap > 0 && lunarMonth == leap && !isLeap) {
                // 进入闰月
                isLeap = true
                monthDays = leapMonthDays(lunarYear)
                if (temp < monthDays) break
                temp -= monthDays
                isLeap = false
            }
            lunarMonth++
        }

        val lunarDay = temp + 1

        return LunarDate(lunarYear, lunarMonth, lunarDay, isLeap)
    }

    // ========== 农历转公历 ==========

    /**
     * 农历日期转公历
     * @param lunarYear 农历年
     * @param lunarMonth 农历月 (1-12)
     * @param lunarDay 农历日
     * @param isLeapMonth 是否闰月
     * @return SolarDate 公历日期对象
     */
    fun lunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int, isLeapMonth: Boolean = false): SolarDate {
        if (lunarYear < LUNAR_EPOCH_YEAR || lunarYear > 2100) {
            throw IllegalArgumentException("农历年份需在 1900-2100 范围内")
        }

        // 计算从1900年正月初一到目标日期的总天数
        var offset = 0

        // 加上整年的天数
        for (year in LUNAR_EPOCH_YEAR until lunarYear) {
            offset += lunarYearDays(year)
        }

        // 加上整月的天数
        val leap = leapMonth(lunarYear)
        var isLeapPassed = false

        for (month in 1 until lunarMonth) {
            offset += monthDays(lunarYear, month)
            // 如果闰月在该月之前或就是该月
            if (leap > 0 && month == leap && !isLeapPassed) {
                offset += leapMonthDays(lunarYear)
                isLeapPassed = true
            }
        }

        // 处理目标月是否是闰月
        if (isLeapMonth && lunarMonth == leap) {
            offset += monthDays(lunarYear, lunarMonth)
        }

        // 加上日期天数
        offset += lunarDay - 1

        // 从基准日期（1900年1月31日）加上偏移天数
        val baseCal = GregorianCalendar(LUNAR_EPOCH_YEAR, 0, LUNAR_EPOCH_DAY)
        baseCal.add(Calendar.DAY_OF_MONTH, offset)

        return SolarDate(
            baseCal.get(Calendar.YEAR),
            baseCal.get(Calendar.MONTH) + 1,
            baseCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // ========== 辅助方法 ==========

    /**
     * 计算两个日期之间的天数差
     */
    private fun daysBetween(startMillis: Long, endMillis: Long): Long {
        val oneDay = 24L * 60 * 60 * 1000
        return (endMillis - startMillis) / oneDay
    }

    /**
     * 获取星期几的中文名
     */
    fun getWeekdayName(year: Int, month: Int, day: Int): String {
        val cal = GregorianCalendar(year, month - 1, day)
        val names = arrayOf("日", "一", "二", "三", "四", "五", "六")
        return "星期" + names[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }

    // ========== 数据类 ==========

    /**
     * 农历日期数据类
     */
    data class LunarDate(
        val year: Int,      // 农历年
        val month: Int,     // 农历月 (1-12)
        val day: Int,       // 农历日 (1-30)
        val isLeap: Boolean // 是否闰月
    ) {
        /** 获取完整的农历日期字符串 */
        fun toFullString(): String {
            return "${getGanZhi(year)}年【${getZodiac(year)}】${getMonthName(month, isLeap)}${getDayName(day)}"
        }

        /** 获取简短的农历日期字符串 */
        fun toShortString(): String {
            val leapPrefix = if (isLeap) "闰" else ""
            return "农历${year}年${leapPrefix}${LUNAR_MONTH_NAME[month - 1]}月${LUNAR_DAY_NAME[day - 1]}"
        }
    }

    /**
     * 公历日期数据类
     */
    data class SolarDate(
        val year: Int,
        val month: Int,
        val day: Int
    ) {
        fun toDateString(): String {
            return "${year}年${month}月${day}日"
        }
    }
}
