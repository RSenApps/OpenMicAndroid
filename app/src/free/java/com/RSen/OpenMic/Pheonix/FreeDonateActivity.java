package com.RSen.OpenMic.Pheonix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.android.gms.plus.PlusShare;

public class FreeDonateActivity extends FragmentActivity {
    SocialNetworkManager mSocialNetworkManager;
    Button twitter;
    Button facebook;
    Button google;
    int tasksCompleted = 0;
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_donate);
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        mSocialNetworkManager = (SocialNetworkManager) getSupportFragmentManager().findFragmentByTag("social");

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(this)
                    .twitter("oNsJwbYKYgMs3xp73X3hBAfH0", "qcfaqUyqlq4DhMI1hge71QzDtCRLAuruY3AYSnCztQQfclKyub")
                    .googlePlus()
                    .build();
            getSupportFragmentManager().beginTransaction().add(mSocialNetworkManager, "social").commit();

        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        twitter = (Button) findViewById(R.id.twitter);
        if (prefs.getBoolean("postTwitter", false)) {
            twitter.setEnabled(false);
            twitter.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTwitter();
            }
        });
        facebook = (Button) findViewById(R.id.facebook);
        if (prefs.getBoolean("postFacebook", false)) {
            facebook.setEnabled(false);
            facebook.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFacebook();
            }
        });
        google = (Button) findViewById(R.id.google);
        if (prefs.getBoolean("postGoogle", false)) {
            google.setEnabled(false);
            google.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoogle();
            }
        });
        tasksCompleted = prefs.getInt("freeDonateTasksCompleted", 0);
        if (tasksCompleted >= 2) {
            findViewById(R.id.congratulations).setVisibility(View.VISIBLE);
            findViewById(R.id.congratulationsText).setVisibility(View.VISIBLE);
        }

    }


    protected void loginTwitter() {

        try {
            if (mSocialNetworkManager.getSocialNetwork(TwitterSocialNetwork.ID).isConnected()) {
                postTwitter();
            } else {
                mSocialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
                    @Override
                    public void onLoginSuccess(int i) {
                        if (mSocialNetworkManager.getTwitterSocialNetwork().isConnected()) {
                            postTwitter();
                        } else {
                            Toast.makeText(FreeDonateActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s, String s2, Object o) {
                        Toast.makeText(FreeDonateActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(FreeDonateActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
        }
    }

    protected void loginFacebook() {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setLink("http://OpenMic.RSenApps.com/")
                    .setDescription(getString(R.string.fb_share_msg))
                    .setName(getString(R.string.share_name))
                    .setPicture("http://open-mic.appspot.com/openmicfeature.jpg")
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else {

            Session.openActiveSession(this, true, new Session.StatusCallback() {

                // callback when session changes state
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (session.isOpened()) {
                        Bundle params = new Bundle();
                        params.putString("name", getString(R.string.share_name));
                        params.putString("description", getString(R.string.fb_share_msg));
                        params.putString("link", "http://OpenMic.RSenApps.com/");
                        params.putString("picture", "http://open-mic.appspot.com/openmicfeature.jpg");
                        WebDialog feedDialog = (
                                new WebDialog.FeedDialogBuilder(FreeDonateActivity.this,
                                        session,
                                        params))
                                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                                    @Override
                                    public void onComplete(Bundle values,
                                                           FacebookException error) {
                                        if (error == null) {
                                            // When the story is posted, echo the success
                                            // and the post Id.
                                            final String postId = values.getString("post_id");
                                            if (postId != null) {
                                                facebook.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                                                facebook.setEnabled(false);
                                                incrementTasksCompleted();
                                                PreferenceManager.getDefaultSharedPreferences(FreeDonateActivity.this).edit().putBoolean("postFacebook", true).commit();
                                            } else {
                                                Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                })
                                .build();
                        feedDialog.show();
                    } else {
                        Toast.makeText(FreeDonateActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void loginGoogle() {
        if (mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).isConnected()) {
            postGoogle();
        } else {
            mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin(new OnLoginCompleteListener() {
                @Override
                public void onLoginSuccess(int i) {

                    postGoogle();
                }

                @Override
                public void onError(int i, String s, String s2, Object o) {
                    Toast.makeText(FreeDonateActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {

            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                if (FacebookDialog.getNativeDialogDidComplete(data) && FacebookDialog.getNativeDialogCompletionGesture(data) != null && FacebookDialog.getNativeDialogCompletionGesture(data).equals("post")) {
                    facebook.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    facebook.setEnabled(false);
                    incrementTasksCompleted();
                    PreferenceManager.getDefaultSharedPreferences(FreeDonateActivity.this).edit().putBoolean("postFacebook", true).commit();
                } else {
                    Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
                }
            }

        });
        if (requestCode == 182) //post
        {
            if (resultCode == RESULT_OK) {
                google.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                google.setEnabled(false);
                incrementTasksCompleted();
                PreferenceManager.getDefaultSharedPreferences(FreeDonateActivity.this).edit().putBoolean("postGoogle", true).commit();
            } else {
                Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
            }
        } else {
            /**
             * This is required only if you are using Google Plus, the issue is that there SDK
             * require Activity to launch Auth, so library can't receive onActivityResult in fragment
             */
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("social");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void postTwitter() {
        try {
            mSocialNetworkManager.getTwitterSocialNetwork().requestPostMessage(getString(R.string.twitter_share_msg),
                    new OnPostingCompleteListener() {
                        @Override
                        public void onPostSuccessfully(int i) {
                            twitter.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                            twitter.setEnabled(false);
                            incrementTasksCompleted();
                            PreferenceManager.getDefaultSharedPreferences(FreeDonateActivity.this).edit().putBoolean("postTwitter", true).commit();

                        }

                        @Override
                        public void onError(int i, String s, String s2, Object o) {
                            Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
                        }

                    }
            );
        } catch (Exception e) {
            Toast.makeText(FreeDonateActivity.this, getString(R.string.post_failed), Toast.LENGTH_SHORT).show();
        }
    }


    private void postGoogle() {
        try {
            Uri imageUri = null;
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                        BitmapFactory.decodeResource(getResources(), R.drawable.openmicfeature), null, null));
            } catch (Exception e) {
            }
            String text = getString(R.string.google_share_msg);
            // Launch the Google+ share dialog with attribution to your app.
            Intent shareIntent = new PlusShare.Builder(this)
                    .setType("image/jpeg")
                    .setText(text)
                    .addStream(imageUri)
                    .getIntent();
            startActivityForResult(shareIntent, 182);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.install_google_plus), Toast.LENGTH_LONG).show();
        }
    }

    private void incrementTasksCompleted() {
        tasksCompleted++;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("freeDonateTasksCompleted", tasksCompleted).commit();
        if (tasksCompleted >= 2) {
            findViewById(R.id.congratulations).setVisibility(View.VISIBLE);
            findViewById(R.id.congratulationsText).setVisibility(View.VISIBLE);
            prefs.edit().putBoolean("freeDonate", true).commit();
        }
    }

}
