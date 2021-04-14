package com.brightkey.nickfl.myutilities.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.fragments.inflate
import com.brightkey.nickfl.myutilities.models.TimeListModel
import timber.log.Timber

class TimeListAdapter(private val mActivity: Activity, private val paidList: List<TimeListModel>/*, TimeListAdapter.AdapterDashboardInterface rowListener*/) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.d("[$line] DashboardAdapter")
//        this.mListener = rowListener;
    }

    //region RecyclerView Methods
    override fun getItemCount(): Int = paidList.size
    override fun getItemViewType(position: Int): Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.list_list_color_item)
        return TimeListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindDashHolder(holder as TimeListViewHolder, position)
        val listItem = paidList[position]
        bindClickListeners(holder, listItem, position)
    }

    private fun bindDashHolder(dashHolder: TimeListViewHolder, index: Int) {
        dashHolder.totalPaid.text = String.format("$%.2f", this.paidList[index].totalPaid)
        val dueToPay = customString("Due Date: " + this.paidList[index].duePaid, 9)
        dashHolder.dueToPay.text = dueToPay
        val whenPaid = customString("Paid Date: " + this.paidList[index].whenPaid, 10)
        dashHolder.whenPaid.text = whenPaid
        val amount = customString(String.format("Amount: $%.2f", this.paidList[index].nowPaid), 7)
        dashHolder.nowPaid.text = amount
        val color = Color.parseColor(this.paidList[index].utilityColor)
        dashHolder.line.setBackgroundColor(color)
        dashHolder.dots.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
        dashHolder.dots.setOnClickListener {
            val screenType = paidList[index].screenType()
            (mActivity as MainActivity).editFragment(screenType, paidList.size - index - 1)
        }
    }

    private fun customString(input: String, split: Int): SpannableString {
        val result = SpannableString(input)
        result.setSpan(StyleSpan(Typeface.BOLD), 0, split, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return result
    }

    private fun bindClickListeners(listViewHolder: TimeListViewHolder, userItem: TimeListModel, position: Int) {}
    //endregion

    //region View Holders
    internal inner class TimeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dueToPay: TextView = itemView.findViewById<View>(R.id.textViewDuePaid) as TextView    // due date
        val nowPaid: TextView = itemView.findViewById<View>(R.id.textViewAmountPaid) as TextView  // amount paid this time
        val totalPaid: TextView = itemView.findViewById<View>(R.id.textViewTotalPaid) as TextView // total paid for this utility
        val whenPaid: TextView = itemView.findViewById<View>(R.id.textViewDataPaid) as TextView   // date when paid
        val line: View = itemView.findViewById(R.id.viewLine) as View
        val dots: ImageView = itemView.findViewById<View>(R.id.imageViewMore) as ImageView
    }
    //endregion
}
