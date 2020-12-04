package com.example.lwazizwane.customClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.lwazizwane.R
import com.example.lwazizwane.classes.ForecastGrouping
import java.util.*

class CustomList(private val dataSet: ArrayList<ForecastGrouping>, var mContext: Context) : ArrayAdapter<ForecastGrouping?>(mContext, R.layout.cust_listview, dataSet as List<ForecastGrouping?>), View.OnClickListener {

    // View lookup cache
    private class ViewHolder {
        var date: TextView? = null
        var icon: ImageView? = null
        var temp_max: TextView? = null
    }

    override fun onClick(v: View) {
        val position = v.tag as Int
        val `object`: Any? = getItem(position)
        val ForecastDataModel = `object` as ForecastGrouping?
    }

    private var lastPosition = -1
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var convertView = convertView
        val ForecastGrouping = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        val result: View?
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.cust_listview, parent, false)
            viewHolder.date = convertView!!.findViewById<View>(R.id.date) as TextView
            viewHolder.icon = convertView.findViewById<View>(R.id.icon) as ImageView
            viewHolder.temp_max = convertView.findViewById<View>(R.id.temp_max) as TextView
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        lastPosition = position
        viewHolder.date!!.text = ForecastGrouping!!.date
        viewHolder.temp_max!!.text = ForecastGrouping.temp_Max
        viewHolder.icon!!.setImageResource(ForecastGrouping.icon)

        return convertView!!
    }

}
