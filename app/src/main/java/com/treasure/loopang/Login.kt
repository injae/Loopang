package com.treasure.loopang

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding3.view.clicks
import com.treasure.loopang.Database.DatabaseManager
import com.treasure.loopang.communication.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*

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

        val ld = LoadingActivity(this@Login)
        GlobalScope.launch {
            DatabaseManager.deleteToken(this@Login)
            val email = DatabaseManager.getEmail(this@Login)
            if(DatabaseManager.getPassword(this@Login) != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    ld.show()
                    if(email != null) input_id.text = SpannableStringBuilder(email)
                    input_password.text = SpannableStringBuilder("********")
                }
                UserManager.setUser(DatabaseManager.getEmail(this@Login)!!, decodeYuni(DatabaseManager.getPassword(this@Login)!!))
                cb_auto_login.isChecked = true
                val cnt = Connector()
                val res = cnt.process(ResultManager.LOGIN, UserManager.getUser())
                if(ResultManager.getCode(res) == ResultManager.SUCCESS) {
                    UserManager.isLogined = true
                    DatabaseManager.insertToken(this@Login, res.refreshToken)
                    CoroutineScope(Dispatchers.Main).launch { ld.dismiss() }
                    startActivity(Intent(this@Login, Recording::class.java))
                    finish()
                }
            }
            else {
                //val email = DatabaseManager.getEmail(this@Login)
                if(email != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        cb_save_id.isChecked = true
                        input_id.text = SpannableStringBuilder(email)
                    }
                }
            }
        }

        input_id.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    input_password.requestFocus()
                    return true
                }
                return false
            }
        })
        input_password.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    ASyncer(this@Login).execute()
                    return true
                }
                return false
            }
        })
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
    private fun onSignUpButtonClick() { startActivity(Intent(this, RegisterActivity::class.java)) }
    private fun onLoginButtonClick(){ ASyncer(this).execute() }
}