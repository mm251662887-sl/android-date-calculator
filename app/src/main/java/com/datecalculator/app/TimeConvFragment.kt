package com.datecalculator.app

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.datecalculator.app.databinding.FragmentTimeConvBinding
import com.google.android.material.snackbar.Snackbar

/**
 * 时间换算 Fragment
 * 时间单位互转：秒/分/时/天/周/月/年
 */
class TimeConvFragment : Fragment() {

    private var _binding: FragmentTimeConvBinding? = null
    private val binding get() = _binding!!

    // 时间单位定义 - 以秒为基准的换算系数
    private data class TimeUnit(val name: String, val toSeconds: Double)

    private val timeUnits = listOf(
        TimeUnit("秒", 1.0),
        TimeUnit("分钟", 60.0),
        TimeUnit("小时", 3600.0),
        TimeUnit("天", 86400.0),
        TimeUnit("周", 604800.0),
        TimeUnit("月", 2592000.0),    // 30天
        TimeUnit("年", 31536000.0)    // 365天
    )

    // Chip ID 到单位索引的映射
    private val chipToUnitIndex = mapOf(
        0 to 0,  // 秒
        1 to 1,  // 分钟
        2 to 2,  // 小时
        3 to 3,  // 天
        4 to 4,  // 周
        5 to 5,  // 月
        6 to 6   // 年
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimeConvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupConvertButton()
    }

    /** 设置转换按钮 */
    private fun setupConvertButton() {
        ButtonEffects.applyAllEffects(binding.btnConvert)

        binding.btnConvert.setOnClickListener {
            performConversion()
        }
    }

    /** 获取当前选中的单位索引 */
    private fun getSelectedUnitIndex(): Int {
        val chipGroup = binding.fromUnitGroup
        val checkedChipId = chipGroup.checkedChipId

        return when (checkedChipId) {
            binding.chipSecond.id -> 0
            binding.chipMinute.id -> 1
            binding.chipHour.id -> 2
            binding.chipDay.id -> 3
            binding.chipWeek.id -> 4
            binding.chipMonth.id -> 5
            binding.chipYear.id -> 6
            else -> 0 // 默认秒
        }
    }

    /** 执行时间换算 */
    private fun performConversion() {
        val valueText = binding.timeValueInput.text.toString().trim()
        if (valueText.isEmpty()) {
            Snackbar.make(binding.root, "请输入数值", Snackbar.LENGTH_SHORT).show()
            return
        }

        val value = valueText.toDoubleOrNull()
        if (value == null) {
            Snackbar.make(binding.root, "请输入有效的数值", Snackbar.LENGTH_SHORT).show()
            return
        }

        val fromIndex = getSelectedUnitIndex()
        val fromUnit = timeUnits[fromIndex]

        // 先转换为秒
        val totalSeconds = value * fromUnit.toSeconds

        // 显示所有单位的换算结果
        binding.convertResultCard.visibility = View.VISIBLE
        binding.resultContainer.removeAllViews()

        for ((index, unit) in timeUnits.withIndex()) {
            if (index == fromIndex) continue // 跳过原单位

            val converted = totalSeconds / unit.toSeconds
            addResultRow(unit.name, formatNumber(converted))
        }
    }

    /** 向结果容器添加一行 */
    private fun addResultRow(unitName: String, value: String) {
        val context = requireContext()

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
            gravity = Gravity.CENTER_VERTICAL
        }

        // 单位名称
        val unitView = TextView(context).apply {
            text = unitName
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // 换算数值
        val valueView = TextView(context).apply {
            text = value
            setTextColor(context.getColor(R.color.cyan_200))
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f)
        }

        row.addView(unitView)
        row.addView(valueView)
        binding.resultContainer.addView(row)
    }

    /** 格式化数字 - 去除不必要的小数位 */
    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else if (kotlin.math.abs(value) >= 1000000) {
            String.format("%.2e", value)
        } else if (kotlin.math.abs(value) >= 1) {
            String.format("%,.4f", value).trimEnd('0').trimEnd('.')
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
