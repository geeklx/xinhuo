package tuoyan.com.xinghuo_dayingindex.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.zxing.journeyapps.barcodescanner.CaptureManager
import com.zxing.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.activity_scanner.*
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
import tuoyan.com.xinghuo_dayingindex.ui.scan.ScanHistoryActivity


class ScannerActivity : LifeActivity<BasePresenter>(), DecoratedBarcodeView.TorchListener {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_scanner
    private val captureManager by lazy { CaptureManager(this, db_code) }
    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen = true
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        captureManager.decode()
    }

    override fun handleEvent() {
        ic_close.setOnClickListener {
            onBackPressed()
        }

        ic_flash.setOnClickListener {
            if (it.isSelected) db_code.setTorchOff() else db_code.setTorchOn()
            it.isSelected = !it.isSelected
        }

        edit_scan.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var resultIntent = Intent()
                resultIntent.putExtra("code", edit_scan.text.toString())
                setResult(93, resultIntent)
                finish()
            }
            false
        }
        tv_scan_history.setOnClickListener {
            startActivity(Intent(this@ScannerActivity, ScanHistoryActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return db_code.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onTorchOn() {
    }

    override fun onTorchOff() {
    }
}