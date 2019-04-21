package com.brightkey.nickfl.adapters

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.brightkey.nickfl.models.DashboardModel
import com.brightkey.nickfl.myutilities.R

import timber.log.Timber

class DashboardAdapter(private val mActivity: Activity, private val utilityList: List<DashboardModel>, rowListener: DashboardAdapter.AdapterDashboardInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mListener: AdapterDashboardInterface

    init {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.d("[$line] DashboardAdapter")
        this.mListener = rowListener
    }

    //region RecyclerView Methods
    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_dash_item, parent, false)
        return DashboardsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindDashHolder(holder as DashboardsViewHolder, position)
        val utilityItem = utilityList[position]
        bindClickListeners(holder, utilityItem, position)
    }

    fun cleanUtilities() {
        for (data in utilityList) {
            data.resetData()
        }
    }

    private fun bindDashHolder(dashHolder: DashboardsViewHolder, index: Int) {
        val res = this.utilityList[index].iconResource()
        dashHolder.utilityIcon.setImageResource(res)
        dashHolder.utilityType.text = this.utilityList[index].utilityType
        dashHolder.utilityVendor.text = this.utilityList[index].utilityVendor
        val color = Color.parseColor(this.utilityList[index].vendorColor)
        dashHolder.utilityVendor.setTextColor(color)
        dashHolder.accountNumber.text = "Account: " + this.utilityList[index].accountNumber
        if (this.utilityList[index].totalUnits > 0) {
            dashHolder.totalUnits.visibility = View.VISIBLE
            dashHolder.totalUnits.text = "Used this year: " + this.utilityList[index].totalUnits + " " + this.utilityList[index].unitType
        } else {
            dashHolder.totalUnits.visibility = View.GONE
        }
        dashHolder.totalPaid.setText(String.format("Paid this year: $%.2f", this.utilityList[index].totalPaid))
        dashHolder.cardView.setOnClickListener {
            if (utilityList[index].totalPaid > 0.0) {
                mListener.utilityPressed(utilityList[index].utilityIcon)
            } else {
                Toast.makeText(mActivity, "No data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindClickListeners(itemViewHolder: DashboardsViewHolder, listItem: DashboardModel, position: Int) {}

    override fun getItemCount(): Int {
        return utilityList.size
    }
    //endregion

    //region View Holders
    internal inner class DashboardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView: CardView
        val utilityIcon: ImageView
        val utilityType: TextView
        val utilityVendor: TextView
        val accountNumber: TextView
        val totalUnits: TextView
        val totalPaid: TextView

        init {
            this.cardView = itemView.findViewById<View>(R.id.dashCardView) as CardView
            this.utilityIcon = itemView.findViewById<View>(R.id.imageViewIcon) as ImageView
            this.utilityType = itemView.findViewById<View>(R.id.textViewDashboard) as TextView
            this.utilityVendor = itemView.findViewById<View>(R.id.textViewVendor) as TextView
            this.accountNumber = itemView.findViewById<View>(R.id.textViewAccount) as TextView
            this.totalUnits = itemView.findViewById<View>(R.id.textViewTotalUnits) as TextView
            this.totalPaid = itemView.findViewById<View>(R.id.textViewTotalPaid) as TextView
        }
    }
    //endregion

    //------------------------------------------
    interface AdapterDashboardInterface {
        fun utilityPressed(itemId: String)
    }
}