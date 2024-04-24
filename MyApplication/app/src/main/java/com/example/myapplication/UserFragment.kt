package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.edit.EditMessageActivity
import com.example.myapplication.weather.WeathBroadActivity
import kotlinx.android.synthetic.main.activity_user_fragment.*


class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_user_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        user.text = "This is User Fragment"
        val userPrefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        current_name.text=currentUsername
        new_friend.setOnClickListener {
            val intent = Intent(getActivity(), NewFriendActivity::class.java)
            startActivity(intent);
        }
        small_tool.setOnClickListener {
            val intent = Intent(getActivity(), EditMessageActivity::class.java)
            startActivity(intent);
        }
        setting.setOnClickListener {
            val intent = Intent(getActivity(), SettingActivity::class.java)
            startActivity(intent);
        }
        current_name.setOnClickListener {
            val intent = Intent(getActivity(), PersonalSpaceActivity::class.java)
            startActivity(intent);
        }
        posting_updates.setOnClickListener {
            val intent = Intent(getActivity(), PostingUpdates::class.java)
            startActivity(intent);
        }
        group_chat.setOnClickListener {
            val intent = Intent(getActivity(), GroupChat::class.java)
            startActivity(intent);
        }
    }
}
