package com.treasure.loopang

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    protected val disposables by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.clicks()
            .subscribe { startActivity(Intent(this, Recording::class.java)) }.apply { disposables.add(this) }
        guest_login_button.clicks()
            .subscribe { startActivity(Intent(this, Recording::class.java)) }.apply { disposables.add(this) }

    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
