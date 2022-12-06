package tuoyan.com.xinghuo_dayingindex.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Base64
import android.view.Gravity
import android.view.Window
import android.webkit.*
import android.widget.*
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import org.jetbrains.anko.*
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.bean.SMSCode
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * 创建者：
 * 时间：  2018/1/5.
 */
class CodeDialog(
    context: Context,
    private val phone: String,
    private val type: String,
    private val presenter: BasePresenter,
    private val determine: () -> Unit
) : Dialog(context, R.style.custom_dialog) {
    private var mWebView: WebView? = null
    private var editText: EditText? = null
    private var imageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bar = ProgressBar(context)
        val layout = FrameLayout(context)
//        val verifyCoder = VerifyCoder.getVerifyCoder()
//        verifyCoder.isShowtitle = false
//        verifyCoder.json = "themeColor:'00ff0000',type:'popup'"
        presenter.getCheckType(phone, {
            //             0数字验证，1阿里滑动验证
            if (it["checkType"] == "1") {
                initAliCodeView(it)
                layout.addView(mWebView)
            } else if (it["checkType"] == "0") {
                val view = with(context) {
                    verticalLayout {
                        showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                        backgroundResource = R.drawable.bg_radius_w_20
                        space().lparams(wrapContent, dip(20))
                        textView("请输入下方图片验证码") {
                            gravity = Gravity.CENTER
                            textSize = 15f
                            textColor = resources.getColor(R.color.color_1e1e1e)
                        }.lparams(matchParent, wrapContent)
                        linearLayout {
                            horizontalPadding = dip(15)
                            topPadding = dip(20)
                            bottomPadding = dip(15)
                            editText = editText {
                                textColor = resources.getColor(R.color.color_1e1e1e)
                                gravity = Gravity.CENTER
                                padding = 0
                                backgroundResource = R.drawable.bg_shape_e6e6e6_r
                            }.lparams(0, matchParent, 1f)
                            space().lparams(dip(5), wrapContent)
                            imageView = imageView {
                                backgroundResource = R.color.color_a7a7a7
                                scaleType = ImageView.ScaleType.FIT_XY
                                setOnClickListener {
                                    randomCode()
                                }
                            }.lparams(dip(85), matchParent)
                        }.lparams(matchParent, dip(70))
                        view { backgroundResource = R.drawable.divider }.lparams(
                            matchParent, dip(1)
                        )
                        frameLayout {
                            textView("取消") {
                                textSize = 14f
                                padding = 0
                                gravity = Gravity.CENTER
                                backgroundResource = R.color.color_ffffff
                                textColor = resources.getColor(R.color.color_666666)
                                setOnClickListener { dismiss() }
                            }.lparams(dip(90), dip(30)) {
                                gravity = Gravity.CENTER_VERTICAL
                                leftMargin = dip(32)
                            }
                            view {
                                backgroundResource = R.drawable.divider
                            }.lparams(dip(1), matchParent) {
                                gravity = Gravity.CENTER
                            }
                            textView("确定") {
                                textSize = 14f
                                padding = 0
                                gravity = Gravity.CENTER
                                backgroundResource = R.color.color_ffffff
                                setOnClickListener {
                                    saVerify()
                                    presenter.validateCode(phone,
                                        editText?.text.toString().trim(),
                                        type,
                                        {
                                            determine()
                                            dismiss()
                                        }) {
                                        //                                ctx.toast("验证码错误")
                                        editText?.text = SpannableStringBuilder.valueOf("")
                                        dismiss()
                                    }

                                }
                            }.lparams(dip(90), dip(30)) {
                                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                                rightMargin = dip(32)
                            }

                        }.lparams(matchParent, dip(50))
                    }
                }
                layout.addView(view)
                val lp = window?.attributes
                lp?.width = context.dip(285)
                window?.attributes = lp
                val s = it["codeImage"] ?: ""
                val bytes = Base64.decode(s, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView?.imageBitmap = bitmap
            }
        }) {
            dismiss()
        }
        setContentView(layout)
//        val params = FrameLayout.LayoutParams(context.dip(50), context.dip(50))
//        params.gravity = Gravity.CENTER
//        layout.addView(bar, params)
    }

    fun initAliCodeView(map: Map<String, String>) {
        mWebView = WebView(context)
        mWebView!!.settings.useWideViewPort = true
        mWebView!!.settings.loadWithOverviewMode = true
        mWebView!!.settings.loadWithOverviewMode = true
        mWebView!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView!!.webChromeClient = WebChromeClient()
        mWebView!!.webViewClient = WebViewClient()
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.addJavascriptInterface(JsInterface() {
            var json = JSONObject(it)
            presenter.getAliChecked(phone,
                json["token"] as String,
                json["sessionid"] as String,
                json["sig"] as String,
                {
//                {"code":200,"results":{"ret":100,"msg":"","body":{"flag":"0"}}} flag:0没通过，1：通过
                    if (it["flag"] == "1") {
                        saVerify()
                        presenter.sendCode(SMSCode(phone, type), onNext = {
                            determine()
                            dismiss()
                            context.toast("验证码已发送！")
                        }) {
                            dismiss()
                        }
                    } else {
                        dismiss()
                        context.toast("图形验证失败，请重试！")
                    }
                }) {
                dismiss()
                context.toast("图形验证失败，请重试！")
            }
        }, "interface")
        //动态替换里面的账号id
        var htmlStr = initHtmlStr().toString()
        htmlStr = htmlStr.replace("CF_APP_1", map["appKey"]!!)
        htmlStr = htmlStr.replace("register", map["scene"]!!)
        mWebView!!.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null)
    }

    private fun randomCode() {
        presenter.randomCode(phone) {
            val s = it["codeImage"] ?: ""
            val bytes = Base64.decode(s, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView?.imageBitmap = bitmap
        }
    }

    //验证码通过,神策自定义事件
    private fun saVerify() {
        try {
            if ("0" == type) {
                val property = JSONObject()
                property.put("current_scene", "注册")
                property.put("phone_number", phone)
                SensorsDataAPI.sharedInstance().track("get_verification_code", property)

            }
            val properties = JSONObject()
            properties.put("phone_number", phone)
            SensorsDataAPI.sharedInstance().profileSet(properties)
        } catch (e: Exception) {
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = window?.attributes
//        lp?.width = context.dip(260)
        lp?.width = context.resources.displayMetrics.widthPixels / 10 * 7 //设置宽度
//        lp?.height = context.dip(133)
        window?.attributes = lp
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        window?.setDimAmount(0.4f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.CENTER)
    }

    class JsInterface(val callback: (String) -> Unit) {
        //    {
//        "token":"CF_APP_1:1583741458981:0.7323828302058961",
//        "sessionid":"01M5Fcgt-wCL7RxwD-FePZeBB-_d7O2cj6OTccAdGL_d4wgr60fjlVjG-AcDAkqxoxBU2xACbsFr8WZrdQ1XmeWxVFSuwi-CnNimnK-hevYBM",
//        "sig":"05YeuhNVRWpJfVhSFI6DPrERA19ex2OH7De9GXU4ybCcWWsm9FcKWMnxG2w2ES3cLiccqmxJMtjAr7j5dKqTNYJ0ZTND4-NyN-jrA0a3w7GhqWPEilFw6RXOaTOt-shEdH2BWUsP_h6JFAytgjwuCsy0lKL1G4CiN7EhWGmAO_ilX88cV2HHzMUX7ZYR7fhxRfmlc9c39bgF_rtVrnk6zX2mhizZKGFAca1m4c1og8fafa3TorDhZeMvym-A_GZxDKJQ1pxJPExAyjX7T3zUzKZzzsFquPGKFGfbGpgJ2MELGZaykPHIAQSjiRuyvIEpv1"
//    }
        @JavascriptInterface
        fun getSlideData(callData: String) {
            callback(callData)
        }
    }

    private fun initHtmlStr(): StringBuilder {
        var htmlStr: StringBuilder = StringBuilder()
        var ips: InputStream = context.resources.assets.open("alih5.html")
        var reder = BufferedReader(InputStreamReader(ips))
        var str = reder.readLine()

        while (str != null) {
            htmlStr.append(str + "\n")
            str = reder.readLine()
        }
        return htmlStr
    }
}