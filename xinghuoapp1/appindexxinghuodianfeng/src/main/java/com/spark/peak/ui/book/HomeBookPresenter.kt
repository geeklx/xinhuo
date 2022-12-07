package com.spark.peak.ui.book

import com.spark.peak.base.BasePresenter
import com.spark.peak.base.OnProgress
import com.spark.peak.bean.BookContent
import com.spark.peak.bean.GradeBean
import com.spark.peak.bean.GradeType
import com.spark.peak.net.Re
import com.spark.peak.net.Results

/**
 * Created by Zzz on 2020/12/30
 * Email:zgqmax@foxmail.com
 */

class HomeBookPresenter(progress: OnProgress) : BasePresenter(progress) {
    fun getSelectEditor(sectionKey: String, gradeKey: String, gradeCX: String, onNext: (GradeType) -> Unit) {
        bookApi.getSelectEditor("syncListen", sectionKey, gradeKey, gradeCX, Re.getBookHeard()).sub({ onNext(it.body) })
    }

    fun getSelectSection(onNext: (List<GradeBean>) -> Unit) {
        bookApi.getSelectSection("syncListen", Re.getBookHeard()).sub({ onNext(it.body) })
    }

    fun getSelectGrade(sectionCX: String, onNext: (List<GradeBean>) -> Unit) {
        bookApi.getSelectGrade("syncListen", sectionCX, Re.getBookHeard()).sub({ onNext(it.body) })
    }

    fun getSelectSyncListen(sectionKey: String, gradeKey: String, subjectKey: String, edition: String, term: String, onNext: (Results<BookContent>) -> Unit) {
        bookApi.getSelectSyncListen(sectionKey, gradeKey, subjectKey, edition, term, Re.getBookHeard()).sub({ onNext(it) })
    }

    fun getCatalogResources(appSyncStudyKey: String, resourceType: String, onNext: (BookContent) -> Unit) {
        bookApi.getCatalogResources(appSyncStudyKey, resourceType, Re.getBookHeard()).sub({ onNext(it.body) })
    }
}