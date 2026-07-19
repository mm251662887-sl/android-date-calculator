package com.datecalculator.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.datecalculator.app.databinding.FragmentLunarBinding
import com.google.android.material.snackbar.Snackbar

/**
 * 农历转换 Fragment
 * 支持公历转农历、农历转公历
 * 显示天干地支、生肖、闰月信息
 */
class LunarFragment : Fragment() {

    private var _binding: FragmentLunarBinding? = null
    private val binding get() = _binding!!

    // 当前模式：true=公历转农历, false=农历转公历
    private var isSolarToLunar = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLunarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDirectionToggle()
        setupConvertButton()

        // 默认选中公历转农历
        binding.btnSolarToLunar.isChecked = true
        updateInputHint()
    }

    /** 设置转换方向切换 */
    private fun setupDirectionToggle() {
        binding.toggleDirection.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isSolarToLunar = checkedId == binding.btnSolarToLunar.id
                updateInputHint()
            }
        }
    }

    /** 更新输入提示文字 */
    private fun updateInputHint() {
        if (isSolarToLunar) {
            binding.inputTitle.text = "输入公历日期"
            binding.yearInputLayout.hint = "年份 (1900-2100)"
            binding.monthInputLayout.hint = "月份 (1-12)"
            binding.dayInputLayout.hint = "日期"
            binding.leapMonthCheck.visibility = View.GONE
        } else {
            binding.inputTitle.text = "输入农历日期"
            binding.yearInputLayout.hint = "农历年 (1900-2100)"
            binding.monthInputLayout.hint = "农历月 (1-12)"
            binding.dayInputLayout.hint = "农历日 (1-30)"
            binding.leapMonthCheck.visibility = View.VISIBLE
        }
    }

    /** 设置转换按钮 */
    private fun setupConvertButton() {
        ButtonEffects.applyAllEffects(binding.btnConvertLunar)

        binding.btnConvertLunar.setOnClickListener {
            performConversion()
        }
    }

    /** 执行转换 */
    private fun performConversion() {
        val yearText = binding.yearInput.text.toString().trim()
        val monthText = binding.monthInput.text.toString().trim()
        val dayText = binding.dayInput.text.toString().trim()

        if (yearText.isEmpty() || monthText.isEmpty() || dayText.isEmpty()) {
            Snackbar.make(binding.root, "请填写完整的日期", Snackbar.LENGTH_SHORT).show()
            return
        }

        val year = yearText.toIntOrNull()
        val month = monthText.toIntOrNull()
        val day = dayText.toIntOrNull()

        if (year == null || month == null || day == null) {
            Snackbar.make(binding.root, "请输入有效的数字", Snackbar.LENGTH_SHORT).show()
            return
        }

        try {
            if (isSolarToLunar) {
                convertSolarToLunar(year, month, day)
            } else {
                val isLeap = binding.leapMonthCheck.isChecked
                convertLunarToSolar(year, month, day, isLeap)
            }
        } catch (e: Exception) {
            Snackbar.make(binding.root, "转换失败: ${e.message}", Snackbar.LENGTH_SHORT).show()
            binding.lunarResultCard.visibility = View.GONE
        }
    }

    /** 公历转农历 */
    private fun convertSolarToLunar(solarYear: Int, solarMonth: Int, solarDay: Int) {
        // 验证输入
        if (solarYear < 1900 || solarYear > 2100) {
            throw IllegalArgumentException("公历年份需在 1900-2100 范围内")
        }

        val lunarDate = LunarCalendar.solarToLunar(solarYear, solarMonth, solarDay)

        // 显示结果
        binding.lunarResultCard.visibility = View.VISIBLE

        // 主要结果 - 农历日期
        binding.lunarDateResult.text = lunarDate.toFullString()

        // 详细信息
        binding.ganzhiText.text = LunarCalendar.getGanZhi(lunarDate.year)
        binding.zodiacText.text = LunarCalendar.getZodiac(lunarDate.year)
        binding.lunarYearText.text = "${lunarDate.year}年"
        binding.leapMonthText.text = if (lunarDate.isLeap) "是（闰${lunarDate.month}月）" else "否"
    }

    /** 农历转公历 */
    private fun convertLunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int, isLeap: Boolean) {
        // 验证输入
        if (lunarYear < 1900 || lunarYear > 2100) {
            throw IllegalArgumentException("农历年份需在 1900-2100 范围内")
        }
        if (lunarMonth < 1 || lunarMonth > 12) {
            throw IllegalArgumentException("农历月份需在 1-12 范围内")
        }
        if (lunarDay < 1 || lunarDay > 30) {
            throw IllegalArgumentException("农历日期需在 1-30 范围内")
        }

        val solarDate = LunarCalendar.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeap)

        // 显示结果
        binding.lunarResultCard.visibility = View.VISIBLE

        // 主要结果 - 公历日期
        binding.lunarDateResult.text = "公历 ${solarDate.toDateString()}"

        // 详细信息
        binding.ganzhiText.text = LunarCalendar.getGanZhi(lunarYear)
        binding.zodiacText.text = LunarCalendar.getZodiac(lunarYear)
        binding.lunarYearText.text = "${lunarYear}年"

        val leapMonth = LunarCalendar.leapMonth(lunarYear)
        binding.leapMonthText.text = if (leapMonth > 0) "${leapMonth}月" else "该年无闰月"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
