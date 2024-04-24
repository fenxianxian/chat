package com.example.myapplication.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.myapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.concurrent.thread

class EditMessageActivity : AppCompatActivity() {

    var yearNumber = 2000;
    var monthNumber = 1;
    var dayNumber = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_message)

        val tv_year : TextView = findViewById(R.id.tv_year)
        val btn_up1 : Button = findViewById(R.id.btn_up1)
        val tv_month : TextView = findViewById(R.id.month)
        val btn_up2 : Button = findViewById(R.id.btn_up2)
        val tv_day : TextView = findViewById(R.id.day)
        val btn_up3 : Button = findViewById(R.id.btn_up3)
        val btn_down1 : Button = findViewById(R.id.btn_down1)
        val btn_down2 : Button = findViewById(R.id.btn_down2)
        val btn_down3 : Button = findViewById(R.id.btn_down3)


        btn_up1.setOnClickListener{
            yearNumber = yearNumber + 1
            tv_year.setText(Integer.toString(yearNumber))
            if(monthNumber == 2){
                if(yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0)) //闰年
                {
                    if(dayNumber>29){
                        dayNumber = 29
                    }
                }else{
                    if(dayNumber>28){
                        dayNumber = 28
                    }
                }
            }
            tv_day.setText(Integer.toString(dayNumber))
        }
        btn_down1.setOnClickListener{
            yearNumber = yearNumber - 1
            tv_year.setText(Integer.toString(yearNumber))
            if(monthNumber == 2){
                if(yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0)) //闰年
                {
                    if(dayNumber>29){
                        dayNumber = 29
                    }
                }else{
                    if(dayNumber>28){
                        dayNumber = 28
                    }
                }
            }
            tv_day.setText(Integer.toString(dayNumber))
        }


        btn_up2.setOnClickListener{
            if(monthNumber == 12){
                monthNumber = 1;
            }else{
                monthNumber = monthNumber + 1
            }
            if(monthNumber==4 || monthNumber==6 || monthNumber==9 || monthNumber==11)
            {
                if(dayNumber==31){
                    dayNumber = 30
                }
            }
            if(monthNumber == 2){
                if((yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0))) //闰年
                {
                    if(dayNumber>29){
                        dayNumber = 29
                    }
                }else{
                    if(dayNumber>28){
                        dayNumber = 28
                    }
                }
            }
            tv_month.setText(Integer.toString(monthNumber))
            tv_day.setText(Integer.toString(dayNumber))
        }
        btn_down2.setOnClickListener{
            if(monthNumber == 1){
                monthNumber = 12;
            }else{
                monthNumber = monthNumber - 1
            }
            if(monthNumber==4 || monthNumber==6 || monthNumber==9 || monthNumber==11)
            {
                if(dayNumber==31){
                    dayNumber = 30
                }
            }
            if(monthNumber == 2){
                if((yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0))) //闰年
                {
                    if(dayNumber>29){
                        dayNumber = 29
                    }
                }else{
                    if(dayNumber>28){
                        dayNumber = 28
                    }
                }
            }
            tv_month.setText(Integer.toString(monthNumber))
            tv_day.setText(Integer.toString(dayNumber))
        }



        btn_up3.setOnClickListener{
            //            1.每个月31天的有1月、3月、5月、7月、8月、10月、12月，一共是七个月；
//            2.每月30天的有4月、6月、9月、11月共四个月；
//            2.2月是平月（二十八天）或者是闰月（二十九天）。
            //如果传进来是28，判断与之选择的月份是不是2月
            var flag:Boolean = true
            if(dayNumber==31){
                dayNumber = 1
                flag = false
            }
            if(dayNumber==30){
                if(monthNumber==1 || monthNumber==3 || monthNumber==5 || monthNumber==7 || monthNumber==8 || monthNumber==10 || monthNumber==12)
                {
                    dayNumber = dayNumber + 1
                }else{
                    dayNumber = 1
                    flag = false
                }
            }
            if(dayNumber==29){
                if(monthNumber == 2){
                    //判断是闰年还是平年，是闰年给它加1，不是闰年给它置为1
                    if(yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0)) //闰年
                    {
                        dayNumber = 1
                        flag = false
                    }
                }else{
                    dayNumber = dayNumber+1
                }
            }
            if(dayNumber==28){
                if(monthNumber == 2){
                    //判断是闰年还是平年，是闰年给它加1，不是闰年给它置为1
                    if(yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0)) //闰年
                    {
                        dayNumber = dayNumber + 1
                    } else { //平年
                        dayNumber = 1
                        flag = false
                    }
                }else{
                    dayNumber = dayNumber + 1
                }
            }
            if(flag){
                if(dayNumber<28){
                    dayNumber = dayNumber + 1
                }
            }
            tv_day.setText( dayNumber.toString())
        }
        btn_down3.setOnClickListener{
            if(dayNumber==1){
                if(monthNumber==1 || monthNumber==3 || monthNumber==5 || monthNumber==7 || monthNumber==8 || monthNumber==10 || monthNumber==12)
                {
                    dayNumber = 31
                }
                if(monthNumber==4 || monthNumber==6 || monthNumber==9 || monthNumber==11)
                {
                    dayNumber = 30
                }
                if(monthNumber == 2){
                    if(yearNumber%400==0||(yearNumber%4==0&&yearNumber%100!=0)) //闰年
                    {
                        dayNumber = 29
                    } else { //平年
                        dayNumber = 28
                    }
                }
            }else{
                dayNumber = dayNumber - 1
            }
            tv_day.setText(Integer.toString(dayNumber))
        }

    }

}
