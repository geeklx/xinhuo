package tuoyan.com.xinghuo_dayingindex.ui.books

import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tuoyan.com.xinghuo_dayingindex.bean.Book
import tuoyan.com.xinghuo_dayingindex.bean.BookDetail

class BooksPresenter(onProgress: OnProgress): BasePresenter(onProgress){

    /**
     * 全部配套图书列表
     */
    fun getBooks(grade: String, onNext: (List<Book>)-> Unit){
        api.getBooks(grade).sub({onNext(it.body)})
    }

    /**
     * 图书详情
     */
    fun getBookDetail(key: String, type: String, onNext: (BookDetail)-> Unit){
        api.getBookDetail(key,type).sub({onNext(it.body)})
    }

    /**
     * 加入我的学习
     */
    fun addMyStudy(data : Map<String,String>, onNext: (String) -> Unit) {
        api.addMyStudy(data).sub({ onNext(it.msg) })
    }

}