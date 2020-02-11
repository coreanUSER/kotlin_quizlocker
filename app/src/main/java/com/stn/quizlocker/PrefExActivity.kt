package com.stn.quizlocker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pref_ex.*

// SharedPreference 를 사용하면 앱 데이터를 'Key - Value' 형태로 저장할 수 있다.
class PrefExActivity : AppCompatActivity() {

    // nameField 의 데이터를 저장할 Key
    val nameFieldKey = "nameField"

    // pushCheckBox 의 데이터를 저장할 Key
    val pushCheckBoxKey = "pushCheckBox"

    // shared preference 객체, Activity 초기화 이후에 사용해야 하기 때문에 lazy 위임을 사용
    // SharedPreference 객체를 가져오려면 Activity 의 'getSharedPreference()' 함수를 사용해야함
    // SharedPreference 객체를 가져오는 방법은 3가지이다.
    // 1. Activity.getPreference(): Activity 별 Preference 객체를 반환
    // 2. Context.getSharedPreference(): 파라미터로 전달받은 일므의 Preference 객체를 반환함, 앱의 모든 컨텍스트에서 호출 가능
    // 3. PreferenceManager.getDefaultSharedPreference(): 앱 기본 환경 설정 객체를 반환

    // 'getPreference()' 함수는 Activity 마다 다른 Preference 객체를 반환하고,
    // 'getSharedPreference()' 함수는 전달받은 '이름'으로 Preference 객체를 반환한다는 것이 차이점
    // 환경 설정값은 보통 여러 Activity 에서 참조하는 경우가 많으므로
    // getPreference() 함수보다는 이름으로 서로 공유할 수 있는 getSharedPreference() 함수가 더 많이 쓰임
    val preference by lazy { getSharedPreferences("PrefExActivity", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_ex)

        // 저장 버튼이 클릭된 경우
        saveButton.setOnClickListener {
            // SharedPreference 에서 nameFieldKey 값으로 nameField 의 현재 텍스트를 저장
            // SharedPreference 로 데이터를 저장하기 위해서는 'SharedPreference.Editor' 객체를 사용
            // 이 Editor 객체는 'SharedPreference.edit()'함수를 사용해 가져올 수 있다.
            preference.edit().putString(nameFieldKey, nameField.text.toString()).apply()

            // SharedPreference 에서 pushCheckBoxKey 값으로 nameField 의 현재 텍스트를 저장
            preference.edit().putBoolean(pushCheckBoxKey, pushCheckBox.isChecked).apply()
        }

        // 불러오기 버튼이 클릭된 경우
        loadButton.setOnClickListener {
            // SharedPreference 에서 nameFieldKey 로 저장된 문자열을 불러와 nameField 의 텍스트로 설정
            // SharedPreference 객체로 데이터를 다시 불러올 때는
            // preference.getString("Key", "Default Value")
            nameField.setText(preference.getString(nameFieldKey, ""))

            // SharedPreference 에서 nameFieldKey 로 저장된 문자열을 불러와 nameField 의 텍스트로 설정
            pushCheckBox.isChecked = preference.getBoolean(pushCheckBoxKey, false)
        }

    }
}
