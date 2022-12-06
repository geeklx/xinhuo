//package tuoyan.com.xinghuo_daying.ui.mine.address
//
//import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
//import tuoyan.com.xinghuo_dayingindex.base.OnProgress
//import tuoyan.com.xinghuo_dayingindex.bean.*
//import tuoyan.com.xinghuo_dayingindex.net.Results
//
///**
// * 创建者：
// * 时间：
// */
//class AddressPresenter(progress: OnProgress) : BasePresenter(progress) {
//
//    fun addresses(onNext: (Results<List<Address>>) -> Unit) {
//        api.addresses().sub(onNext = { onNext(it) })
//    }
//
//    fun addAddress(map: Map<String, String?>, onNext: () -> Unit) {
//        api.addAddress(map).sub(onNext = { onNext() })
//    }
//    fun editAddress(map: Map<String, String?>, onNext: () -> Unit) {
//        api.editAddress(map).sub(onNext = { onNext() })
//    }
//    fun deleteAddress(key:String, onNext: () -> Unit) {
//        api.deleteAddress(key).sub(onNext = { onNext() })
//    }
//}