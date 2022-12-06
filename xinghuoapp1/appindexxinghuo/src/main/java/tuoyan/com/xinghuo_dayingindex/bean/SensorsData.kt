package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Zzz on 2021/5/14
 * Email:
 */
class SensorsData() : Parcelable {
    var course_id = ""
    var course_name = ""
    var is_live = false
    var course_validity = ""
    var original_price = ""
    var current_price = ""
    var internal_name_online_course = ""
    var teacher_name = ""
    var charge_type = ""
    var period_id = ""
    var live_platform_id = ""
    var period_type = ""
    var period_name = ""
    var video_service_provider = ""

    constructor(parcel: Parcel) : this() {
        course_id = parcel.readString() ?: ""
        course_name = parcel.readString() ?: ""
        is_live = parcel.readByte() != 0.toByte()
        course_validity = parcel.readString() ?: ""
        original_price = parcel.readString() ?: ""
        current_price = parcel.readString() ?: ""
        internal_name_online_course = parcel.readString() ?: ""
        teacher_name = parcel.readString() ?: ""
        charge_type = parcel.readString() ?: ""
        period_id = parcel.readString() ?: ""
        live_platform_id = parcel.readString() ?: ""
        period_type = parcel.readString() ?: ""
        period_name = parcel.readString() ?: ""
        video_service_provider = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(course_id)
        parcel.writeString(course_name)
        parcel.writeByte(if (is_live) 1 else 0)
        parcel.writeString(course_validity)
        parcel.writeString(original_price)
        parcel.writeString(current_price)
        parcel.writeString(internal_name_online_course)
        parcel.writeString(teacher_name)
        parcel.writeString(charge_type)
        parcel.writeString(period_id)
        parcel.writeString(live_platform_id)
        parcel.writeString(period_type)
        parcel.writeString(period_name)
        parcel.writeString(video_service_provider)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SensorsData> {
        override fun createFromParcel(parcel: Parcel): SensorsData {
            return SensorsData(parcel)
        }

        override fun newArray(size: Int): Array<SensorsData?> {
            return arrayOfNulls(size)
        }
    }
}

class SensorsExercise() : Parcelable {
    var test_paper_id = ""
    var paper_name = ""
    var section = ""
    var is_there_a_score = false
    var number_of_topics = 0
    var test_paper_form = ""
    var presentation_form_paper = ""

    constructor(parcel: Parcel) : this() {
        test_paper_id = parcel.readString() ?: ""
        paper_name = parcel.readString() ?: ""
        section = parcel.readString() ?: ""
        is_there_a_score = parcel.readByte() != 0.toByte()
        number_of_topics = parcel.readInt()
        test_paper_form = parcel.readString() ?: ""
        presentation_form_paper = parcel.readString() ?: ""
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(test_paper_id)
        parcel.writeString(paper_name)
        parcel.writeString(section)
        parcel.writeByte(if (is_there_a_score) 1 else 0)
        parcel.writeInt(number_of_topics)
        parcel.writeString(test_paper_form)
        parcel.writeString(presentation_form_paper)
    }

    companion object CREATOR : Parcelable.Creator<SensorsExercise> {
        override fun createFromParcel(parcel: Parcel): SensorsExercise {
            return SensorsExercise(parcel)
        }

        override fun newArray(size: Int): Array<SensorsExercise?> {
            return arrayOfNulls(size)
        }
    }
}