package com.spark.peak.utlis

import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by 李昊 on 2017/12/22.
 */

/**
 * Implementation of [PagerAdapter] that
 * represents each page as a [Fragment] that is persistently
 * kept in the fragment manager as long as the user can return to the page.
 *
 *
 * This version of the pager is best for use when there are a handful of
 * typically more static fragments to be paged through, such as a set of tabs.
 * The fragment of each page the user visits will be kept in memory, though its
 * view hierarchy may be destroyed when not visible.  This can result in using
 * a significant amount of memory since fragment instances can hold on to an
 * arbitrary amount of state.  For larger sets of pages, consider
 * [FragmentStatePagerAdapter].
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter.
 *
 *
 * Here is an spark implementation of a pager containing fragments of
 * lists:
 *
 * {@sample frameworks/support/samples/Support4Demos/src/com/spark/android/supportv4/app/FragmentPagerSupport.java
 * *      complete}
 *
 *
 * The `R.layout.fragment_pager` resource of the top-level fragment is:
 *
 * {@sample frameworks/support/samples/Support4Demos/res/layout/fragment_pager.xml
 * *      complete}
 *
 *
 * The `R.layout.fragment_pager_list` resource containing each
 * individual fragment's layout is:
 *
 * {@sample frameworks/support/samples/Support4Demos/res/layout/fragment_pager_list.xml
 * *      complete}
 */
abstract class FragmentPagerAdapter(private val mFragmentManager: FragmentManager) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID) {
            throw IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val itemId = getItemId(position)

        // Do we already have this fragment?
        val name = makeFragmentName(container.id, itemId)
        var fragment: Fragment? = mFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            if (DEBUG) Log.v(TAG, "Attaching item #$itemId: f=$fragment")
            mCurTransaction!!.attach(fragment)
        } else {
            fragment = getItem(position)
            if (DEBUG) Log.v(TAG, "Adding item #$itemId: f=$fragment")
            mCurTransaction!!.add(container.id, fragment,
                    makeFragmentName(container.id, itemId))
        }
        if (fragment !== mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        if (DEBUG)
            Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + `object`
                    + " v=" + (`object` as Fragment).view)
        mCurTransaction!!.detach(`object` as Fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurrentPrimaryItem!!.userVisibleHint = false
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commit()
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    /**
     * Return a unique identifier for the item at the given position.
     *
     *
     * The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun makeFragmentName(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }

    companion object {
        private val TAG = "FragmentPagerAdapter"
        private val DEBUG = false
    }
}
