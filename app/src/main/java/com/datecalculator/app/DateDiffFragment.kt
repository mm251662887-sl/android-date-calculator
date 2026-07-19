package com.datecalculator.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.datecalculator.app.databinding.FragmentDateDiffBinding
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * 日期差值计算 Fragment
 * 选择起止日期（可选时分），计算天数/小时/分钟/秒/周数
 */
class DateDiffFragment : Fragment() {

    private var _binding: FragmentDateDiffBinding? = null
    private val binding get() = _binding!!

    // 起止日期时间
    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1) // 默认结束日期为明天
    }
    private var hasStartTime = false
    private var hasEndTime = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDateDiffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDateInputs()
        setupTimeOptions()
        setupShortcutButtons()
        setupCalculateButton()
        updateDateDisplay()
    }

    /** 设置日期输入点击事件 */
    private fun setupDateInputs() {
        binding.startDateInput.setOnClickListener {
            showDatePicker(true)
        }
        binding.endDateInput.setOnClickListener {
            showDatePicker(false)
        }
    }

    /** 显示日期选择器 */
    private fun showDatePicker(isStart: Boolean) {
        val cal = if (isStart) startCalendar else endCalendar
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                updateDateDisplay()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /** 显示时间选择器 */
    private fun showTimePicker(isStart: Boolean) {
        val cal = if (isStart) startCalendar else endCalendar
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                updateTimeDisplay()
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    /** 设置时间选项 */
    private fun setupTimeOptions() {
        binding.includeStartTimeCheck.setOnCheckedChangeListener { _, isChecked ->
            hasStartTime = isChecked
            binding.startTimeLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                binding.startTimeInput.setOnClickListener { showTimePicker(true) }
            }
        }
        binding.includeEndTimeCheck.setOnCheckedChangeListener { _, isChecked ->
            hasEndTime = isChecked
            binding.endTimeLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                binding.endTimeInput.setOnClickListener { showTimePicker(false) }
            }
        }
    }

    /** 更新界面日期显示 */
    private fun updateDateDisplay() {
        binding.startDateInput.setText(formatDate(startCalendar))
        binding.endDateInput.setText(formatDate(endCalendar))
        if (hasStartTime) updateTimeDisplay()
    }

    /** 更新时间显示 */
    private fun updateTimeDisplay() {
        if (hasStartTime) {
            binding.startTimeInput.setText(formatTime(startCalendar))
        }
        if (hasEndTime) {
            binding.endTimeInput.setText(formatTime(endCalendar))
        }
    }

    /** 设置快捷操作按钮 */
    private fun setupShortcutButtons() {
        val today = Calendar.getInstance()

        // 今天到年底
        binding.btnTodayToYearEnd.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.DECEMBER)
                set(Calendar.DAY_OF_MONTH, 31)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 未来100天
        binding.btnNext100Days.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 100)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 未来一年
        binding.btnNextYear.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 本周剩余
        binding.btnThisWeek.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 未来30天
        binding.btnNextMonth.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 30)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 未来1000天
        binding.btn1000Days.setOnClickListener {
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1000)
            }
            hasStartTime = false
            hasEndTime = false
            binding.includeStartTimeCheck.isChecked = false
            binding.includeEndTimeCheck.isChecked = false
            updateDateDisplay()
        }

        // 为快捷按钮添加特效
        listOf(
            binding.btnTodayToYearEnd, binding.btnNext100Days, binding.btnNextYear,
            binding.btnThisWeek, binding.btnNextMonth, binding.btn1000Days
        ).forEach { ButtonEffects.applyShortcutEffects(it) }
    }

    /** 设置计算按钮 */
    private fun setupCalculateButton() {
        ButtonEffects.applyAllEffects(binding.btnCalculateDiff)

        binding.btnCalculateDiff.setOnClickListener {
            calculateDifference()
        }
    }

    /** 执行日期差值计算 */
    private fun calculateDifference() {
        val startMillis = startCalendar.timeInMillis
        val endMillis = endCalendar.timeInMillis

        if (endMillis <= startMillis) {
            binding.resultCard.visibility = View.GONE
            return
        }

        val diffMillis = endMillis - startMillis

        // 计算各种时间单位的差值
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis)
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val totalHours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val totalWeeks = totalDays / 7

        // 显示结果
        binding.resultCard.visibility = View.VISIBLE
        binding.resultGrid.removeAllViews()

        // 添加结果项
        addResultItem("天", totalDays.toString())
        addResultItem("小时", totalHours.toString())
        addResultItem("分钟", totalMinutes.toString())
        addResultItem("秒", totalSeconds.toString())
        addResultItem("周", totalWeeks.toString())

        // 如果包含时间，显示更精确的结果
        if (hasStartTime || hasEndTime) {
            val remainHours = totalHours - totalDays * 24
            val remainMinutes = totalMinutes - totalHours * 60
            val remainSeconds = totalSeconds - totalMinutes * 60
            addResultItem("精确", "${totalDays}天 ${remainHours}时 ${remainMinutes}分 ${remainSeconds}秒")
        }
    }

    /** 向结果网格添加一项 */
    private fun addResultItem(label: String, value: String) {
        val context = requireContext()

        // 标签
        val labelView = TextView(context).apply {
            text = label
            setTextColor(context.getColor(R.color.text_secondary))
            textSize = 14f
            layoutParams = GridLayout.LayoutParams().apply {
                setMargins(0, 0, 16, 16)
            }
        }

        // 数值
        val valueView = TextView(context).apply {
            text = value
            setTextColor(context.getColor(R.color.cyan_200))
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = GridLayout.LayoutParams().apply {
                setMargins(0, 0, 0, 16)
            }
        }

        binding.resultGrid.addView(labelView)
        binding.resultGrid.addView(valueView)
    }

    /** 格式化日期显示 */
    private fun formatDate(cal: Calendar): String {
        return String.format("%d年%02d月%02d日",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH))
    }

    /** 格式化时间显示 */
    private fun formatTime(cal: Calendar): String {
        return String.format("%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
