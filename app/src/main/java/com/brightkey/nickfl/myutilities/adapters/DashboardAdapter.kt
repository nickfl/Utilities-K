package com.brightkey.nickfl.myutilities.adapters

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.fragments.inflate
import com.brightkey.nickfl.myutilities.models.DashboardModel
import timber.log.Timber

class DashboardAdapter(
    private val mActivity: Activity,
    private val utilityList: List<DashboardModel>,
    rowListener: AdapterDashboardInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mListener: AdapterDashboardInterface

    init {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.d("[$line] DashboardAdapter")
        this.mListener = rowListener
    }

    //region RecyclerView Methods
    override fun getItemCount(): Int = utilityList.size
    override fun getItemViewType(position: Int): Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.list_dash_item)
        return DashboardsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindDashHolder(holder as DashboardsViewHolder, position)
        val utilityItem = utilityList[position]
        bindClickListeners(holder, utilityItem, position)
    }

    fun cleanUtilities() {
        utilityList.forEach { it.resetData() }
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
            dashHolder.totalUnits.text =
                "Used this period: " + this.utilityList[index].totalUnits + " " + this.utilityList[index].unitType
        } else {
            dashHolder.totalUnits.visibility = View.GONE
        }
        dashHolder.totalPaid.text =
            String.format("Paid this period: $%.2f", this.utilityList[index].totalPaid)
        dashHolder.cardView.setOnClickListener {
            if (utilityList[index].totalPaid > 0.0) {
                mListener.utilityPressed(utilityList[index].utilityIcon)
            } else {
                Toast.makeText(mActivity, "No data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindClickListeners(
        itemViewHolder: DashboardsViewHolder,
        listItem: DashboardModel,
        position: Int
    ) {
    }
    //endregion

    //region View Holders
    internal inner class DashboardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView: CardView = itemView.findViewById<View>(R.id.dashCardView) as CardView
        val utilityIcon: ImageView = itemView.findViewById<View>(R.id.imageViewIcon) as ImageView
        val utilityType: TextView = itemView.findViewById<View>(R.id.textViewDashboard) as TextView
        val utilityVendor: TextView = itemView.findViewById<View>(R.id.textViewVendor) as TextView
        val accountNumber: TextView = itemView.findViewById<View>(R.id.textViewAccount) as TextView
        val totalUnits: TextView = itemView.findViewById<View>(R.id.textViewTotalUnits) as TextView
        val totalPaid: TextView = itemView.findViewById<View>(R.id.textViewTotalPaid) as TextView
    }
    //endregion

    //------------------------------------------
    interface AdapterDashboardInterface {
        fun utilityPressed(itemId: String)
    }
}