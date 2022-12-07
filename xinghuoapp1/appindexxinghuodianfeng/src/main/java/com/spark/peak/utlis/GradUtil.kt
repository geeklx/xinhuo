package com.spark.peak.utlis

class GradUtil {

    companion object {
        fun parseGradStr(sectionName: String, gradName: String): String{
            return if (gradName == "通用"){
                sectionName
            }else if (sectionName == "小学" || sectionName == "初中"){
                gradName
            } else if (sectionName.isNotEmpty() && gradName.isNotEmpty()){
                sectionName.substring(0,1)+ gradName.substring(0,1)
            }else{
                ""
            }

        }
    }
}