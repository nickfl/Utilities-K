package com.brightkey.nickfl.myutilities.fragments

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.entities.ConfigEntity
import com.brightkey.nickfl.myutilities.entities.UtilityBillModel
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import timber.log.Timber
import java.util.*

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/21/2017
 */
abstract class BaseFragment(billType: String = ""  // Constant.HeatType
) : Fragment() {

    lateinit var mTag: FragmentScreen
    var entity = if (billType.isNotEmpty()) MyUtilitiesApplication.getConfigEntityForType(billType) else null

    private lateinit var addDueDay: Button
    var currentDateView: TextView? = null  // Due or Statement Date
    lateinit var addPayment: Button        // Add or Edit payment
    var addStatementDay: Button? = null
    var billDate: TextView? = null
    private lateinit var paymentTotal: EditText   // total amount to pay for a utility
    var dueDate: TextView? = null

    lateinit var paidAmount0: EditText
    var paidAmount1: EditText? = null
    var paidAmount2: EditText? = null

    var doEdit: Boolean = false
    var editIndex: Int = 0

    var editUtility: UtilityBillModel? = null

    var exitListener: ExitFragmentListener? = null

    fun billForUtility(item: ConfigEntity, index: Int): UtilityBillModel {
        val utils = RealmHelper.utilitiesForType(item.utilityIcon!!)
        val utility = utils[index]
        fillInStatement(utility)
        return utility.copy()
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
        addDueDay.setOnClickListener(listener)
        dueDate = due.findViewById(R.id.textDateViewDate)
        val nameDue = due.findViewById<TextView>(R.id.textDateViewName)
        nameDue.setText(R.string.utility_due_date)

        val price = layoutStatement.findViewById<View>(R.id.includePrice)
        paymentTotal = price.findViewById(R.id.textPriceViewAmount)
    }

    private fun saveMainStatement(utility: UtilityBillModel, utilityType: String) {
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
        paymentTotal.setText("")
        paidAmount0.setText("")
        paidAmount1?.setText("")
        paidAmount2?.setText("")
    }

    fun changeDateVisibility(show: Boolean?) {
        val visible = if (show!!) View.VISIBLE else View.INVISIBLE
        addStatementDay?.visibility = visible
        addDueDay.visibility = visible
    }

    private fun fillInStatement(utility: UtilityBillModel) {
        changeDateVisibility(false)
        addPayment.setText(R.string.hydro_update_payment)
        billDate?.text = utility.getBillDate()
        dueDate?.text = utility.getDueDate()
        paymentTotal.setText(utility.getAmountDue())
        paidAmount0.setText(utility.getAmountType0())
        paidAmount1?.setText(utility.getAmountType1())
        paidAmount2?.setText(utility.getAmountType2())
    }

    private fun amountFrom(payment: TextView): Double {
        var text = payment.text.toString()
        if (text[0] == '$') {
            text = text.substring(1)
        }
        return java.lang.Double.parseDouble(text)
    }

    private fun validateData(fields: MutableList<EditText>): Boolean {
        for (one in fields) {
            if (one.text.isNullOrBlank()) {
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

    fun saveFullBill(): Boolean {
        val check = ArrayList<EditText>()
        check.add(paymentTotal)
        check.add(paidAmount0)
        paidAmount1?.run { check.add(paidAmount1!!) }
        paidAmount2?.run { check.add(paidAmount2!!) }
        if (!validateData(check)) {
            val line = Exception().stackTrace[0].lineNumber + 1
            Timber.e("[$line] validateData failed!")
            showError()
            return false
        }
        val utility = if (doEdit) editUtility else UtilityBillModel()
        utility?.let { model ->
            if (!doEdit) {
                saveMainStatement(model, Constants.PhoneType)
            }
            model.amountDue = amountFrom(paymentTotal)
            model.amountType0 = amountFrom(paidAmount0)
            paidAmount1?.run { model.amountType1 = amountFrom(paidAmount1!!) }
            paidAmount2?.run { model.amountType2 = amountFrom(paidAmount2!!) }
            RealmHelper.updateBill(model)
        }
        return true
    }

    private fun showError() {
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
