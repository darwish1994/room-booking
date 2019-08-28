package com.dac.roombooking.view.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.dac.roombooking.R
import com.dac.roombooking.data.model.GlideApp

class ViewPagerAdapter(val context: Context, val images: List<String>?, val url: String) : PagerAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }

    override fun getCount(): Int {
        if (images != null)
            return images.size
        return 0
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false)!!
        val imageView = imageLayout.findViewById(R.id.image) as ImageView
        GlideApp.with(context).load(url + images!![position]).into(imageView)
        view.addView(imageLayout)

        return imageLayout
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

}