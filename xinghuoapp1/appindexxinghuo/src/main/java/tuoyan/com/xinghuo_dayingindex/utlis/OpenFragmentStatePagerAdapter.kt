//package tuoyan.com.xinghuo_daying.utlis
//
//import android.annotation.TargetApi
//import android.app.Fragment
//import android.app.FragmentManager
//import android.app.FragmentTransaction
//import android.os.Build
//import android.os.Bundle
//import android.os.Parcelable
//import android.support.v4.view.PagerAdapter
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//
///**
// * 创建者：
// * 时间：  2018/10/10.
// */
//abstract class OpenFragmentStatePagerAdapter<T>(private val mFragmentManager: FragmentManager) : PagerAdapter() {
//
//    private val TAG = "FragmentStatePagerAdapt"
//    private val DEBUG = false
//
//    private var mCurTransaction: FragmentTransaction? = null
//
//    private val mSavedState = ArrayList<Fragment.SavedState?>()
//    private var mItemInfos = ArrayList<ItemInfo<T>?>()
//    protected var mCurrentPrimaryItem: Fragment? = null
//    private var mNeedProcessCache = false
//
//    /**
//     * Return the Fragment associated with a specified position.
//     */
//    abstract fun getItem(position: Int): Fragment
//
//    protected fun getCachedItem(position: Int): Fragment? = if (mItemInfos.size > position) mItemInfos[position]?.fragment else null
//
//    override fun startUpdate(container: ViewGroup) {
//        if (container.id == View.NO_ID) {
//            throw IllegalStateException("ViewPager with adapter " + this
//                    + " requires a view id")
//        }
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        // If we already have this item instantiated, there is nothing
//        // to do.  This can happen when we are restoring the entire pager
//        // from its saved state, where the fragment manager has already
//        // taken care of restoring the fragments we previously had instantiated.
//        if (mItemInfos.size > position) {
//            val ii = mItemInfos[position]
//            ii?.let {
//                if (it.position == position) {
//                    return this
//                } else {
//                    checkProcessCacheChanged()
//                }
//            }
//        }
//
//        val fragment = getItem(position)
//        if (DEBUG) Log.v(TAG, "Adding item #$position: f=$fragment")
//        if (mSavedState.size > position) {
//            val fss = mSavedState[position]
//            if (fss != null) {
//                fragment.setInitialSavedState(fss)
//            }
//        }
//        while (mItemInfos.size <= position) {
//            mItemInfos.add(null)
//        }
//        fragment.setMenuVisibility(false)
//        fragment.userVisibleHint = false
//        val iiNew = ItemInfo(fragment, getItemData(position), position)
//        mItemInfos[position] = iiNew
//        if (mCurTransaction == null) {
//            mCurTransaction = mFragmentManager.beginTransaction()
//        }
//        mCurTransaction!!.add(container.id, fragment)
//
//        return iiNew
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        val ii = `object` as ItemInfo<T>
//
//        if (DEBUG)
//            Log.v(TAG, "Removing item #" + position + ": f=" + `object`
//                    + " v=" + ii.fragment.view)
//        while (mSavedState.size <= position) {
//            mSavedState.add(null)
//        }
//        mSavedState[position] = if (ii.fragment.isAdded)
//            mFragmentManager.saveFragmentInstanceState(ii.fragment)
//        else
//            null
//        mItemInfos[position] = null
//        if (mCurTransaction == null) {
//            mCurTransaction = mFragmentManager.beginTransaction()
//        }
//        mCurTransaction!!.remove(ii.fragment)
//    }
//
//    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
//        val ii = `object` as? ItemInfo<T>
//        val fragment = ii?.fragment
//        if (fragment != mCurrentPrimaryItem) {
//            mCurrentPrimaryItem?.apply {
//                setMenuVisibility(false)
//                userVisibleHint = false
//            }
//            fragment?.apply {
//                setMenuVisibility(true)
//                userVisibleHint = true
//            }
//            mCurrentPrimaryItem = fragment
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.N)
//    override fun finishUpdate(container: ViewGroup) {
//        mCurTransaction?.apply {
//            commitNowAllowingStateLoss()
//
//        }
//        mCurTransaction = null
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        val fragment = (`object` as ItemInfo<T>).fragment
//        return fragment.view === view
//    }
//
//    override fun getItemPosition(`object`: Any): Int {
//        mNeedProcessCache = true
//        val itemInfo: ItemInfo<T> = `object` as ItemInfo<T>
//        val oldPosition = mItemInfos.indexOf(itemInfo)
//        if (oldPosition >= 0) {
//            val oldData: T? = itemInfo.data
//            val newData: T? = getItemData(oldPosition)
//            return if (dataEquals(oldData, newData)) {
//                PagerAdapter.POSITION_UNCHANGED
//            } else {
//                val oldItemInfo = mItemInfos[oldPosition]
//                var oldDataNewPosition = getDataPosition(oldData)
//                if (oldDataNewPosition < 0) {
//                    oldDataNewPosition = PagerAdapter.POSITION_NONE
//                }
//                oldItemInfo?.apply {
//                    position = oldDataNewPosition
//                }
//                oldDataNewPosition
//            }
//        }
//        return PagerAdapter.POSITION_UNCHANGED
//    }
//
//    override fun notifyDataSetChanged() {
//        super.notifyDataSetChanged()
//        checkProcessCacheChanged()
//    }
//
//    private fun checkProcessCacheChanged() {
//        if (!mNeedProcessCache) return
//        mNeedProcessCache = false
//        val pendingItemInfos = ArrayList<ItemInfo<T>?>(mItemInfos.size)
//        for (i in 0..(mItemInfos.size - 1)) {
//            pendingItemInfos.add(null)
//        }
//        for (value in mItemInfos) {
//            value?.apply {
//                if (position >= 0) {
//                    while (pendingItemInfos.size <= position) {
//                        pendingItemInfos.add(null)
//                    }
//                    pendingItemInfos[value.position] = value
//                }
//            }
//        }
//        mItemInfos = pendingItemInfos
//    }
//
//    override fun saveState(): Parcelable? {
//        var state: Bundle? = null
//        if (mSavedState.size > 0) {
//            state = Bundle()
//            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
//            mSavedState.toArray(fss)
//            state.putParcelableArray("states", fss)
//        }
//        for (i in mItemInfos.indices) {
//            val f = mItemInfos[i]?.fragment
//            if (f != null && f.isAdded) {
//                if (state == null) {
//                    state = Bundle()
//                }
//                val key = "f$i"
//                mFragmentManager.putFragment(state, key, f)
//            }
//        }
//        return state
//    }
//
//    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//        if (state != null) {
//            val bundle = state as Bundle?
//            bundle!!.classLoader = loader
//            val fss = bundle.getParcelableArray("states")
//            mSavedState.clear()
//            mItemInfos.clear()
//            if (fss != null) {
//                for (i in fss.indices) {
//                    mSavedState.add(fss[i] as Fragment.SavedState)
//                }
//            }
//            val keys = bundle.keySet()
//            for (key in keys) {
//                if (key.startsWith("f")) {
//                    val index = Integer.parseInt(key.substring(1))
//                    val f = mFragmentManager.getFragment(bundle, key)
//                    if (f != null) {
//                        while (mItemInfos.size <= index) {
//                            mItemInfos.add(null)
//                        }
//                        f.setMenuVisibility(false)
//                        val iiNew = ItemInfo(f, getItemData(index), index)
//                        mItemInfos[index] = iiNew
//                    } else {
//                        Log.w(TAG, "Bad fragment at key $key")
//                    }
//                }
//            }
//        }
//    }
//
//    protected fun getCurrentPrimaryItem() = mCurrentPrimaryItem
//    protected fun getFragmentByPosition(position: Int): Fragment? {
//        if (position < 0 || position >= mItemInfos.size) return null
//        return mItemInfos[position]?.fragment
//    }
//
//    abstract fun getItemData(position: Int): T?
//
//    abstract fun dataEquals(oldData: T?, newData: T?): Boolean
//
//    abstract fun getDataPosition(data: T?): Int
//
//    class ItemInfo<D>(var fragment: Fragment, var data: D?, var position: Int)
//}