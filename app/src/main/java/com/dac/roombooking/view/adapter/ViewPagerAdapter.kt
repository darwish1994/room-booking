package com.dac.roombooking.view.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.request.RequestOptions
import com.dac.roombooking.R
import com.dac.roombooking.data.model.GlideApp

class ViewPagerAdapter(private val imageList: List<String>?, val url: String) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }

    override fun getCount(): Int {
        if (imageList != null)
            return imageList.size
        return 0
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = LayoutInflater.from(view.context).inflate(R.layout.slidingimages_layout, view, false)!!
        val imageView = imageLayout.findViewById(R.id.image) as ImageView
        GlideApp.with(view.context)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_image_holder)
                    .error(R.drawable.ic_broken_image)
            )
            .load(url + imageList!![position]).into(imageView)
        view.addView(imageLayout)

        return imageLayout
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

}