package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.myapplication.add.ApplyRemark
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import kotlinx.android.synthetic.main.fragment_second.*

class NewFriendActivity : AppCompatActivity() {
    private lateinit var newFriendEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friend)

        newFriendEditText = findViewById(R.id.new_friend)
        val newFriendBtn: Button = findViewById(R.id.newFriend_button)
        val dbHelper= MySqliteHelper(this,"chatHub",2)

        val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")
        var flag:Boolean=false
        var flag1:Boolean=false
        var flag2:Boolean=false

        val line = findViewById<LinearLayout>(R.id.line)
        val newFriend_nikeName = findViewById<TextView>(R.id.newFriend_nikeName)
        val user_photo = findViewById<ImageView>(R.id.user_photo)
        val line2 = findViewById<LinearLayout>(R.id.line2)
        val newFriend_nikeName2 = findViewById<TextView>(R.id.newFriend_nikeName2)
        val user_photo2 = findViewById<ImageView>(R.id.user_photo2)
        val line3 = findViewById<LinearLayout>(R.id.line3)
        val newFriend_nikeName3 = findViewById<TextView>(R.id.newFriend_nikeName3)
        val user_photo3 = findViewById<ImageView>(R.id.user_photo3)
        val line4 = findViewById<LinearLayout>(R.id.line4)
        val newFriend_nikeName4 = findViewById<TextView>(R.id.newFriend_nikeName4)
        val user_photo4 = findViewById<ImageView>(R.id.user_photo4)
        val absent: TextView = findViewById(R.id.absent);

        newFriendBtn.setOnClickListener {
           var find = findUser(newFriendEditText.text.toString().trim())
            if(find){
                val db=dbHelper.writableDatabase
                val cursor = db.query("user_friend",null,null,null,null,null,null)
                if(cursor.moveToFirst())
                {
                    do{

                        val userName = cursor.getString(cursor.getColumnIndex("userId"))
                        if(current_username==userName){
                            val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                            val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                            if(friendName==newFriendEditText.text.toString().trim() && current_username != newFriendEditText.text.toString().trim() && is_delete == 0){
                                //对方向我发送了好友申请
                                flag=true
                            }
                        }
                    }while (cursor.moveToNext())
                }

//                val user: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
//                var current_username = user.getString("username", "")
                //Toast.makeText(this,current_username, Toast.LENGTH_SHORT).show()
                if(!flag){
                    //进这里说明对方没有向我发送好友申请
                    if(current_username!=newFriendEditText.text.toString().trim()){

                        if(cursor.moveToFirst())
                        {
                            do{
                                //判断我是否向对方发送好友申请
                                val userName = cursor.getString(cursor.getColumnIndex("friendId"))
                                val friendName = cursor.getString(cursor.getColumnIndex("userId"))
                                val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                                if(current_username==userName && friendName==newFriendEditText.text.toString().trim() && is_delete==0){
                                    //我发了
                                    flag1=true
                                    break
                                }

                            }while (cursor.moveToNext())
                            if(!flag1){
                                //添加到通讯录
                                line.visibility = View.GONE
                                line2.visibility = View.GONE
                                line4.visibility = View.GONE
                                absent.setText("");
                                line3.visibility = View.VISIBLE
                                newFriend_nikeName3.setText(newFriendEditText.text.toString())
                                user_photo3.setImageResource(R.drawable.touxian)
                            }else{
                                //等待验证
                                flag1=false
                                line.visibility = View.GONE
                                line2.visibility = View.GONE
                                line3.visibility = View.GONE
                                absent.setText("");
                                line4.visibility = View.VISIBLE
                                newFriend_nikeName4.setText(newFriendEditText.text.toString())
                                user_photo4.setImageResource(R.drawable.touxian)
                            }
                        }else{
                            //添加到通讯录
                            line.visibility = View.GONE
                            line2.visibility = View.GONE
                            line4.visibility = View.GONE
                            absent.setText("");
                            line3.visibility = View.VISIBLE
                            newFriend_nikeName3.setText(newFriendEditText.text.toString())
                            user_photo3.setImageResource(R.drawable.touxian)
                        }
                        cursor.close()
                    }else{
                        line.visibility = View.GONE
                        line2.visibility = View.GONE
                        line3.visibility = View.GONE
                        line4.visibility = View.GONE
                        absent.setText("");
                        Toast.makeText(this,"不能添加自己为好友", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    flag = false

                    if(cursor.moveToFirst()) {
                        do {
                            val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                            val userName = cursor.getString(cursor.getColumnIndex("userId"))
                            val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                            if (newFriendEditText.text.toString().trim()== userName && friendName == current_username && is_delete == 0) {
                                flag2 = true
                                break
                            }
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    //前面已经确保对方向我发好友验证了，接下来要判断我是否有同意过，如果同意过，那么flag2就是true
                    //比如小王向小陈发送好友验证申请，那么表中就有一条记录userId是我小陈，friendId是对方小王，如果进到此处方法体，
                    //说明数据库已经有这条记录了，还要进一步判断是否有一条记录userId是对方小王，friendId的是我小陈，如果有flag2为true，否则为false
                    if(flag2){
                        flag2 = false
                        Toast.makeText(this,"已添加该好友，不能重复添加", Toast.LENGTH_SHORT).show()

                        line2.visibility = View.GONE
                        line3.visibility = View.GONE
                        line4.visibility = View.GONE
                        absent.setText("");

                        //发消息
                        line.visibility = View.VISIBLE
                        newFriend_nikeName.setText(newFriendEditText.text.toString())
                        user_photo.setImageResource(R.drawable.touxian)
                    }else{
                        Toast.makeText(this,"对方已向你发送添加好友请求，请前去同意！！！", Toast.LENGTH_SHORT).show()

                        line.visibility = View.GONE
                        line3.visibility = View.GONE
                        line4.visibility = View.GONE
                        absent.setText("");

                        line2.visibility = View.VISIBLE
                        newFriend_nikeName2.setText(newFriendEditText.text.toString())
                        user_photo2.setImageResource(R.drawable.wuming)
                    }
                }
            }else{
                line.visibility = View.GONE
                line2.visibility = View.GONE
                line3.visibility = View.GONE
                line4.visibility = View.GONE
                absent.setText("用户不存在");
               // Toast.makeText(this,"不存在该用户", Toast.LENGTH_SHORT).show()
            }
        }

        val go_sendMessage: Button = findViewById(R.id.go_sendMessage)
        go_sendMessage.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("friend_name",newFriendEditText.text.toString()); //备注
            intent.putExtra("nichen",newFriendEditText.text.toString());//真实姓名
            intent.putExtra("onlyId",0);
            intent.putExtra("friend_type",0);
            intent.putExtra("requestCode", 2)
            intent.putExtra("is_delete",0);

            startActivityForResult(intent,2);
        }
        val go_agree: Button = findViewById(R.id.go_agree)
        go_agree.setOnClickListener{
            val intent = Intent(this, HasNewFriendActivity::class.java)
            startActivityForResult(intent,2);
        }
        val apply: Button = findViewById(R.id.apply)
        apply.setOnClickListener{
            val intent = Intent(this, ApplyRemark::class.java)
            intent.putExtra("userId",newFriendEditText.text.toString());
            intent.putExtra("friendId",current_username )
            startActivityForResult(intent,1);
        }
    }

    fun findUser(username:String):Boolean{
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user",null,null,null,null,null,null)
        if(cursor.moveToFirst())
        {
            do{
                val name = cursor.getString(cursor.getColumnIndex("username"))
                if(username==name)
                {
                    return true;
                }
            }while (cursor.moveToNext())
        }
        cursor.close()
        return false
    }

}
