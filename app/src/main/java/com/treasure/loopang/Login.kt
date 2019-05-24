package com.treasure.loopang

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioTimestamp
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.treasure.loopang.audio.NativeAudioManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import android.os.Process.getExclusiveCores
import android.os.Build


class Login : AppCompatActivity() {

    protected val disposables by lazy { CompositeDisposable() }
    var manager = NativeAudioManager()

    private var permission_list = arrayOf( // 여기에 권한 추가
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var is_start : Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkPermission()
        manager.startTest()
        //manager.startEngine(getExclusiveCores())
        login_button.clicks()
            .subscribe {
                //startActivity(Intent(this, Recording::class.java))
            }.apply { disposables.add(this) }
        gest_login_button.clicks()
            .subscribe { startActivity(Intent(this, Recording::class.java)) }.apply { disposables.add(this) }
        testbutton2.clicks()
            .subscribe { test_recording() } .apply { disposables.add(this) }
    }

    override fun onDestroy() {
        disposables.clear()
        //manager.stopEngine()
        super.onDestroy()
    }

    fun test_recording() {
        manager.tabTest()
        Toast.makeText(this,"Tab",Toast.LENGTH_SHORT).show();
        //if(!is_start) { is_start = true;
        //    manager.tab(true)

        //} else { is_start = false;
        //    manager.tab(false)
        //}
    }

    private fun getExclusiveCores(): IntArray {
        var exclusiveCores = intArrayOf()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        } else {
            try {
                exclusiveCores = android.os.Process.getExclusiveCores()
            } catch (e: RuntimeException) { }
        }
        return exclusiveCores
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
}
