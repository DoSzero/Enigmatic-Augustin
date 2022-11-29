package com.rubygames.assass.logic.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rubygames.assass.R
import com.rubygames.assass.databinding.ActivityMenuBinding


class MenuActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =  DataBindingUtil.setContentView(this, R.layout.activity_menu)
        binding.startButton.setOnClickListener {
            val intent = Intent(this, GMainActivity::class.java)
            startActivity(intent)
        }
    }
}
