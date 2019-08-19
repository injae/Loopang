package com.treasure.loopang

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import androidx.room.Database
import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding3.view.clicks
import com.treasure.loopang.Database.DatabaseManager
import com.treasure.loopang.ui.toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    protected val disposables by lazy { CompositeDisposable() }
    private var permission_list = arrayOf( // 필요한 권한 입력 후 AndroidManifest.xml에도 추가 해주면됌
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.INTERNET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkPermission()
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        login_button.clicks()
            .subscribe { onLoginButtonClick() }.apply { disposables.add(this) }
        btn_sign_up.clicks()
            .subscribe { onSignUpButtonClick() }.apply { disposables.add(this) }
        guest_login_button.clicks()
            .subscribe { startActivity(Intent(this, Recording::class.java)) }.apply { disposables.add(this) }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    fun checkPermission() {
        for(permission in permission_list) {
            val check = checkCallingOrSelfPermission(permission)
            if(check == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list, 0)
                break
            }
        }
    }

    private fun onSignUpButtonClick() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun onLoginButtonClick(){
        login_button.text = getString(R.string.btn_login_wiat)
        login_button.isClickable = false
        val result = login(input_id.text, input_password.text)
        login_button.text = getString(R.string.btn_sign_in)
        if(result){
            startActivity(Intent(this, Recording::class.java))
        } else {
            login_button.isClickable = true
            toast("Login Failed")
        }
    }

    private fun login(txtId: Editable?, txtPassword: Editable?): Boolean {
        // 여기에 로그인관련 비지니스 로직 입력. 성공시 true 반환. 실패시 false 반환. 사실 고쳐도됨.
        return !(txtId == null || txtPassword == null || txtId.toString() == "" || txtPassword.toString() == "")
    }
}
