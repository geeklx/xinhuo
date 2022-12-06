package tuoyan.com.xinghuo_dayingindex.bean

import com.contrarywind.interfaces.IPickerViewData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 创建者：
 * 时间：  2018/10/12.
 */
class WordHome {
    /**
     * coreCount : 7yS
     * catalogList : [{"catalogKey":"zvAq28W","catalogName":"!q88zn"}]
     * wrongWordCount : Xbd
     * newWordCount : OxeALUc
     * coreKey : xz10*j
     */

    var coreCount: String? = null
    var wrongWordCount: String? = null
    var newWordCount: String? = null
    var coreKey: String? = null
    var catalogList: List<CatalogList>? = null
    var list: List<WordCatalog>? = null

}

class WordCatalog {
    var wordClassifyKey = ""//为空  为生词本
    var isCollection = ""
    var wordClassifyName = ""
    var gradeKey = ""
}

class CatalogList : IPickerViewData {


    /**
     * catalogKey : zvAq28W
     * catalogName : !q88zn
     */

    var catalogKey: String? = null
    var catalogName: String? = null
    override fun getPickerViewText(): String {
        return catalogName ?: ""
    }
}

class ClassifyList {
    /**
     * catalogKey : ccomQf
     * catalogName : (t#k
     * rightRate : hI!
     */

    var catalogKey: String? = null
    var catalogName: String? = null
    var rightRate: String? = null
}

class LearnStatus : Serializable {
    var totalCount: Int = 0
    var isLearn: Int = 0
    var classifyKey: String? = null
    var list: List<WordsByCatalogkey>? = null
}

class WordsByCatalogkey : Serializable {
    /**
     * symbol : ˈbenɪfɪt
     * collocation :
     * emergence :
     * paraphrase : n.益处；救济金 vi.得益于 vt.使受益
     * sound : http://res.xhiw.com.cn/benefit.mp3
     * questionInfo : [{"content":"n.举止","order":"A","isAnswer":"0"},{"content":"n.代表","order":"B","isAnswer":"0"},{"content":"n.益处","order":"C","isAnswer":"1"}]
     * wordKey : 94836617007369410441
     * antonym :
     * exampleSentence : They can affect a person’s ability to get a job and qualification for benefits. 它们会影响一个人获取工作的能力和谋求福利的资格。
     * parasynonyms :
     * mnemonic :
     * word : benefit
     * key : 86925357517215416621
     */

    var key: String? = null
    var isAdd: String? = null
    var word: String? = null
    var symbol: String? = null

    @SerializedName("resourceKey")
    var sound: String? = null
    var paraphrase: String? = null
    var exampleSentence: String? = null
    var collocation: String? = null
    var emergence: String? = null
    var wordKey: String? = null
    var antonym: String? = null
    var parasynonyms: String? = null
    var mnemonic: String? = null
    var questionInfo: List<Question>? = null

    var classifyKey = ""
    var catalogKey = ""

    var learn = true
    var correct = false
    fun copy(): WordsByCatalogkey {
        val catalogkey = WordsByCatalogkey()
        catalogkey.key = key
        catalogkey.word = word
        catalogkey.isAdd = isAdd
        catalogkey.symbol = symbol
        catalogkey.sound = sound
        catalogkey.paraphrase = paraphrase
        catalogkey.exampleSentence = exampleSentence
        catalogkey.collocation = collocation
        catalogkey.emergence = emergence
        catalogkey.wordKey = wordKey
        catalogkey.antonym = antonym
        catalogkey.parasynonyms = parasynonyms
        catalogkey.mnemonic = mnemonic
        catalogkey.questionInfo = questionInfo
        catalogkey.learn = learn
        catalogkey.correct = correct
        return catalogkey
    }

}

class Question : Serializable {
    /**
     * content : n.举止
     * order : A
     * isAnswer : 0
     */
    var content: String? = null
    var order: String? = null
    var isAnswer: String? = null
}

class WordDetail : Serializable {
    /**
     * key : K@Or
     * word : y#hc6Kd
     * symbol : L8h
     * sound : eF1[
     * paraphrase : dwdD0
     * exampleSentence : v&f7pyC
     */

    var key: String? = null
    var word: String? = null
    var symbol: String? = null
    var sound: String? = null
    var paraphrase: String? = null
    var exampleSentence: String? = null
}

class ScanWord {
    var lastCatalogName = ""
    var lastCatalogKey = ""
    var lastWordKKey = ""
    var catalogList = mutableListOf<CatalogList>()
    var wordList = mutableListOf<WordsByCatalogkey>()
}