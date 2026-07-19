package com.datecalculator.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.datecalculator.app.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 主界面 - 日期时间计算器
 * 使用 TabLayout + ViewPager2 管理四个功能模块
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 标签页标题
    private val tabTitles = arrayOf("日期差值", "日期推算", "时间换算", "农历转换")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置 ViewPager2 适配器
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 4 // 预加载所有页面

        // 关联 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // 设置状态栏颜色
        window.statusBarColor = getColor(R.color.background_dark)
    }

    /**
     * ViewPager2 页面适配器
     */
    private class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        // 四个功能 Fragment
        private val fragments = listOf(
            DateDiffFragment(),
            DateCalcFragment(),
            TimeConvFragment(),
            LunarFragment()
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
