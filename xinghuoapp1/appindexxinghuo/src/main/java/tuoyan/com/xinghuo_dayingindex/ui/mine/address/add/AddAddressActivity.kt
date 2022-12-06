//package tuoyan.com.xinghuo_daying.ui.mine.address.add
//
//import android.view.Gravity
//import android.view.View
//import kotlinx.android.synthetic.main.activity_add_address.*
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.bean.Address
//import tuoyan.com.xinghuo_dayingindex.ui.mine.address.AddressPresenter
//import tuoyan.com.xinghuo_dayingindex.utlis.OptionsWindowHelper
//
//class AddAddressActivity : LifeActivity<AddressPresenter>() {
//    override val presenter = AddressPresenter(this)
//    override val layoutResId = R.layout.activity_add_address
//    private val data by lazy { intent.getSerializableExtra(DATA) as? Address }
//    private val isAdd by lazy { intent.getBooleanExtra(ADD, false) }
//    private var provinceId: String? = null
//    private var cityId: String? = null
//    private var areaId: String? = null
//    private val picker by lazy {
//        OptionsWindowHelper.builder(this, object : OptionsWindowHelper.OnOptionsSelectListener {
//            override fun onOptionsSelect(province: OptionsWindowHelper.Picker?,
//                                         city: OptionsWindowHelper.Picker?,
//                                         area: OptionsWindowHelper.Picker?) {
//                tv_city.text = "${province?.name ?: ""}${city?.name ?: ""}${area?.name ?: ""}"
//                provinceId = province?.code
//                cityId = city?.code
//                areaId = area?.code
//            }
//
//        })
//    }
//
//    override fun configView() {
//        data?.let {
//            et_name.setText(it.receiver ?: "")
//            et_phone.setText(it.receiverMobile ?: "")
//            et_address.setText(it.detailAddress ?: "")
//            tv_city.text = it.fullName ?: ""
//            provinceId = it.provinceId
//            cityId = it.cityId
//            areaId = it.countyId
//            tv_delete.visibility = View.VISIBLE
//            iv_default.isSelected = it.isDefault == "1"
//        }
//    }
//
//    fun save(v: View) {
//        val name = et_name.text.toString().trim()
//        val phone = et_phone.text.toString().trim()
//        val address = et_address.text.toString().trim()
//        val city = tv_city.text.toString().trim()
//
//        if (isAdd) {
//            val map = mutableMapOf("provinceId" to provinceId,
//                    "cityId" to cityId,
//                    "countyId" to areaId,
//                    "fullName" to city,
//                    "detailAddress" to address,
//                    "receiver" to name,
//                    "receiverMobile" to phone,
//                    "isDefault" to if (iv_default.isSelected) "1" else "0")
//            presenter.addAddress(map) {
//                onBackPressed()
//            }
//        } else {
//            val map = mutableMapOf("provinceId" to provinceId,
//                    "cityId" to cityId,
//                    "countyId" to areaId,
//                    "fullName" to city,
//                    "detailAddress" to address,
//                    "receiver" to name,
//                    "receiverMobile" to phone,
//                    "isDefault" to if (iv_default.isSelected) "1" else "0",
//                    "key" to data?.key)
//            presenter.editAddress(map) {
//                onBackPressed()
//            }
//
//        }
//    }
//
//    fun city(v: View) {
//        if (picker.isShowing)
//            picker.dismiss()
//        else
//            picker.showAtLocation(v, Gravity.BOTTOM, 0, 0)
//    }
//
//    fun isDefault(v: View) {
//        // : 2018/10/18 13:29  设置默认
//        v.isSelected = !v.isSelected
//    }
//
//    fun delete(v: View) {
//        // : 2018/10/18 13:29  删除
//        data?.key?.let {
//            presenter.deleteAddress(it) {
//                onBackPressed()
//
//            }
//        }
//    }
//
//    companion object {
//        const val DATA = "data"
//        const val ADD = "add"
//    }
//}
