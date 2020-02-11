package com.stn.quizlocker

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ScreenOffExActivity : AppCompatActivity() {
    // ScreenOffReceiver 객체
    var screenOffReceiver: ScreenOffReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_off_ex)

        // screenOffReceiver 가 널인 경우에만 screenOffReceiver 를 생성하고 등록
        if (screenOffReceiver == null) {
            screenOffReceiver = ScreenOffReceiver()
            val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            // 'registerReceiver()' 함수는 브로드캐스트 리시버를 런타임에 등록하는 역활을 하는 함수
            // 첫번째 파라미터: 브로드케스트 리시버를 상속받은 객체
            // 두번째 파라미터: 메세지를 필터링 할 IntentFilter
            registerReceiver(screenOffReceiver, intentFilter)
        }
    }
}
