package com.RSen.OpenMic.Pheonix;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.RSen.OpenMic.Pheonix.R;
import com.apptentive.android.sdk.ApptentiveActivity;

import org.sufficientlysecure.donations.DonationsFragment;

public class DonateActivity extends AppCompatActivity {
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAic9tLgA2YfTlgOhN8yTfqXdI+9R/fyuaMdHyv2n0qYetDM0uUR+ECLNyWWgZA94UcPUe05Mq6xVQbA7f+m2cGnMQW2fiUz3RZ2am7ZeBj9XyDEz7kLGAIlAS+FnX6AJmIeqgm7fWQgDxtYMIMvu9LfeVRi4YFP7Buontj0hoBimybwmekLXLCr3/saA6ozfyJh8eWFkPra1muTkIzuX5IeUeFff1Bp+uf2442LJzRruCWdMZe1WonBxBnJIp/xwNTSMnS/mp/YQHIWWV2gSGo6t7Fz/hmtqj82nOuJnAgqv5OxmldrcABaWv6v2+jTLzw8HnwDFUFBr/WylUWxlpoQIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{
            "openmic.donation.1", "openmic.donation.2", "openmic.donation.3",
            "openmic.donation.5", "openmic.donation.10", "openmic.donation.15",
            "openmic.donation.20", "openmic.donation.30"};

    /**
     * PayPal
     */
    private static final String PAYPAL_USER = "rsenapps@gmail.com";
    private static final String PAYPAL_CURRENCY_CODE = "USD";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DonationsFragment donationsFragment;

        donationsFragment = DonationsFragment.newInstance(false, true,
                GOOGLE_PUBKEY, GOOGLE_CATALOG, new String[]{"$1", "$2", "$3",
                        "5", "10", "$15", "$20", "$30"}, false, null, null,
                null, false, null, null,false,null
        );

        ft.replace(R.id.donations_activity_container, donationsFragment,
                "donationsFragment");
        ft.commit();
    }

    /**
     * Needed for Google Play In-app Billing. It uses
     * startIntentSenderForResult(). The result is not propagated to the
     * Fragment like in startActivityForResult(). Thus we need to propagate
     * manually to our Fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentByTag("donationsFragment");
        if (fragment != null) {
            ((DonationsFragment) fragment).onActivityResult(requestCode,
                    resultCode, data);
        }
    }
}
