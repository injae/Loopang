package com.treasure.loopang.communication

import android.content.Intent
import android.os.AsyncTask
import com.treasure.loopang.Database.DatabaseManager
import com.treasure.loopang.Login
import com.treasure.loopang.R
import com.treasure.loopang.Recording
import com.treasure.loopang.RegisterActivity
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.input_id
import kotlinx.android.synthetic.main.activity_login.input_password
import kotlinx.android.synthetic.main.activity_register.*

class ASyncer<T>(private val context: T, private var code: Int = 0,
                 private var response: Result = Result()) : AsyncTask<Unit, Unit, Unit>() {
    override fun onPreExecute() {
        super.onPreExecute()
        when(context) {
            is Login -> {
                context.login_button.text = context.getString(R.string.btn_login_wiat)
                context.login_button.isClickable = false
            }

            is RegisterActivity -> {
                context.sign_up_button.text = context.getString(R.string.btn_login_wiat)
                context.sign_up_button.isClickable = false
            }
        }
    }

    override fun doInBackground(vararg params: Unit?) {
        val connector = Connector()

        when(context) {
            is Login -> {
                UserManager.setUser(context.input_id.text.toString(), context.input_password.text.toString())
                response = connector.process(ResultManager.LOGIN, UserManager.getJson())
                code = ResultManager.getCode(response)
            }

            is RegisterActivity -> {

            }
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        when(context) {
            is Login -> {
                if(code == ResultManager.SUCCESS_LOGIN) {
                    DatabaseManager.insertToken(context, response.refreshToken)
                    context.startActivity(Intent(context, Recording::class.java))
                }
                else if(code == ResultManager.UNREG_OR_WRONG) {
                    context.login_button.isClickable = true
                    context.toast("Login Failed..")
                    context.login_button.text = context.getString(R.string.btn_sign_in)
                }
            }

            is RegisterActivity -> {

            }
        }
    }
}