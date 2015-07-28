package com.RSen.OpenMic.Pheonix;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import org.apache.commons.codec.language.DoubleMetaphone;

public class ContactSearcher {
    public static String phoneticNameToValue(Context context, String checkName,
                                             Boolean email) {
        try {
            if (checkName.matches("")) {
                return null;
            }
            final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
            doubleMetaphone.setMaxCodeLen(15);
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                    String name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (doubleMetaphone.encode(name).matches(
                            doubleMetaphone.encode(checkName))) {
                        if (email) {
                            String emailAddress = contactToEmail(cur, cr, id);
                            if (emailAddress != null) {
                                return emailAddress;
                            }
                        } else {
                            String number = contactToNumber(cur, cr, id);
                            if (number != null) {
                                return number;
                            }
                        }
                    }
                }
                // try again with startswith instead of matches
                cur.moveToFirst();
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                    String name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (doubleMetaphone.encode(name).startsWith(
                            doubleMetaphone.encode(checkName))) {
                        if (email) {
                            String emailAddress = contactToEmail(cur, cr, id);
                            if (emailAddress != null) {
                                return emailAddress;
                            }
                        } else {
                            String number = contactToNumber(cur, cr, id);
                            if (number != null) {
                                return number;
                            }
                        }
                    }
                }
                // try again with contains
                cur.moveToFirst();
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                    String name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (doubleMetaphone.encode(name).contains(
                            doubleMetaphone.encode(checkName))) {

                        if (email) {
                            String emailAddress = contactToEmail(cur, cr, id);
                            if (emailAddress != null) {
                                return emailAddress;
                            }
                        } else {
                            String number = contactToNumber(cur, cr, id);
                            if (number != null) {
                                return number;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
        }

        return null;
    }

    public static String phoneticNameToNumber(Context context, String checkName) {
        return phoneticNameToValue(context, checkName, false);
    }

    public static String phoneticNameToEmail(Context context, String checkName) {
        return phoneticNameToValue(context, checkName, true);
    }

    private static String contactToNumber(Cursor cur, ContentResolver cr,
                                          String id) {
        try {
            if (Integer.parseInt(cur.getString(cur
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur;

                pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[]{id}, null
                );

                // first search for default number
                while (pCur.moveToNext()) {

                    int defaultIfGreaterThanZero = pCur
                            .getInt(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (defaultIfGreaterThanZero > 0) {
                        return pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                }
                pCur.moveToPosition(-1);
                while (pCur.moveToNext()) {
                    return pCur
                            .getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                pCur.close();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static String contactToEmail(Cursor cur, ContentResolver cr,
                                         String id) {
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{id}, null);
        // first search for default number
        while (emailCur.moveToNext()) {

            int defaultIfGreaterThanZero = emailCur
                    .getInt(emailCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (defaultIfGreaterThanZero > 0) {
                return emailCur
                        .getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }
        emailCur.moveToPosition(-1);
        while (emailCur.moveToNext()) {
            // This would allow you get several email addresses
            // if the email addresses were stored in an array
            return emailCur
                    .getString(emailCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

        }
        emailCur.close();
        return null;
    }

    // ID is name>number
    public static String numberToID(String phoneNumber, Context context) {
        try {
            ContentResolver localContentResolver = context.getContentResolver();
            Cursor contactLookupCursor = localContentResolver.query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(phoneNumber)), new String[]{
                            PhoneLookup.DISPLAY_NAME, BaseColumns._ID}, null,
                    null, null
            );
            try {
                while (contactLookupCursor.moveToNext()) {
                    String contactName = contactLookupCursor
                            .getString(contactLookupCursor
                                    .getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
                    return contactName;
                }
            } finally {
                contactLookupCursor.close();
            }
            String spacedNumber = "";
            for (char c : phoneNumber.toCharArray()) {
                spacedNumber += c + " ";
            }
            return spacedNumber;
        } catch (Exception e) {
            return "Unknown Number";
        }
    }
}
