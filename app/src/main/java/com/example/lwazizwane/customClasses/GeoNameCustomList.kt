package com.example.lwazizwane.customClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.lwazizwane.R
import com.example.lwazizwane.classes.GeoName
import java.util.ArrayList

class GeoNameCustomList(private val dataSet: ArrayList<GeoName>, var mContext: Context) : ArrayAdapter<GeoName?>(mContext, R.layout.cust_listview_geoname, dataSet as List<GeoName?>), View.OnClickListener {


    private class ViewHolder {
        var name: TextView? = null

    }

    override fun onClick(v: View) {

    }

    private var lastPosition = -1
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var convertView = convertView
        val GeoName = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        val result: View?
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.cust_listview_geoname, parent, false)
            viewHolder.name = convertView!!.findViewById<View>(R.id.date) as TextView

            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder

        }

        lastPosition = position
        viewHolder.name!!.text = GeoName!!.name + ", "+ GeoName.lat + ", " + GeoName.lng


        return convertView
    }
    


}
