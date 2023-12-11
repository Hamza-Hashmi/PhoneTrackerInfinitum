package com.phone.tracker.locate.number.app.repo

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.phone.tracker.locate.number.app.dataModel.ContactInfo

class ContactsRepo(private var context: Context) {
    lateinit var userContactsList : ArrayList<ContactInfo>

    @SuppressLint("Range")
    fun getContactList() {
        userContactsList = ArrayList()
        var c = 0
        val contentResolver : ContentResolver = context.getContentResolver()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC "
        )
        var contactLN: String? = "0"
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                var number: String? = null
                val contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt() > 0
                ) {
                    val secondCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactID),
                        null
                    )
                    while (secondCursor!!.moveToNext()) {
                        number =
                            secondCursor.getString(secondCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        if (number == contactLN) {
                        } else {
                            contactLN = number
                            val type = secondCursor.getInt(secondCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                            Log.e("type ", type.toString())
                            when (type) {
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {

                                    c++
                                    val contact = ContactInfo(contactLN, contactName, contactID)
                                    userContactsList.add(contact)
                                }
                            }
                        }
                    }
                    secondCursor.close()
                }
            }
        }
    }

}