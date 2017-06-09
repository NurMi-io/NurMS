/*
 * VoIP.ms SMS
 * Copyright (C) 2017 Michael Kourlas
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

package net.kourlas.voipms_sms.backup

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import net.kourlas.voipms_sms.utils.subscribeToDidTopics


/**
 * Custom backup agent that does nothing except register for FCM DID topics.
 */
class CustomBackupAgent : BackupAgent() {
    override fun onBackup(oldState: ParcelFileDescriptor,
                          data: BackupDataOutput,
                          newState: ParcelFileDescriptor) {
        // Do nothing.
    }

    override fun onRestore(data: BackupDataInput, appVersionCode: Int,
                           newState: ParcelFileDescriptor) {
        // Do nothing.
    }

    override fun onRestoreFinished() {
        super.onRestoreFinished()

        // Subscribe to topics for current DIDs
        subscribeToDidTopics(applicationContext)
    }
}
