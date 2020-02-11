package com.stn.quizlocker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PrefFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_fregment)
        // Activity 의 컨텐트 뷰를 MyPrefFragment 로 교체
        supportFragmentManager.beginTransaction().replace(android.R.id.content, MyPrefFragment()).commit()
    }

    // PreferenceFragment: XML 로 작성한 Preference 를 UI 로 보여주는 클래스
    class MyPrefFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Preference 정보가 있는 XML 파일 지정
            addPreferencesFromResource(R.xml.ex_pref)
        }
    }
}

// XML 리소스로 공유 환경 설정 UI를 만들 때에는 PreferenceFragment 이외에도 활용할 수 있는 것들이 많다.
// 1. PreferenceScreen: Preference 기본 설정 계층 구조의 최상위 루트, 설정 그룹 여러개를 보조 화면에 배치하고자 하는 경우 PreferenceScreen 을 중첩하여 사용
// 2. PreferenceCategory: 여러 개의 설정 그룹 사이에 구분선과 제목을 제공하고자 하는 경우, 각 Preference 객체 그룹을 PreferenceCategory 내부에 배치
// 3. CheckBoxPreference: 활성화되었거나 비활성화된 설정에 대한 체크 박스가 있는 항목을 표시, 이 Preference 는 SharedPreferences 에 Boolean 을 저장
// 4. EditTextPreference: EditText 위젯이 있는 대화 상자를 염, 이 Preference 는 SharedPreferences 에 String 을 저장
// 5. ListPreference: 라디오 버튼 목록이 있는 대화상자를 염, SharedPreferences 에 저장되는 값은 Boolean, Float, Int, Long, String, String Set 중 어느 것이라도 될 수 있음
// 6. MultiSelectListPreference: 여러 개의 값이 선택 가능한 체크 박스 목록이 있는 대화 상자를 염, 이 Preference 는 SharedPreferences 에 String Set 을 저장
// 7. RingtonePreference: 사용자가 기기의 벨소리를 선택할 수 있는 Preference, 선택한 벨소리의 URI 는 String 으로 유지
// 8. SwitchPreference: 2가지 상태를 전환하는 옵션을 제공하는 Preference, 이 Preference 는 SharedPreferences 에 Boolean 을 저장
