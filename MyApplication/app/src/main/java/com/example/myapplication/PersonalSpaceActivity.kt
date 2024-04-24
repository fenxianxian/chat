package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService



class PersonalSpaceActivity : AppCompatActivity() {

    // 声明 LinearLayout 组件
    private lateinit var expandLayout: LinearLayout
    private lateinit var collapseLayout: LinearLayout
    // 声明 ImageView 组件
    private lateinit var arrowImageView: ImageView

    private var isExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_space)

        // 获取 LinearLayout 组件实例
        expandLayout = findViewById(R.id.expand_layout)
        collapseLayout = findViewById(R.id.collapse_layout)

        // 获取 ImageView 组件实例
        arrowImageView = findViewById(R.id.arrowImage)

        // 设置箭头图标的点击事件
        arrowImageView.setOnClickListener {
            try {
                // 切换折叠和展开状态
                if (isExpanded) {
                    expandLayout.visibility = View.GONE
                    collapseLayout.visibility = View.VISIBLE
                    rotateArrow(0f, 180f) // 旋转箭头图标
                } else {
                    expandLayout.visibility = View.VISIBLE
                    collapseLayout.visibility = View.GONE
                    rotateArrow(180f, 0f) // 旋转箭头图标
                }
                isExpanded = !isExpanded // 更新标记值
            } catch (e: Exception) {
                Log.e("ERROR", "Error message: ${e.message}", e)
            }
        }
    }

    // 旋转箭头图标
    private fun rotateArrow(from: Float, to: Float) {
        val rotate = RotateAnimation(
            from,
            to,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        arrowImageView.startAnimation(rotate)
    }
}
