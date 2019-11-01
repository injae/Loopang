package com.treasure.loopang.communication

import android.content.Intent
import android.os.AsyncTask
import com.treasure.loopang.*
import com.treasure.loopang.Database.DatabaseManager
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.input_id
import kotlinx.android.synthetic.main.activity_login.input_password
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ASyncer<T>(private val context: T, private var code: Int = 0, private var connector: Connector = Connector(),
                 private var response: Result = Result(), private var ld: LoadingActivity? = null) : AsyncTask<Unit, Unit, Unit>() {
    override fun onPreExecute() {
        super.onPreExecute()
        when(context) {
            is Login -> {
                ld = LoadingActivity(context)
                ld?.show()
                context.login_button.text = context.getString(R.string.btn_login_wait)
                context.login_button.isClickable = false
            }

            is RegisterActivity -> {
                ld = LoadingActivity(context)
                ld?.show()
                context.sign_up_button.text = context.getString(R.string.btn_login_wait)
                context.sign_up_button.isClickable = false
            }

            is Recording -> {
                ld = LoadingActivity(context)
                ld?.show()
            }
        }
    }

    override fun doInBackground(vararg params: Unit?) {
        when(context) {
            is Login -> {
                UserManager.setEncodedPassword(encodeYuni(context.input_password.text.toString()))
                UserManager.setUser(context.input_id.text.toString(), context.input_password.text.toString())
                response = connector.process(ResultManager.LOGIN, UserManager.getUser())
                code = ResultManager.getCode(response)
            }

            is RegisterActivity -> {
                if(context.input_id.text.toString().find { it == '@' } != null) {
                    UserManager.setUser(context.input_id.text.toString(), context.input_password.text.toString(), context.input_name.text.toString())
                    response = connector.process(ResultManager.SIGN_UP, UserManager.getUser())
                    UserManager.makeEmptyUser()
                    code = ResultManager.getCode(response)
                }
                else {
                    code = ResultManager.FAIL
                    response.status = "fail"
                    response.message = "아이디는 이메일 형식이어야 합니다."
                }
            }

            is Recording -> {
                if(UserManager.isLogined && UserManager.getUser().name == "") {
                    var boolTemp = true
                    response = connector.process(ResultManager.INFO_REQUEST)
                    if(response.status == "fail") boolTemp = false
                    response = connector.process(ResultManager.FEED_REQUEST)
                    if(response.status == "fail") boolTemp = false
                    if(boolTemp) response.message = "SUCCESS INFO, FEED"
                    code = ResultManager.getCode(response)
                    /*val ct = Connector()
                    Log.d("OkHttp", "첫번째로 받아온 트랙 : ${UserManager.getUser().trackList[0]}")
                    ct.process(ResultManager.FILE_DOWNLOAD, null, null, null, UserManager.getUser().trackList[0].id)
                    connector.process(ResultManager.FEED_REQUEST)
                    Log.d("OkHttp", "피드 결과 : ${connector.feedResult}")
                    connector.process(ResultManager.SEARCH_REQUEST, null, null, "open")
                    Log.d("OkHttp", "검색 결과 : ${connector.searchResult}")*/
                }
            }
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        when(context) {
            is Login -> {
                ld?.dismiss()
                context.toast("Code = ${response.message}")
                when(code) {
                    ResultManager.SUCCESS -> {
                        GlobalScope.launch {
                            DatabaseManager.deleteEmail(context)
                            DatabaseManager.deletePassword(context)
                            DatabaseManager.insertToken(context, response.refreshToken)
                            if(context.cb_save_id.isChecked)
                                DatabaseManager.insertEmail(context, UserManager.getUser().email)
                            if(context.cb_auto_login.isChecked) {
                                DatabaseManager.insertEmail(context, UserManager.getUser().email)
                                DatabaseManager.insertPassword(context, UserManager.getUser().encodedPassword)
                            }
                        }
                        UserManager.isLogined = true
                        context.startActivity(Intent(context, Recording::class.java))
                    }
                    else -> {
                        UserManager.makeEmptyUser()
                        context.login_button.isClickable = true
                        context.login_button.text = context.getString(R.string.btn_sign_in)
                    }
                }
            }

            is RegisterActivity -> {
                ld?.dismiss()
                context.toast("Code = ${response.message}")
                when(code) {
                    ResultManager.SUCCESS -> { context.finish() }
                    else -> {
                        context.sign_up_button.isClickable = true
                        context.sign_up_button.text = context.getString(R.string.btn_register_sign_up)
                    }
                }
            }

            is Recording -> {
                if(UserManager.isLogined) {
                    ld?.dismiss()
                    context.toast("Code = ${response.message}")
                    when(code) {
                        ResultManager.SUCCESS -> {
                            val intentToCommunity = Intent(context, CommunityActivity::class.java)
                            intentToCommunity.putExtra("feedResult", connector.feedResult)
                            context.startActivity(intentToCommunity)
                        }
                        ResultManager.FAIL -> {
                            UserManager.setInfo("FAIL_NICKNAME", List<MusicListClass>(0,{ MusicListClass() }), List<MusicListClass>(0,{MusicListClass()}))
                        }
                    }
                }
            }
        }
    }
}