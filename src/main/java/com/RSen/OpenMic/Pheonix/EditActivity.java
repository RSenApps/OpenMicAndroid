package com.RSen.OpenMic.Pheonix;

/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 * <p/>
 * This Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} from a previously saved
 * plug-in instance that the user is editing.</li>
 * </ul>
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class EditActivity extends AbstractPluginActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent resultIntent = new Intent();

		/*
         * This extra is the data to ourselves: either for the Activity or the
		 * BroadcastReceiver. Note that anything placed in this Bundle must be
		 * available to Locale's class loader. So storing String, int, and other
		 * standard objects will work just fine. Parcelable objects are not
		 * acceptable, unless they also implement Serializable. Serializable
		 * objects must be standard Android platform objects (A Serializable
		 * class private to this plug-in's APK cannot be stored in the Bundle,
		 * as Locale's classloader will not recognize it).
		 */
        final Bundle resultBundle = PluginBundleManager.generateBundle(
                getApplicationContext(), "Ready");
        resultIntent.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE",
                resultBundle);

		/*
         * The blurb is concise status text to be displayed in the host's UI.
		 */
        resultIntent.putExtra("com.twofortyfouram.locale.intent.extra.BLURB",
                "Ready");

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getString(R.string.action_question));
        final String[] actions = new String[]{getString(R.string.start),
                getString(R.string.stop), getString(R.string.toggle),
                getString(R.string.enable_screen_off),
                getString(R.string.disable_screen_off)};
        builder.setSingleChoiceItems(actions, -1, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                resultIntent.putExtra("Action", actions[which]);
            }
        });
        builder.setPositiveButton(getString(R.string.ok),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
        );
        builder.setNegativeButton(getString(R.string.cancel),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }
        );

        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

}