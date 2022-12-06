package tuoyan.com.xinghuo_dayingindex.utlis

import android.annotation.SuppressLint
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.DrawableUtils


@SuppressLint("RestrictedApi")
        /**
         * 创建者：
         * 时间：  2018/1/6.
         */
fun TextInputLayout.recoverEditTextBackGround() {
    editText?.background?.let {
        var editTextBackground = it
        if (DrawableUtils.canSafelyMutateDrawable(editTextBackground)) {
            editTextBackground = editTextBackground.mutate()
        }
        DrawableCompat.clearColorFilter(editTextBackground)
    }
}