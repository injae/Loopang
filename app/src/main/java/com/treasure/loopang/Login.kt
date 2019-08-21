package com.treasure.loopang

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import com.jakewharton.rxbinding3.view.clicks
import com.treasure.loopang.communication.Connector
import com.treasure.loopang.communication.Login
import com.treasure.loopang.communication.ResultManager
import com.treasure.loopang.ui.toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

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

        var code = AtomicInteger(0)

        GlobalScope.launch{
            code.set(login(input_id.text, input_password.text))
            Log.d("TESTMAN", "코드값 : ${code}")
        }
        while(code.get() == 0) { }

        if(code.get() == ResultManager.SUCCESS_LOGIN){
            startActivity(Intent(this, Recording::class.java))
        }
        else if(code.get() == ResultManager.UNREG_OR_WRONG) {
            login_button.isClickable = true
            toast("Login Failed")
            login_button.text = getString(R.string.btn_sign_in)
        }
    }

    private fun login(txtId: Editable?, txtPassword: Editable?): Int {
        val connector = Connector()
        val lg = Login()
        lg.setUserInfo(txtId.toString(), txtPassword.toString())
        connector.process(ResultManager.LOGIN, lg.getJson())
        return ResultManager.getCode(connector.result.get())
    }
}
