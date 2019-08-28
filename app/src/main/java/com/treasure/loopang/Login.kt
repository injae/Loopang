package com.treasure.loopang

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.jakewharton.rxbinding3.view.clicks
import com.treasure.loopang.communication.ASyncer
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*

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
        val asyncer = ASyncer(this)
        asyncer.execute()
    }
}