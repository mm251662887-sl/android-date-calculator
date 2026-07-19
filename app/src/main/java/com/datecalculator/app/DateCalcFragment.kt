package com.datecalculator.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.datecalculator.app.databinding.FragmentDateCalcBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

/**
 * 日期推算 Fragment
 * 从指定日期加减天数，得出目标日期
 */
class DateCalcFragment : Fragment() {

    private var _binding: FragmentDateCalcBinding? = null
    private val binding get() = _binding!!

    // 基准日期
    private var baseCalendar: Calendar = Calendar.getInstance()
    // 是否为加法模式
    private var isAddMode = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDateCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBaseDateInput()
        setupToggleGroup()
        setupQuickButtons()
        setupCalculateButton()
        updateDateDisplay()

        // 默认选中"加"
        binding.btnAdd.isChecked = true
    }

    /** 设置基准日期输入 */
    private fun setupBaseDateInput() {
        binding.baseDateInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    baseCalendar.set(year, month, dayOfMonth)
                    updateDateDisplay()
                },
                baseCalendar.get(Calendar.YEAR),
                baseCalendar.get(Calendar.MONTH),
                baseCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    /** 设置加减切换 */
    private fun setupToggleGroup() {
        binding.toggleAddSubtract.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isAddMode = checkedId == binding.btnAdd.id
            }
        }
    }

    /** 设置快捷天数按钮 */
    private fun setupQuickButtons() {
        binding.btnQuick7.setOnClickListener {
            binding.daysInput.setText("7")
        }
        binding.btnQuick30.setOnClickListener {
            binding.daysInput.setText("30")
        }
        binding.btnQuick100.setOnClickListener {
            binding.daysInput.setText("100")
        }
        binding.btnQuick365.setOnClickListener {
            binding.daysInput.setText("365")
        }

        // 为快捷按钮添加特效
        listOf(binding.btnQuick7, binding.btnQuick30, binding.btnQuick100, binding.btnQuick365)
            .forEach { ButtonEffects.applyShortcutEffects(it) }
    }

    /** 设置计算按钮 */
    private fun setupCalculateButton() {
        ButtonEffects.applyAllEffects(binding.btnCalcDate)

        binding.btnCalcDate.setOnClickListener {
            performCalculation()
        }
    }

    /** 执行日期推算 */
    private fun performCalculation() {
        val daysText = binding.daysInput.text.toString().trim()
        if (daysText.isEmpty()) {
            Snackbar.make(binding.root, "请输入天数", Snackbar.LENGTH_SHORT).show()
            return
        }

        val days = daysText.toIntOrNull()
        if (days == null || days < 0) {
            Snackbar.make(binding.root, "请输入有效的天数", Snackbar.LENGTH_SHORT).show()
            return
        }

        // 计算目标日期
        val resultCalendar = baseCalendar.clone() as Calendar
        if (isAddMode) {
            resultCalendar.add(Calendar.DAY_OF_YEAR, days)
        } else {
            resultCalendar.add(Calendar.DAY_OF_YEAR, -days)
        }

        // 显示结果
        val year = resultCalendar.get(Calendar.YEAR)
        val month = resultCalendar.get(Calendar.MONTH) + 1
        val day = resultCalendar.get(Calendar.DAY_OF_MONTH)

        binding.calcResultCard.visibility = View.VISIBLE
        binding.resultDateText.text = String.format("%d年%02d月%02d日", year, month, day)
        binding.resultWeekdayText.text = LunarCalendar.getWeekdayName(year, month, day)

        // 显示对应的农历日期
        try {
            val lunarDate = LunarCalendar.solarToLunar(year, month, day)
            binding.resultLunarText.text = lunarDate.toShortString()
        } catch (e: Exception) {
            binding.resultLunarText.text = ""
        }
    }

    /** 更新基准日期显示 */
    private fun updateDateDisplay() {
        binding.baseDateInput.text = String.format("%d年%02d月%02d日",
            baseCalendar.get(Calendar.YEAR),
            baseCalendar.get(Calendar.MONTH) + 1,
            baseCalendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
