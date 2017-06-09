/*
 * VoIP.ms SMS
 * Copyright (C) 2015-2017 Michael Kourlas
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

package net.kourlas.voipms_sms.utils

import android.provider.ContactsContract
import java.text.MessageFormat

/**
 * Returns a string consisting only of the digits in the specified string.
 *
 * @param str The specified string.
 * @return The digits in the specified string.
 */
fun getDigitsOfString(str: String): String {
    val stringBuilder = StringBuilder()
    for (char in str) {
        if (Character.isDigit(char)) {
            stringBuilder.append(char)
        }
    }
    return stringBuilder.toString()
}

/**
 * Formats the specified phone number in the form (XXX) XXX-XXXX if it is
 * ten digits in length (or eleven with a leading one). Otherwise, simply
 * returns the original phone number.
 *
 * @param phoneNumber The specified phone number.
 * @return The formatted phone number.
 */
fun getFormattedPhoneNumber(phoneNumber: String): String {
    if (phoneNumber.length == 10) {
        val phoneNumberFormat = MessageFormat("({0}) {1}-{2}")
        val phoneNumberArray = arrayOf(phoneNumber.substring(0, 3),
                                       phoneNumber.substring(3, 6),
                                       phoneNumber.substring(6))
        return phoneNumberFormat.format(phoneNumberArray)
    } else if (phoneNumber.length == 11 && phoneNumber[0] == '1') {
        val phoneNumberFormat = MessageFormat("({0}) {1}-{2}")
        val phoneNumberArray = arrayOf(phoneNumber.substring(1, 4),
                                       phoneNumber.substring(4, 7),
                                       phoneNumber.substring(7))
        return phoneNumberFormat.format(phoneNumberArray)
    }

    return phoneNumber
}

/**
 * Returns the string representation of the specified phone number type.
 *
 * @param type The specified phone number type.
 * @return The string representation of the specified phone number type.
 */
fun getPhoneNumberType(type: Int): String {
    when (type) {
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> return "Home"
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> return "Mobile"
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> return "Work"
        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME -> return "Home Fax"
        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK -> return "Work Fax"
        ContactsContract.CommonDataKinds.Phone.TYPE_MAIN -> return "Main"
        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> return "Other"
        ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM -> return "Custom"
        ContactsContract.CommonDataKinds.Phone.TYPE_PAGER -> return "Pager"
        else -> return ""
    }
}

/**
 * Throws an exception if the specified phone number contains anything other
 * than numbers.
 *
 * @param value The specified phone number.
 */
fun validatePhoneNumber(value: String) {
    if (getDigitsOfString(value) != value) {
        throw IllegalArgumentException(
            "value must consist only of numbers")
    }
}
