package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.adapter.FriendListAdapter
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.adapter.UserAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend


class FriendFragment : Fragment() {

    private lateinit var lv_user: ListView
    val friendlist=ArrayList<Friend>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_friend_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lv_user = view.findViewById(R.id.lv_user)
        //定义数据源
        initData()
        //定义一个适配器
        val friend_adapter = FriendListAdapter(requireActivity(),R.layout.user_list_table,friendlist)
        //给控件加适配器
        lv_user.adapter=friend_adapter


        val friend_say = view.findViewById<LinearLayout>(R.id.friend_say)
        friend_say.setOnClickListener {
            val intent = Intent(context,FriendSay::class.java)
            startActivity(intent)
        }

        lv_user.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // 获取选定位置的数据对象，将其转换为 User 类型
            val friend = lv_user.adapter.getItem(position) as? Friend
            // var hasHint = friend?.hasHint ?: false

//            var hasHint = friend?.hasHint ?: false
//            if (!hasHint) {
//               //hint.visibility = View.VISIBLE
//                Log.d("aa",friend?.hasHint.toString())
//                friend?.hasHint = true
//            }
            if (friend != null) {
                // 访问该用户的属性
                val userName = friend.friendRemarks
                val userNichen = friend.friendId
                val friendType = friend.friendType
                val onlyId = friend.onlyId
                val is_delete = friend.is_delete
                val intent = Intent(context,UserSpace::class.java)
                //Toast.makeText(context,onlyId.toString(),Toast.LENGTH_SHORT).show()
                intent.putExtra("userNichen",userNichen)
                intent.putExtra("userName",userName)
                intent.putExtra("is_delete", is_delete)
                intent.putExtra("friendType",friendType)
                intent.putExtra("onlyId",onlyId);
                startActivity(intent)
            }
        }

    }
    fun initData() {
        val dbHelper= MySqliteHelper(requireActivity(),"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user_friend",null,null,null,null,null,null)
//        val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        var current_username = user.getString("username", "")

        val userPrefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        if(cursor.moveToFirst())
        {
            do{
                val id = cursor.getColumnIndex("id")
                val userName = cursor.getString(cursor.getColumnIndex("userId"))
                if(currentUsername==userName){

                    val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                    if(friendName != currentUsername){
                        val agree = cursor.getInt(cursor.getColumnIndex("agree"))
                        val friendType = cursor.getInt(cursor.getColumnIndex("friendType"))
                        val onlyId = cursor.getInt(cursor.getColumnIndex("onlyId"))
                        val friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                        val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))

                        if(agree==1 && is_delete == 0){
                            friendlist.add(
                                Friend(
                                    id,userName,friendName,"",R.drawable.xiaoai,friendType,onlyId,friendRemarks,is_delete,false
                                )
                            );
                        }
                    }

                }
            }while (cursor.moveToNext())
        }
        cursor.close()
    }

}
