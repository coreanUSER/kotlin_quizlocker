package com.stn.quizlocker

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz_locker.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class QuizLockerActivity : AppCompatActivity() {

    var quiz:JSONObject? = null

    // 정답횟수 저장 SharedPreference
    val wrongAnswerPref by lazy { getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE) }
    val correctAnswerPref by lazy { getSharedPreferences("correctAnswer", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_locker)

        // 잠금화면보다 상단에 위치하기 위한 설정 조정. 버전별로 사용법이 다르기 때문에 버전에 따라 적용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            // 잠금화면에서 보여지도록 설정
            setShowWhenLocked(true)
            // 잠금 해제
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            // 잠금화면에서 보여지도록 설정
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            // 기본 잠금화면을 해제
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
        // 화면을 켜진 상태로 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_quiz_locker)

        // 퀴즈 데이터를 가져옴
        // 에셋(assets) 저장소는 '리소스'처럼 정적인 데이트를 관리하기 위해 사용하는데, '에셋'이 '리소스'와 다른점은
        // 정적 데이터를 '반환하지 않고 그대로' 가지고 있다는 것이다.
        // 반면 다른 리소스들은 성능을 위해 안드로이드가 자체적으로 '바이너리'형태로 변환한다.
        val json = assets.open("capital.json").reader().readText()
        val quizArray = JSONArray(json)

//        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
//        val category = pref.getStringSet("category", null)

        // 퀴즈를 선택한다.
        quiz = quizArray.getJSONObject(Random().nextInt(quizArray.length()))

        // 퀴즈를 보여준다.
        quizLabel.text = quiz?.getString("question")
        choice1.text = quiz?.getString("choice1")
        choice2.text = quiz?.getString("choice2")

        // 정답 / 오답 회수를 보여준다.
        val id = quiz?.getInt("id").toString() ?: ""
        correctCountLabel.text = "정답횟수: ${correctAnswerPref.getInt(id, 0)}"
        wrongCountLabel.text = "오답횟수: ${wrongAnswerPref.getInt(id, 0)}"

        // SeekBar 의 값이 변경될 때 불리는 리스너
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when {
                    // SeekBar 의 우측 끝으로 가면 choice2 를 선택한 것으로 간주
                    progress > 95 -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        // 우측 이미지뷰의 자물쇠 아이콘을 열림 아이콘으로 변경
                        rightImageView.setImageResource(R.drawable.unlock)
                    }
                    progress < 5 -> {
                        // 좌측 이미지뷰의 자물쇠 아이콘을 열림 아이콘으로 변경
                        leftImageView.setImageResource(R.drawable.unlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                    // 양쪽 끝이 아닌 경우
                    else -> {
                        // 양쪽 이미지를 모두 담금 아이콘으로 변경
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val process = seekBar?.progress ?: 50
                when {
                    // 우측 끝의 답을 선택한 경우
                    process > 95 -> checkChoice(quiz?.getString("choice2") ?: "")
                    // 좌측 끝의 답을 선택한 경우
                    process < 5 -> checkChoice(quiz?.getString("choice1") ?: "")
                    // 양끝이 아닌 경우 seekBar 의 Progress 를 중앙값으로 초기화
                    else -> seekBar?.progress = 50
                }
            }

        })
    }

    // 정답 체크 함수
    fun checkChoice(choice: String) {
        quiz?.let {
            when {
                // choice 의 텍스트가 정답 텍스트와 같으면 Activity 종료
                choice == it.getString("answer") -> {
                    // 정답인 경우 정답횟수 증가
                    val id = it.getInt("id").toString()
                    var count = correctAnswerPref.getInt(id, 0)
                    count++
                    correctAnswerPref.edit().putInt(id, count).apply()
                    correctCountLabel.text = "정답횟수: ${count}"

                    // Activity 종료
                    finish()
                }
                else -> {
                    // 오답 횟수 증가
                    val id = it.getInt("id").toString()
                    var count = wrongAnswerPref.getInt(id, 0)
                    count++
                    wrongAnswerPref.edit().putInt(id, count).apply()
                    wrongCountLabel.text = "오답횟수: ${count}"

                    // 정답이 아닌경우 UI 초기화
                    leftImageView.setImageResource(R.drawable.padlock)
                    rightImageView.setImageResource(R.drawable.padlock)
                    seekBar?.progress = 50

                    // 정답이 아닌 경우 진동알림 추가
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    // SDK 버전에 따라 호출
                    if (Build.VERSION.SDK_INT >= 26) {
                        // 1초동안 100의 세기(최고 255)로 1회 진동
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, 255))
                    } else {
                        // 1초동안 진동
                        vibrator.vibrate(1000)
                    }
                }
            }
        }
    }
}
