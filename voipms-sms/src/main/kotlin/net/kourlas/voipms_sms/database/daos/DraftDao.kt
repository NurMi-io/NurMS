/*
 * VoIP.ms SMS
 * Copyright (C) 2021 Michael Kourlas
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

package net.kourlas.voipms_sms.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.kourlas.voipms_sms.database.entities.Draft
import net.kourlas.voipms_sms.database.entities.Sms

@Dao
interface DraftDao {
    @Query("DELETE FROM ${Draft.TABLE_NAME}")
    suspend fun deleteAll()

    @Query(
        "DELETE FROM ${Draft.TABLE_NAME} WHERE ${Sms.COLUMN_DID} = :did AND ${Sms.COLUMN_CONTACT} = :contact")
    suspend fun deleteConversation(did: String, contact: String)

    @Query(
        "DELETE FROM ${Draft.TABLE_NAME} WHERE ${Draft.COLUMN_DID} NOT IN(:dids)")
    suspend fun deleteWithoutDids(dids: Set<String>)

    @Query(
        "SELECT * FROM ${Draft.TABLE_NAME} WHERE ${Sms.COLUMN_DID} = :did AND ${Sms.COLUMN_CONTACT} = :contact")
    suspend fun getConversation(did: String, contact: String): Draft?

    @Query(
        "SELECT * FROM ${Draft.TABLE_NAME} WHERE ${Draft.COLUMN_DID} IN(:dids) ORDER BY ${Draft.COLUMN_DID} DESC, ${Draft.COLUMN_CONTACT} DESC")
    suspend fun getConversations(dids: Set<String>): List<Draft>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(draft: Draft)
}