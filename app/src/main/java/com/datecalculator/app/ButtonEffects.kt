package com.datecalculator.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat

/**
 * 按钮高级特效工具类
 * 包含：涟漪扩散、渐变流光、按下弹性缩放、悬停状态变化
 */
object ButtonEffects {

    // 渐变色数组（紫色 -> 蓝色 -> 青色）
    private val GRADIENT_COLORS = intArrayOf(
        0xFF7C4DFF.toInt(),  // 紫色
        0xFF536DFE.toInt(),  // 蓝色
        0xFF00E5FF.toInt(),  // 青色
        0xFF536DFE.toInt(),  // 蓝色
        0xFF7C4DFF.toInt()   // 紫色
    )

    /**
     * 为按钮应用全部特效
     * @param view 目标视图
     * @param enableShine 是否启用流光动画
     * @param enableScale 是否启用弹性缩放
     */
    fun applyAllEffects(view: View, enableShine: Boolean = true, enableScale: Boolean = true) {
        if (enableShine) applyGradientShine(view)
        if (enableScale) applyElasticScale(view)
    }

    /**
     * 渐变流光动画效果
     * 在按钮表面创建流动的渐变光效
     */
    fun applyGradientShine(view: View) {
        val shineDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                0xFF7C4DFF.toInt(),
                0xFF536DFE.toInt(),
                0xFF00E5FF.toInt(),
                0xFF536DFE.toInt(),
                0xFF7C4DFF.toInt(),
                0xFF00E5FF.toInt(),
                0xFF7C4DFF.toInt()
            )
        )
        shineDrawable.shape = GradientDrawable.RECTANGLE
        shineDrawable.cornerRadii = floatArrayOf(
            56f, 56f, 56f, 56f, 56f, 56f, 56f, 56f
        )

        view.background = shineDrawable

        // 使用 ValueAnimator 创建流光效果
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                // 动态更新渐变角度，创造流光效果
                val angle = fraction * 360f
                val newDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        shiftColor(0xFF7C4DFF.toInt(), fraction),
                        shiftColor(0xFF536DFE.toInt(), fraction),
                        shiftColor(0xFF00E5FF.toInt(), fraction),
                        shiftColor(0xFF536DFE.toInt(), fraction),
                        shiftColor(0xFF7C4DFF.toInt(), fraction)
                    )
                )
                newDrawable.shape = GradientDrawable.RECTANGLE
                newDrawable.cornerRadii = floatArrayOf(
                    56f, 56f, 56f, 56f, 56f, 56f, 56f, 56f
                )
                view.background = newDrawable
            }
        }

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                if (!animator.isStarted) animator.start()
            }
            override fun onViewDetachedFromWindow(v: View) {
                animator.cancel()
            }
        })
    }

    /**
     * 弹性缩放反馈效果
     * 按下时缩小，松开时弹性恢复
     */
    fun applyElasticScale(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 按下 - 缩小
                    v.animate()
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(100)
                        .setInterpolator(OvershootInterpolator(2f))
                        .start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 松开 - 弹性恢复
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(400)
                        .setInterpolator(OvershootInterpolator(3f))
                        .start()
                    v.performClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    // 取消 - 恢复
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(2f))
                        .start()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 涟漪扩散效果
     * 在点击位置创建扩散的涟漪动画
     */
    fun applyRippleEffect(view: View) {
        view.isClickable = true
        view.isFocusable = true
        // Material Design 自带的涟漪效果已通过 XML ripple drawable 实现
        // 这里额外添加一个透明度脉冲效果
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.85f
                    v.animate().alpha(1f).setDuration(300).start()
                    false // 不消费事件，让涟漪效果正常触发
                }
                else -> false
            }
        }
    }

    /**
     * 为快捷按钮应用轻量特效
     */
    fun applyShortcutEffects(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .alpha(0.8f)
                        .setDuration(80)
                        .start()
                    false
                }
                MotionEvent.ACTION_UP -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(2f))
                        .start()
                    v.performClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(200)
                        .start()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 颜色偏移 - 用于流光动画
     * 通过色相偏移产生颜色流动效果
     */
    private fun shiftColor(color: Int, fraction: Float): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF

        // 使用正弦函数产生平滑的颜色变化
        val shift = Math.sin(fraction * Math.PI * 2) * 30
        val newR = ((r + shift).toInt().coerceIn(0, 255))
        val newG = ((g + shift * 0.7).toInt().coerceIn(0, 255))
        val newB = ((b - shift * 0.5).toInt().coerceIn(0, 255))

        return (0xFF shl 24) or (newR shl 16) or (newG shl 8) or newB
    }
}
