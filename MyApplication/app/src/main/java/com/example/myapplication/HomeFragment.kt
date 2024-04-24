package com.example.myapplication
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.MyPagerAdapter
import com.example.myapplication.R


import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_home_fragment, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // 创建适配器并将ViewPager和TabLayout关联起来
        val myPagerAdapter = MyPagerAdapter(childFragmentManager)
        myPagerAdapter.addFragment(FirstFragment(), "推荐")
//        myPagerAdapter.addFragment(SecondFragment(), "同城")
        myPagerAdapter.addFragment(ThreeFragment(), "关注")
        viewPager.adapter = myPagerAdapter  //它将Fragment与ViewPager进行关联。
        tabLayout.setupWithViewPager(viewPager) //将ViewPager与TabLayout进行关联，实现了页面的切换和Tab标题的显示
//系统会自动根据PagerAdapter中的getPageTitle方法返回的标题来创建相应的Tab，
// 并将其与ViewPager进行关联。这样，当用户滑动ViewPager或点击Tab时，ViewPager会自动切换到对应的页面。
        return view
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //home.text = "This is Home Fragment"
//        button_two.setOnClickListener{
//            home.text = "This is Home Fragment"
//        }

    }



}
