package com.example.nokilllouisvilleandroidphoneapp

import Acuity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class AcuitySchedule : AppCompatActivity() {


    private val acuity = Acuity()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acuity)

        val gfgPolicy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(gfgPolicy)


        val scheduleBtn = findViewById<Button>(R.id.sched_button)


        val appListBtn = findViewById<Button>(R.id.appList_button)
        val appList = findViewById<ListView>(R.id.appList)

        val appButton = findViewById<Button>(R.id.app_button)
        val emailPhoneText = findViewById<TextView>(R.id.editEmailPhone)


        scheduleBtn.setOnClickListener{
            val nklWebIntent = Intent(this, NKL_Scheduling_Activity::class.java)
            startActivity(nklWebIntent)
        }



        appListBtn.setOnClickListener {
            val date = Calendar.getInstance()
            val list = acuity.GetAppointmentList(date)

            val list2 = Array(list.size){i -> acuity.toString(list[i])}
            val appAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list2)
            appList.adapter = appAdapter

        }

        appButton.setOnClickListener {
            val date = Calendar.getInstance()
            val emailPhone = emailPhoneText.text.toString()
            val app = acuity.GetAppointment(date, emailPhone)
            val list = Array(1){acuity.toString(app)}

            val appAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            appList.adapter = appAdapter
        }


    }


}
