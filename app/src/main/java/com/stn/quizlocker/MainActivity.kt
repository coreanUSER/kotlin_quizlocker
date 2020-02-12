package com.stn.quizlocker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // preferenceContent FrameLayout 영역을 PreferenceFragment 로 교체
        supportFragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()

        // 버튼이 클릭되면 initAnswerCount() 함수 실행
        initButton.setOnClickListener { initAnswerCount() }
    }

    fun initAnswerCount() {
        // 정답횟수, 오답횟수 설정정보를 가져옴
        val correctAnswerPref = getSharedPreferences("correctAnswer", Context.MODE_PRIVATE)
        val wrongAnswerPref = getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE)

        // 초기화
        correctAnswerPref.edit().clear().apply()
        wrongAnswerPref.edit().clear().apply()
    }

    class MyPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // 환경설정 리소스 파일 적용
            addPreferencesFromResource(R.xml.pref)

            // 퀴즈 종류 요약정보에, 현재 선택된 항목을 보여주는 코드
            val categoryPref = findPreference("category") as MultiSelectListPreference
            categoryPref.summary = categoryPref.values.joinToString(", ")

            // 환경설정 정보값이 변경될때에도 요약정보를 변경하도록 리스너 등록
            categoryPref.setOnPreferenceChangeListener { preference, newValue ->
                // newValue 파라미터가 HashSet 으로 캐스팅이 실패하면 리턴
                val newValueSet = newValue as? HashSet<*>
                    ?: return@setOnPreferenceChangeListener true

                // 선택된 퀴즈종류로 요약정보 보여줌
                categoryPref.summary = newValue.joinToString(", ")

                true
            }

            // 퀴즈 잠금화면 사용 스위치 객체 가져옴
            val useLockScreenPref = findPreference("useLockScreen") as SwitchPreference

            // 클릭되었을때의 이벤트 리스너 코드 작성
            useLockScreenPref.setOnPreferenceClickListener {
                when {
                    // 퀴즈 잠금화면 사용이 체크된 경우 LockScreenService 실행
                    useLockScreenPref.isChecked -> activity?.startService(Intent(activity, LockScreenService::class.java))

                    // 퀴즈 잠금화면 사용이 체크 해제된 경우 LockScreenService 중단
                    else -> activity?.stopService(Intent(activity, LockScreenService::class.java))
                }
                true
            }

            // 앱이 시작되었을 때 이미 퀴즈잠금화면 사용이 체크되어있으면 서비스 실행
            if (useLockScreenPref.isChecked) {
                activity?.startService(Intent(activity, LockScreenService::class.java))
            }
        }
    }
}
