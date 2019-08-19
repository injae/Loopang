package com.treasure.loopang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        sign_up_button.setOnClickListener { onSignUpButtonClick() }
    }

    private fun onSignUpButtonClick() {
        sign_up_button.text = getString(R.string.btn_login_wiat)
        sign_up_button.isClickable = false
        val result = signup(input_id.text.toString(), input_password.text.toString(), input_email.text.toString())
        sign_up_button.text = getString(R.string.btn_register_sign_up)
        if(result){
            toast("Success!")
            finish()
        } else {
            sign_up_button.isClickable = true
            toast("SignUp Failed")
        }
    }

    private fun signup(id: String, password: String, email: String): Boolean {
        // 여기에 회원가입 비지니스 로직 입력!
        return true
    }
}
