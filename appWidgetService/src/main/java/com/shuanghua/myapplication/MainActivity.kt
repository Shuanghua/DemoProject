package com.shuanghua.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.activity_main)
        val resultValue = Intent()
        setResult(Activity.RESULT_OK, resultValue)
    }
}
