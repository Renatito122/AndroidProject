package com.example.fyreapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fyreapp.databinding.FragmentDashboardBinding
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.pay.PayClient
import com.google.android.gms.wallet.*
//import kotlinx.android.synthetic.main.activity_checkout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



class DashboardFragment : Fragment() {
    private val addToGoogleWalletRequestCode = 1000
    private val SHIPPING_COST_CENTS = 9 * PaymentsUtil.CENTS.toLong()

    /**
     * A client for interacting with the Google Pay API.
     *
     * @see [PaymentsClient](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient)
     */
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var googlePayButton: View
    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject
    private lateinit var addToGoogleWalletButton: View
    private val model: DashboardViewModel by viewModels()

    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup buttons
        googlePayButton = binding.googlePayButton.root
        googlePayButton.setOnClickListener { requestPayment() }


        // Check Google Pay availability
        model.canUseGooglePay.observe(viewLifecycleOwner, Observer(::setGooglePayAvailable))
        model.canSavePasses.observe(viewLifecycleOwner, Observer(::setAddToGoogleWalletAvailable))
        return root
    }
    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this.context,
                "Unfortunately, Google Pay is not available on this phone.",
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     * If the Google Wallet API is available, show the button to Add to Google Wallet. Please adjust to fit
     * in with your current user flow.
     *
     * @param available
     */
    private fun setAddToGoogleWalletAvailable(available: Boolean) {
        if (available) {
            binding.passContainer.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this.context,
                "Unfortunately, Google Pay is not available on this phone.",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val dummyPriceCents = 100L
        val shippingCostCents = 900L
        val task = model.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }
                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                    " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.isClickable = true
        }
    }

    // Handle potential conflict from calling loadPaymentData
    private val resolvePaymentForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            result: ActivityResult ->
        when (result.resultCode) {
            RESULT_OK ->
                result.data?.let { intent ->
                    PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                }

            RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            Toast.makeText(this.context, "Successfully received payment data", Toast.LENGTH_LONG).show()

            // Logging token string.
            Log.d("Google Pay token", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }

    /*private fun requestSavePass() {

        // Disables the button to prevent multiple clicks.
        addToGoogleWalletButton.isClickable = false

        model.savePassesJwt(model.genericObjectJwt, this, addToGoogleWalletRequestCode)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == addToGoogleWalletRequestCode) {
            when (resultCode) {
                RESULT_OK -> Toast
                    .makeText(this.context, "The pass was added successfully!", Toast.LENGTH_LONG)
                    .show()

                RESULT_CANCELED -> {
                    // Save canceled
                }

                PayClient.SavePassesResult.SAVE_ERROR -> data?.let { intentData ->
                    val apiErrorMessage = intentData.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE)
                    handleError(resultCode, apiErrorMessage)
                }

                else -> handleError(
                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!"
                )
            }

            // Re-enables the Google Pay payment button.
            addToGoogleWalletButton.isClickable = true

        }
    }
}