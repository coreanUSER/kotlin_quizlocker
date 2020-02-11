package com.stn.quizlocker

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_file_ex.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.Manifest

// 안드로이드는 "앱데이터(App Data)"를 저장할 수 있는 다양한 방법을 지원한다.
// 1. File: File 시스템을 사용해 데이터를 저장한다. 앱의 독립된 내*외부 저장소에 저장할 수 있다.
//      - 앱 데이터를 저장하는 방법은 전통적인 파일 시스템을 이용해 데이터를 저장하는 방법
//      - '앱의 독립된 내부 저장소에 저장한다'는 의미는, 앱마다 서로 접근이 불가능한 독립된 공간이 있다고 생각하면 된다.

// 2. Shared Preference: 'Key - Value' 형태로 데이터를 저장한다. 앱의 독립된 내부 저장소에 저장된다.
//      - Key 에 따른 값을 저장하기 때문에 파일저장과 비교해 간편하게 사용가능

// 3. sqlite: 데이터베이스를 사용해 구조화된 데이터를 저장한다.
//      - 데이터베이스를 사용하면 데이터를 구조화하여 저장할 수 있게 되고, 데이터를 검색할 때 'SQL'을 사용할 수 있다.

// 4. network: 네트워크를 사용해 데이터를 백엔드 서버에 저장한다.

class FileExActivity : AppCompatActivity() {

    // 데이터 저장에 사용할 파일이름
    val filename = "data.txt"

