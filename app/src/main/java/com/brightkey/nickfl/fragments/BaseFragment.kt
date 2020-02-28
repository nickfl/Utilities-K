package com.brightkey.nickfl.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.entities.BaseUtility
import com.brightkey.nickfl.entities.BaseUtility_
import com.brightkey.nickfl.entities.ConfigEntity
import com.brightkey.nickfl.helpers.DateFormatters
import com.brightkey.nickfl.helpers.ObjectBoxHelper
import com.brightkey.nickfl.helpers.PeriodManager
import com.brightkey.nickfl.myutilities.R
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/21/2017
 */
abstract class BaseFragment : Fragment() {

    val TAG = this.javaClass.getSimpleName()

    var mTag: FragmentScreen? = null
    private var addDueDay: Button? = null
    var currentDateView: TextView? = null // Due or Statement Date
    var addPayment: Button? = null        // Add or Edit payment
    var addStatementDay: Button? = null
    var billDate: TextView? = null
    var paymentTotal: EditText? = null    // total amount to pay for a utility
    var dueDate: TextView? = null

    var paidAmount0: EditText? = null
    var paidAmount1: EditText? = null
    var paidAmount2: EditText? = null

    var doEdit: Boolean? = null
    var editIndex: Int = 0

    var entity: ConfigEntity? = null
    var utilityBox: Box<BaseUtility>? = null
    var editUtility: BaseUtility? = null

    var exitListener: ExitFragmentListener? = null

    init {
        utilityBox = ObjectBoxHelper.shared().utilityBox
        doEdit = false
        editIndex = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onCreate()")
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is BaseFragment.ExitFragmentListener) {
            exitListener = activity
        } else {
            throw RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    fun billForUtility(item: ConfigEntity, index: Int): BaseUtility {
        val utils = utilityBox!!.query()
                .equal(BaseUtility_.utilityType, item.utilityIcon!!).build().find()
                .filter { PeriodManager.shared.isDateInPeriod(it.datePaid) }
        val utility = utils[index]
        fillInStatement(utility)
        return utility
    }

    fun setupMainStatement(view: View, listener: View.OnClickListener) {
        val layoutStatement = view.findViewById<View>(R.id.includeStatementData)

        val statement = layoutStatement.findViewById<View>(R.id.includeSDate)
        addStatementDay = statement.findViewById(R.id.buttonDateAdd)
        addStatementDay?.setOnClickListener(listener)
        billDate = statement.findViewById(R.id.textDateViewDate)
        val nameState = statement.findViewById<TextView>(R.id.textDateViewName)
        nameState.setText(R.string.utility_statement_date)

        val due = layoutStatement.findViewById<View>(R.id.includeDDate)
        addDueDay = due.findViewById(R.id.buttonDateAdd)
        addDueDay?.setOnClickListener(listener)
        dueDate = due.findViewById(R.id.textDateViewDate)
        val nameDue = due.findViewById<TextView>(R.id.textDateViewName)
        nameDue.setText(R.string.utility_due_date)

        val price = layoutStatement.findViewById<View>(R.id.includePrice)
        paymentTotal = price.findViewById(R.id.textPriceViewAmount)
    }

    fun saveMainStatement(utility: BaseUtility, utilityType: String) {
        val cal = GregorianCalendar()
        utility.utilityType = utilityType
        utility.datePaid = cal.time
        utility.dueDate = DateFormatters.dateFromString(dueDate?.text.toString())
        utility.billDate = DateFormatters.dateFromString(billDate?.text.toString())
    }

    fun initMainStatement() {
        val cal = GregorianCalendar()
        billDate?.text = DateFormatters.dateStringFromCalendar(cal)
        dueDate?.text = DateFormatters.dateStringFromCalendar(cal)
        paymentTotal?.setText("")
        if (paidAmount0 != null) {
            paidAmount0?.setText("")
        }
        if (paidAmount1 != null) {
            paidAmount1?.setText("")
        }
        if (paidAmount2 != null) {
            paidAmount2?.setText("")
        }
    }

    fun changeDateVisibility(show: Boolean?) {
        val visible = if (show!!) View.VISIBLE else View.INVISIBLE
        addStatementDay?.visibility = visible
        addDueDay?.visibility = visible
    }

    private fun fillInStatement(utility: BaseUtility) {
        changeDateVisibility(false)
        addPayment?.setText(R.string.hydro_update_payment)
        billDate?.text = utility.getBillDate()
        dueDate?.text = utility.getDueDate()
        paymentTotal?.setText(utility.getAmountDue())
        if (paidAmount0 != null) {
            paidAmount0?.setText(utility.getAmountType0())
        }
        if (paidAmount1 != null) {
            paidAmount1?.setText(utility.getAmountType1())
        }
        if (paidAmount2 != null) {
            paidAmount2?.setText(utility.getAmountType2())
        }
    }

    fun amountFrom(payment: TextView): Double {
        var text = payment.text.toString()
        if (text[0] == '$') {
            text = text.substring(1)
        }
        return java.lang.Double.parseDouble(text)
    }

    fun validateData(fields: MutableList<EditText>): Boolean {
        fields.add(paymentTotal!!)
        for (one in fields) {
            if (one.text == null || one.text.toString().isEmpty()) {
                return false
            }
            try {
                java.lang.Double.parseDouble(one.text.toString())
            } catch (ex: Exception) {
                return false
            }

        }
        return true
    }

    fun showError() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.myErrorDialog))
        builder.setMessage(R.string.not_valid_fields)
                .setTitle(R.string.oops)
                .setIcon(R.drawable.error)
                .setNegativeButton(R.string.Done, null)
        val alert = builder.create()
        alert.show()
    }

    //-------------------------------
    interface ExitFragmentListener {
        fun onFragmentExit()
    }
}
