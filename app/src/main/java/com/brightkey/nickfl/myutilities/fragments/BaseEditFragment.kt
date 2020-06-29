package com.brightkey.nickfl.myutilities.fragments

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.helpers.DateFormatters
import com.brightkey.nickfl.myutilities.models.UtilityEditModel
import com.brightkey.nickfl.myutilities.viewmodel.BaseViewModel
import com.brightkey.nickfl.myutilities.viewmodel.BaseViewModelFactory
import timber.log.Timber
import java.util.*

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/21/2017
 */
abstract class BaseEditFragment(private var billType: String = ""
) : Fragment() {

    lateinit var mTag: FragmentScreen

    private lateinit var model: BaseViewModel
    private var doEdit: Boolean = false

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

    // this property used in fragments
    var exitListener: ExitFragmentListener? = null

    var getBillType: String = "Utility"
        get() = model.getBillType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = BaseViewModelFactory(billType)
        model = ViewModelProvider(this, factory).get(BaseViewModel::class.java)
    }

    fun getArguments(bundle: Bundle?) {
        val args = bundle?.getParcelable<UtilityEditModel>("editBillUtility")
        args?.let{
            doEdit = it.edit
            model.setEditIndex(it.index)
        }
    }

    private fun billForUtility() {
        model.fetchBill()
        fillInStatement()
    }

    fun startUp(newUtilityInit: (()->Unit)? = null) {
        initMainStatement()
        if (!doEdit) {
            changeDateVisibility(true)
            newUtilityInit?.let { it() }
        } else {
            billForUtility()
        }
    }

    fun setupMainStatement(view: View, listener: View.OnClickListener) {
        val layoutStatement = view.findViewById<View>(R.id.includeStatementData)

        val statement = layoutStatement.findViewById<View>(R.id.layoutStatementDate)
        addStatementDay = statement.findViewById(R.id.buttonDateAdd)
        addStatementDay?.setOnClickListener(listener)
        billDate = statement.findViewById(R.id.textDateViewDate)
        val nameState = statement.findViewById<TextView>(R.id.textDateViewName)
        nameState.setText(R.string.utility_statement_date)

        val due = layoutStatement.findViewById<View>(R.id.layoutDueDate)
        addDueDay = due.findViewById(R.id.buttonDateAdd)
        addDueDay.setOnClickListener(listener)
        dueDate = due.findViewById(R.id.textDateViewDate)
        val nameDue = due.findViewById<TextView>(R.id.textDateViewName)
        nameDue.setText(R.string.utility_due_date)

        val price = layoutStatement.findViewById<View>(R.id.layoutPrice)
        paymentTotal = price.findViewById(R.id.textPriceViewAmount)
    }

    fun removeBill(): Int {
        return model.removeCurrentBill()
    }

    private fun saveMainStatement(utilityType: String) {
        val cal = GregorianCalendar()
        model.utilityType = utilityType
        model.setUtilityDatePaid(cal.time)
        model.setUtilityDueDate(DateFormatters.dateFromString(dueDate?.text.toString()))
        model.setUtilityBillDate(DateFormatters.dateFromString(billDate?.text.toString()))
    }

    private fun initMainStatement() {
        val cal = GregorianCalendar()
        billDate?.text = DateFormatters.dateStringFromCalendar(cal)
        dueDate?.text = DateFormatters.dateStringFromCalendar(cal)
        paymentTotal.setText("")
        paidAmount0.setText("")
        paidAmount1?.setText("")
        paidAmount2?.setText("")
    }

    private fun changeDateVisibility(show: Boolean) {
        val visible = if (show) View.VISIBLE else View.INVISIBLE
        addStatementDay?.visibility = visible
        addDueDay.visibility = visible
    }

    private fun fillInStatement() {
        changeDateVisibility(false)
        addPayment.setText(R.string.hydro_update_payment)
        billDate?.text = model.billDate
        dueDate?.text = model.dueDate
        paymentTotal.setText(model.amountDue)
        paidAmount0.setText(model.amountType0)
        paidAmount1?.setText(model.amountType1)
        paidAmount2?.setText(model.amountType2)
    }

    fun saveFullBill(): Boolean {
        val check = ArrayList<EditText>()
        check.add(paymentTotal)
        check.add(paidAmount0)
        paidAmount1?.run { check.add(paidAmount1!!) }
        paidAmount2?.run { check.add(paidAmount2!!) }
        if (!model.validateData(check)) {
            val line = Exception().stackTrace[0].lineNumber + 1
            Timber.e("[$line] validateData failed!")
            showError()
            return false
        }
        if (!doEdit) {
            saveMainStatement(billType)
        }
        model.setAmountDue(paymentTotal)
        model.setAmountType0(paidAmount0)
        paidAmount1?.run { model.setAmountType1(paidAmount1!!) }
        paidAmount2?.run { model.setAmountType2(paidAmount2!!) }
        model.updateBillInRealm()
        return true
    }

    open fun editableChanged(editable: Editable): Double {
        return model.editableChanged(editable)
    }

    var accountNumber: String? = null
        get() = model.accountNumber

    open fun unit0(paid: Double): Double {
        return model.unit0(paid)
    }

    open fun unit1(paid: Double): Double {
        return model.unit1(paid)
    }

    open fun unit2(paid: Double): Double {
        return model.unit2(paid)
    }

    open fun unitWaste1(waste: Double): Double {
        return model.unitWastePaid(waste)
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
}