    // 권한이 있는지 저장하는 변수
    var granted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_ex)

        // 외부저장소의 권한을 동적으로 체크하는 함수를 호출
        checkPermission()

        // 저장 버튼이 클릭된 경우
        saveButton.setOnClickListener {
            val text = textField.text.toString()

            when {
                // 텍스트가 비어있는 경우 오류 메시지를 보여줌
                TextUtils.isEmpty(text) -> {
                    Toast.makeText(applicationContext, "텍스트가 비어있습니다.", Toast.LENGTH_SHORT).show()
                }
                !isExternalStorageWritable() -> {
                    Toast.makeText(applicationContext, "외부 저장장치가 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 내부 저장소 파일에 저장하는 함수 호출
//                    saveToInnerStorage(text, filename)

                    // 외부 저장소의 파일에 저장하는 함수 호출
//                    saveToExternalStorage(text, filename)

                    // 외부저장소 "/sdcard/data.txt"에 데이터를 저장
                    saveToExternalCustomDirectory(text)
                }
            }
        }

        // 불러오기 버튼이 클릭된 경우
        loadButton.setOnClickListener {
            try {
                // textField 의 text 를 불러온 텍스트로 설정한다.
//                textField.setText(loadFromInnerStorage(filename))

                // 외부저장소 앱 전용 디렉토리의 파일에서 읽어온 데이터로 textField 의 텍스트를 설정
//                textField.setText(loadFromInnerStorage(filename))

                // 외부저장소 "/sdcard/data.txt"에서 데이터를 불러옴
                textField.setText(loadFromExternalCustomDirectory())
            } catch(e: FileNotFoundException) {
                // 파일이 없는 경우 에러메시지 송출
                Toast.makeText(applicationContext, "저장된 텍스트가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 내부저장소 파일에 텍스트를 저장한다.
    fun saveToInnerStorage(text: String, filename: String) {
        // 내부 저장소에 전달된 파일이름의 파일 출력 스트림을 가져온다.
        // 'fileOutputStream()' 메소드는 앱 전용 내부 저장소 경로에 파일명(filename)으로 생성된 '파일 출력 스트림(fileOutputStream)' 객체를 반환
        // * Stream: 시간 경과에 따른 연속적인 데이터의 흐름
        // * 스트림을 사용하면 데이터를 '연속적으로', 즉 '여러번에 걸처서' 저장하는 것이 가능하다. -> 큰 파일을 10번에 나눠서 저장과 같은 경우

        // openFileOutput()은 앱의 전용 디렉토리에서 파일 출력 스트림을 가져오는 함수
        // 앱 전용 디렉토리는 다른 앱들은 접근할 수 없는 저장소이다.
        // 안드로이드는 기본적으로 '리눅스 커널 기반 운영체제'이기 때문에, 파일 시스템이 리눅스와 동일하다.
        // 리눅스는 파일 권한을 '소유자', '그룹', '모든 사용자'로 나누어 각각에 대하여 읽기, 쓰기, 실행 권한을 관리한다.
        // 안드로이드는 앱이 설치될 때 앱의 새로운 사용자를 만들고 앱의 디렉토리를 사용자의 소유로 설정한다.
        // 즉, 앱마다 각각의 사용자가 있고, 각 생성된 사용자로 디렉토리의 권한을 설정하기 때문에 서로의 데이터에 접근하지 못하게 된다.

        // openFileOutput()의 두번째 파라미터는 파일을 '어떤 모드로 열 것인지'결정하는 파라미터이다.
        // 1. MODE_PRIVATE: 앱 전용으로 만들어 다른 앱에서는 접근 불가. 이미 파일이 있는 경우 기존 파일에 덮어씀
        // 2. MODE_APPEND: 파일에 기존 내용 이후에 덧붙이는 모드
        // 3. MODE_WORLD_READABLE: 다른 앱들도 읽을 수 있도록 하는 모드, 현재에는 지원이 중단된 모드로 안드로이드 N 이상 버전에서는 SecurityException 이 발생
        // 4. MODE_WORLD_WRITEABLE: 다른 앱들도 파일에 쓰기 권한을 주는 모드, 현재에는 지원이 중단된 모드로 안드로이드 N 이상 버전에서는 SecurityException 이 발생
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)

        // 파일 출력 스트림에 text 를 바이트로 변환하여 write 한다.
        fileOutputStream.write(text.toByteArray())

        // 파일 출력 스트림을 닫는다.
        fileOutputStream.close()
    }

    // 내부저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String {
        // 내부저장소에 전달된 파일이름의 파일 입력 스트림을 가져온다.
        // openFileInput() 함수를 사용할 때 파일이 없으면 'FileNotFoundException'이 발생한다.

        // 안드로이드는 이 밖에도 앱 데이터 관리를 위해 몇 가지 유용한 함수를 제공한다.
        // 1. getFilesDir(): 내부 파일이 저장된 파일 시스템 디렉토리의 절대 경로를 가져온다.
        // 2. getDir(name: String, mode: Int): 내부 저장소 공간 내부에 name 디렉토리가 있으면 해당 File 객체를 반환하고, 없으면 File 객체를 만든 후에 반환, Mode 는 'openFileOutput()'의 Mode 와 동일
        // 3. deleteFile(name: String): 내부 저장소에 name 으로 저장된 파일을 삭제한다.
        // 4. fileList(): 앱이 현재 저장한 파일명의 문자열 배열을 반환
        val fileInputStream = openFileInput(filename)

        // 파일의 저장된 내용을 읽어 String 형태로 불러온다.
        return fileInputStream.reader().readText()
    }

    // 외부 저장장치를 사용할 수 있고 쓸 수 있는지 체크하는 함수
    fun isExternalStorageWritable(): Boolean {
        when {
            // 외부저장장치 상태가 MEDIA_MOUNTED 면 사용가능
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> return true
            else -> return false
        }
    }

    /*
    // 외부저장장치에서 앱 전용데이터로 사용할 파일 객체를 반환하는 함수
    fun getAppDataFileFromExternalStorage(filename: String): File {
        // KITKAT 버전 부터는 앱전용 디렉토리의 디렉토리 타입 상수인 Environment.DIRECTORY_DOCUMENTS 를 지원
        *//*val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            // 하위 버전에서는 직접 디렉토리 이름 입력
            File(Environment.getExternalStorageDirectory().absoluteFile + "/Documents")
        }*//*

        val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // 디렉토리의 경로중 없는 디렉토리가 있다면 생성
        dir?.mkdirs()
        return File("${dir.absolutePath}${File.separator}${filename}")
    }

    // 외부저장소 앱 전용 디렉토리에 파일로 저장하는 함수
    fun saveToExternalStorage(text: String, filename: String) {
        val fileOutputStream = FileOutputStream(getAppDataFileFromExternalStorage(filename))
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    // 외부저장소 앱 전용 디렉토리에서 파일 데이터를 불러오는 함수
    fun loadFromExternalStorage(filename: String): String {
        return FileInputStream(getAppDataFileFromExternalStorage(filename)).reader().readText()
    }*/

    val MY_PERMISSION_REQUEST = 999

    // 권한 체크 및 요청 함수
    fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this@FileExActivity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        when {
            permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                // 권한을 요청
                ActivityCompat.requestPermissions(
                    this@FileExActivity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            }
        }
    }

    // 권한요청 결과 콜백 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            MY_PERMISSION_REQUEST -> {
                when {
                    grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        // 권한 요청 성공
                        granted = true
                    }
                    else -> {
                        // 사용자가 권한을 허용하지 않음
                        granted = false
                    }
                }
            }
        }
    }

    // 임의의 경로의 파일에 데이터를 저장하는 함수
    fun saveToExternalCustomDirectory(text: String, filepath: String = "/sdcard/data.txt") {
        when {
            // 권한이 있는 경우
            granted -> {
                // 파라미터로 전달받은 경로의 파일의 출력 스트림 객체를 생성
                val fileOutputStream = FileOutputStream(File(filepath))
                fileOutputStream.write(text.toByteArray())
                fileOutputStream.close()
            }
            // 권한이 없는 경우
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 임의의 경로의 파일에서 데이터를 읽는 함수
    fun loadFromExternalCustomDirectory(filepath: String = "/sdcard/data.txt"): String {
        when {
            granted -> {
                return FileInputStream(File(filepath)).reader().readText()
            }
            // 권한이 없는 경우
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                return ""
            }
        }
    }
}
