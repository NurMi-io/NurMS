/*
 * VoIP.ms SMS
 * Copyright (C) 2019 Michael Kourlas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kourlas.voipms_sms.sms.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import net.kourlas.voipms_sms.R
import net.kourlas.voipms_sms.utils.getJson
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

/**
 * Service used to test credentials for a particular account from VoIP.ms.
 *
 * This service is an IntentService rather than a JobIntentService because it
 * does not need to be run in the background.
 */
class VerifyCredentialsService : IntentService(
    VerifyCredentialsService::class.java.name) {
    private var error: String? = null

    override fun onHandleIntent(intent: Intent?) {
        // Verify that credentials are valid
        val credentialsValid = handleVerifyCredentials(intent)

        // Send broadcast
        val verifyCredentialsCompleteIntent = Intent(
            applicationContext.getString(
                R.string.verify_credentials_complete_action))
        verifyCredentialsCompleteIntent.putExtra(getString(
            R.string.verify_credentials_complete_error), error)
        verifyCredentialsCompleteIntent.putExtra(
            getString(R.string.verify_credentials_complete_valid),
            credentialsValid)
        applicationContext.sendBroadcast(verifyCredentialsCompleteIntent)
    }

    /**
     * Verifies the credentials of the VoIP.ms account using the parameters
     * from the specified intent.
     */
    private fun handleVerifyCredentials(intent: Intent?): Boolean {
        // Retrieve DIDs from VoIP.ms API
        try {
            // Terminate quietly if intent does not exist or does not contain
            // the sync action
            if (intent == null || intent.action != applicationContext.getString(
                    R.string.verify_credentials_action)) {
                return false
            }

            val email = intent.extras?.getString(
                applicationContext.getString(
                    R.string.verify_credentials_email))
            val password = intent.extras?.getString(
                applicationContext.getString(
                    R.string.verify_credentials_password))
            if (email == null || password == null) {
                return false
            }

            val response = getApiResponse(email, password)
            if (response != null) {
                return verifyResponse(response)
            }
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * Verifies the response of a getDIDsInfo call to the VoIP.ms API.
     *
     * @return Null if an error occurred.
     */
    private fun getApiResponse(email: String, password: String): JSONObject? {
        val retrieveDidsUrl =
            "https://www.voip.ms/api/v1/rest.php?" +
            "api_username=" +
            URLEncoder.encode(email, "UTF-8") + "&" +
            "api_password=" +
            URLEncoder.encode(password, "UTF-8") + "&" +
            "method=getDIDsInfo"
        val response: JSONObject
        try {
            response = getJson(applicationContext, retrieveDidsUrl)
        } catch (e: IOException) {
            error = applicationContext.getString(
                R.string.verify_credentials_error_api_request)
            return null
        } catch (e: JSONException) {
            error = applicationContext.getString(
                R.string.verify_credentials_error_api_parse)
            return null
        } catch (e: Exception) {
            error = applicationContext.getString(
                R.string.verify_credentials_error_unknown)
            return null
        }
        return response
    }

    /**
     * Parses the response of a getDIDsInfo from the VoIP.ms API to verify that
     * the response is valid.
     */
    private fun verifyResponse(response: JSONObject): Boolean {
        try {
            val status = response.getString("status")
            if (status != "success") {
                error = when (status) {
                    "invalid_credentials" -> getString(
                        R.string.verify_credentials_error_api_error_invalid_credentials)
                    else -> getString(
                        R.string.verify_credentials_error_api_error, status)
                }
                return false
            }

            return response.getJSONArray("dids") != null
        } catch (e: JSONException) {
            error = getString(
                R.string.verify_credentials_error_api_parse)
            return false
        }
    }

    companion object {
        /**
         * Verify credentials for a VoIP.ms account.
         */
        fun startService(context: Context, email: String, password: String) {
            val intent = Intent(context, VerifyCredentialsService::class.java)
            intent.action = context.getString(
                R.string.verify_credentials_action)
            intent.putExtra(
                context.getString(R.string.verify_credentials_email), email)
            intent.putExtra(
                context.getString(R.string.verify_credentials_password),
                password)
            context.startService(intent)
        }
    }
}