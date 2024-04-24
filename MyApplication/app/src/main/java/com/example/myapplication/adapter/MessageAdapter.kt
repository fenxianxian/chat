package com.example.myapplication.adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class MessageAdapter(activity: Activity, val resId:Int, data:List<Friend>):ArrayAdapter<Friend>(activity,resId,data){

    val dbHelper= MySqliteHelper(activity,"chatHub",2)
    val db=dbHelper.writableDatabase
    var saveTime:Long=0L
    var saveTime1:Long=0L
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val userImage:ImageView=myview.findViewById(R.id.user_photo)
        val user_nikeName:TextView=myview.findViewById(R.id.user_nikeName)
        val user_message:TextView = myview.findViewById(R.id.user_message)
        val sendTime:TextView = myview.findViewById(R.id.send_time)
      //  val Only_id:TextView=myview.findViewById(R.id.Only_id)
        //为每一个控件加载数据
        val friend:Friend? = getItem(position) //获取每一个控件的位置


//        val hintView = myview.findViewById<ImageView>(R.id.hint)
//        hintView.visibility = View.VISIBLE

        val context = parent.context
        val user: SharedPreferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")
        val selection = "sender = ? AND receiver = ?  AND s_show = ? AND onlyId = ? ORDER BY triggered_at DESC LIMIT 1"
        val selectionArgs = arrayOf(current_username, friend?.friendId,"0",friend?.onlyId.toString())
        lateinit var cursor1: Cursor
        var message_content:String=""
        if(friend?.is_delete==0){
            if(friend.friendType==1){
                //说明是群聊
                //Toast.makeText(context,friend.onlyId.toString(),Toast.LENGTH_SHORT).show()
                val selection2 = "sender != ? AND receiver = ?  AND r_show = ? AND onlyId = ? ORDER BY triggered_at DESC LIMIT 1"
                val selectionArgs2 = arrayOf(current_username, friend.friendId,"0",friend.onlyId.toString())
                cursor1 = db.query("chat_history",null,selection2,selectionArgs2,null,null,null)
            }else if(friend.friendType==0){
                //说明是单聊
                val selection1 = "sender = ? AND receiver = ?  AND r_show = ? ORDER BY triggered_at DESC LIMIT 1"
                val selectionArgs1 = arrayOf(friend.friendId, current_username,"0")
                cursor1 = db.query("chat_history",null,selection1,selectionArgs1,null,null,null)
            }
            val cursor = db.query("chat_history",null,selection,selectionArgs,null,null,null)

            var send_time:Long=0

            var interact = false
            var friend_Type = false
            if(cursor.moveToFirst()&&cursor1.moveToFirst())
            {
                val time = cursor.getInt(cursor.getColumnIndex("triggered_at"))
                val time1 = cursor1.getInt(cursor1.getColumnIndex("triggered_at"))
//            val s_show =cursor.getInt(cursor.getColumnIndex("s_show"))
//            val r_show =cursor1.getInt(cursor1.getColumnIndex("r_show"))
                val timeDifference = time - time1

                if (timeDifference > 0) {
                    if(cursor.moveToFirst())
                    {
                        message_content = cursor.getString(cursor.getColumnIndex("message_content"))
                        send_time = cursor.getLong(cursor.getColumnIndex("triggered_at"))
                    }
                }
                if (timeDifference == 0) {
                    if(cursor1.moveToFirst())
                    {
                        message_content = ""
                    }
                }
                if (timeDifference < 0) {
                    if(cursor1.moveToFirst())
                    {
                        // 获取当前列表项所对应的数据对象
                        // val friend = getItem(position)
                        //加红点
                        message_content = cursor1.getString(cursor1.getColumnIndex("message_content"))
                        send_time = cursor1.getLong(cursor1.getColumnIndex("triggered_at"))
                        var letter = cursor1.getInt(cursor1.getColumnIndex("letter"))
                        if (letter==1){
                            val hintView = myview.findViewById<ImageView>(R.id.hint)
                            hintView.visibility = View.VISIBLE
                        }
                        // 查找视图中的 ImageView
//                    val hintView = myview.findViewById<ImageView>(R.id.hint)
//                    hintView.visibility = View.VISIBLE

                        // Log.d("A",(saveTime==send_time).toString())
                        // 根据数据对象的 hasHint 属性来决定是否显示小红点
//                    hintView.visibility = if(saveTime==send_time){
//                        View.GONE
//                    }else{
//                        View.VISIBLE
//                    }

                        // saveTime = send_time
                        // 根据数据对象的 hasHint 属性来决定是否显示小红点
                        //  var hasHint = friend?.hasHint ?: false  //如果 friend 不为 null，则返回 friend.hasHint 的值，否则返回 false。
//                    if(saveTime1 == send_time){
//                        hasHint=false  //说明无新消息
//                    }else{
//                        hasHint=true   //说明有新消息
//                        saveTime1 = send_time
//                    }
//                    hasHint=true
//                    if (hasHint) {
//                        hintView.visibility = View.VISIBLE
//                    } else {
//                        hintView.visibility = View.GONE
//                    }

//                    saveTime = if( hintView.visibility== View.VISIBLE){
//                        send_time
//                    }else{
//                        saveTime
//                    }

//                    if(hintView.visibility == View.VISIBLE){
//                        hintView.visibility = View.GONE
//                    }else {
//                        hintView.visibility = View.VISIBLE
//                    }

//                    else View.GONE
                    }
                }
            }else if(cursor.moveToFirst()){
                message_content = cursor.getString(cursor.getColumnIndex("message_content"))
                send_time = cursor.getLong(cursor.getColumnIndex("triggered_at"))
            }else if(cursor1.moveToFirst()){
                message_content = cursor1.getString(cursor1.getColumnIndex("message_content"))
                send_time = cursor1.getLong(cursor1.getColumnIndex("triggered_at"))
                var letter = cursor1.getInt(cursor1.getColumnIndex("letter"))
                if (letter==1){
                    val hintView = myview.findViewById<ImageView>(R.id.hint)
                    hintView.visibility = View.VISIBLE
                }
            }
            cursor.close()
            cursor1.close()





                userImage.setImageResource(friend.headPhoto)
                if(friend.friendRemarks==null){
                    user_nikeName.text = friend.friendId
                }else{
                    user_nikeName.text = friend.friendRemarks
                }
                user_message.text = message_content
                //val timeString:String
                if(send_time!=0L){
                    // Log.d("aaaa","${send_time}");

                    // val currentTimeMillis = System.currentTimeMillis()

//                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:s")
//                sendTime.text= sdf.format(sendDate)

//                val sendDate = Date(send_time)
//                val sendCalendar = Calendar.getInstance()
//                sendCalendar.time = sendDate // 设置 Calendar 对象的时间
//                val currentCalendar = Calendar.getInstance() // 创建当前时间的 Calendar 对象
//                val diff = currentCalendar.timeInMillis - sendCalendar.timeInMillis // 计算两个时间的时间差

                    var timeString = formatTime(send_time)

                    //将结果赋值给需要显示时间的控件
                    sendTime.text = timeString
                }else if(send_time==0L)
                {

                    val db1=dbHelper.writableDatabase
                    val cursor3 = db1.query("user_friend",null,null,null,null,null,null)
                    if(cursor3.moveToFirst()) {
                        do {
                            val userId = cursor3.getString(cursor3.getColumnIndex("userId"))
                            if (current_username == userId) {
                                val friendId = cursor3.getString(cursor3.getColumnIndex("friendId"))
                                if(friendId == friend.friendId){
                                    var friendType = cursor3.getInt(cursor3.getColumnIndex("friendType"))
                                    var is_interact = cursor3.getInt(cursor3.getColumnIndex("is_interact"))
                                    if(is_interact==1){
                                        interact = true
                                        break
                                    }
                                    if(friendType==1){
                                        friend_Type = true
                                        break
                                    }
                                }
                            }
                        }while (cursor3.moveToNext())
                    }
                    cursor3.close()
                    //需要interact是因为要跟删除聊天记录区分开，证明你跟对方前有过交互
                    if(!interact){
                        if(friend_Type){
                            user_message.text = "[你已加入群："+friend.friendId+"]"
                        }else{
                            user_message.text = "[你已添加\""+friend.friendId+"\"为好友]"
                        }
                    }
                }

        }else if(friend?.is_delete==1){
            userImage.setImageResource(friend.headPhoto)
            if(friend.friendRemarks==null){
                user_nikeName.text = friend.friendId
            }else{
                user_nikeName.text = friend.friendRemarks
            }
            user_message.text = "你已被对方删除！！"
        }


        return myview
    }




    fun formatTime(send_time: Long): String {
        val sendDate = Date(send_time)
        // 当前时间的Calendar对象
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = System.currentTimeMillis()

        // 待解析时间的Calendar对象
        val sendCalendar = Calendar.getInstance()
        sendCalendar.timeInMillis = send_time

        //获取当前年份
        val sendYear = SimpleDateFormat("yyyy").format(sendCalendar.time)
        //获取当前月份
        val sendMonth = SimpleDateFormat("MM").format(sendCalendar.time)
        //获取当前日期
        val sendDate1 = SimpleDateFormat("dd").format(sendCalendar.time)

        // 计算时间差（毫秒为单位）
        val diff = currentCalendar.timeInMillis - sendCalendar.timeInMillis

        return when {
//            1秒等于1000毫秒，所以60秒等于60乘以1000毫秒。
            diff < 60 * 1000 -> {
                "刚刚"
            }
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"

            else -> {
                val currentYear = SimpleDateFormat("yyyy").format(currentCalendar.time)
                //获取当前月份
                val currentMonth = SimpleDateFormat("MM").format(currentCalendar.time)
                //获取当前日期
                val currentDate = SimpleDateFormat("dd").format(currentCalendar.time)

                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                var timeOfDay: String
                if(currentYear==sendYear && currentMonth==sendMonth && currentDate==sendDate1)
                {

                    val hour = SimpleDateFormat("HH", Locale.getDefault())
                    timeOfDay = when (hour.format(sendDate).toInt()) {
                        in 0..5 -> "凌晨"
                        in 6..11 -> "上午"
                        in 12..13 -> "中午"
                        in 14..18 -> "下午"
                        else -> "晚上"
                    }
                    "$timeOfDay ${format.format(sendDate)}"
                }else{
                    // 将当前年月日转换为整数
                    val year = sendYear.toInt()
                    val month = sendMonth.toInt()
                    val day = sendDate1.toInt()
                    // 使用 Calendar 进行日期计算
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month - 1, day) // 月份需要减去1，因为 Calendar 类中月份是从 0 开始的
                    calendar.add(Calendar.DAY_OF_MONTH, 1) // 添加一天，即获取下一个日期
                    val nextYear = calendar.get(Calendar.YEAR)
                    val nextMonth = calendar.get(Calendar.MONTH) + 1 // 月份是从 0 开始计数的，因此需要加 1
                    val nextDay = calendar.get(Calendar.DAY_OF_MONTH)
                    if(currentYear.toInt()==nextYear && currentMonth.toInt()==nextMonth && currentDate.toInt()==nextDay)
                    {

                        "昨天 ${format.format(sendDate)}"
                    }else{
                        val currentYearFormatted = SimpleDateFormat("yyyy").format(currentCalendar.time)
                        val sendYearFormatted = SimpleDateFormat("yyyy").format(sendDate)
                        val format1 = if (currentYearFormatted == sendYearFormatted) {
                            // 显示月日+时间
                            SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())
                        } else {
                            // 显示日期和时间
                            SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
                        }
                        format1.format(sendDate)
                    }

                }
            }
//            diff < 48 * 60 * 60 * 1000 -> "昨天"


        }
    }
}