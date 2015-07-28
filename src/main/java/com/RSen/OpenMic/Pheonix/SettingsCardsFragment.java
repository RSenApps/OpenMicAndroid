package com.RSen.OpenMic.Pheonix;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.RSen.OpenMic.Pheonix.Cards.AdvancedCard;
import com.RSen.OpenMic.Pheonix.Cards.BlacklistedCard;
import com.RSen.OpenMic.Pheonix.Cards.HavingTroubleCard;
import com.RSen.OpenMic.Pheonix.Cards.HotwordRecognitionCard;
import com.RSen.OpenMic.Pheonix.Cards.MyListCard;
import com.RSen.OpenMic.Pheonix.Cards.RunningCard;
import com.RSen.OpenMic.Pheonix.Cards.ShakeDetectionCard;
import com.RSen.OpenMic.Pheonix.Cards.TaskerCard;
import com.RSen.OpenMic.Pheonix.Cards.TryCard;
import com.RSen.OpenMic.Pheonix.Cards.VolumeCard;
import com.RSen.OpenMic.Pheonix.Cards.WaveDetectionCard;
import com.RSen.OpenMic.Pheonix.Cards.WhenToRunCard;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;
import it.gmariotti.cardslib.library.internal.Card;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCardsFragment extends Fragment implements Refreshable {
    final Handler handler = new Handler();
    int mAnimationTime;
    ArrayList<Card> cards;
    CardGridStaggeredArrayAdapter mCardArrayAdapter;
    AnimationAdapter animCardArrayAdapter;
    boolean currentlyStoppedLayout = true;
    Runnable addStoppedCards = new Runnable() {
        @Override
        public void run() {
            try {
                HotwordRecognitionCard h = new HotwordRecognitionCard(getActivity(), SettingsCardsFragment.this);
                h.init();
                mCardArrayAdapter.add(h);
                WaveDetectionCard w = new WaveDetectionCard(getActivity(), SettingsCardsFragment.this);
                w.init();
                mCardArrayAdapter.add(w);
                ShakeDetectionCard s = new ShakeDetectionCard(getActivity(), SettingsCardsFragment.this);
                s.init();
                mCardArrayAdapter.add(s);
                WhenToRunCard wh = new WhenToRunCard(getActivity(), SettingsCardsFragment.this);
                wh.init();
                mCardArrayAdapter.add(wh);
                BlacklistedCard b = new BlacklistedCard(getActivity(), SettingsCardsFragment.this);
                b.init();
                mCardArrayAdapter.add(b);
                TaskerCard t = new TaskerCard(getActivity(), SettingsCardsFragment.this);
                t.init();
                mCardArrayAdapter.add(t);
                AdvancedCard a = new AdvancedCard(getActivity(), SettingsCardsFragment.this);
                a.init();
                mCardArrayAdapter.add(a);
            } catch (Exception e) {
            }
        }
    };
    Runnable addStartedCards = new Runnable() {
        @Override
        public void run() {
            try {
                RunningCard r = new RunningCard(getActivity(), SettingsCardsFragment.this);
                r.init();
                mCardArrayAdapter.add(r);
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("speech_engine", "google").equals("google")) {
                    mCardArrayAdapter.add(new VolumeCard(getActivity(), SettingsCardsFragment.this));
                }
                TryCard t = new TryCard(getActivity(), SettingsCardsFragment.this);
                t.init();
                mCardArrayAdapter.add(t);
                HavingTroubleCard h = new HavingTroubleCard(getActivity(), SettingsCardsFragment.this);
                h.init();
                mCardArrayAdapter.add(h);
            } catch (Exception e) {
            }
        }
    };
    boolean hasWaited = true; //pretty hacky way to ensure that we wait an additional second once scrolled
    DismissCardRunnable dismissCard = new DismissCardRunnable();
    ArrayList<View> viewsToResetProperties = new ArrayList<View>();

    public SettingsCardsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnimationTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
        cards = new ArrayList<Card>();
        if (MyService.isRunning || MainActivity.listenScreenOffActivated) {
            currentlyStoppedLayout = false;
            RunningCard r = new RunningCard(getActivity(), SettingsCardsFragment.this);
            r.init();
            cards.add(r);
            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("speech_engine", "google").equals("google")) {
                cards.add(new VolumeCard(getActivity(), SettingsCardsFragment.this));
            }
            TryCard t = new TryCard(getActivity(), SettingsCardsFragment.this);
            t.init();
            cards.add(t);
            HavingTroubleCard h = new HavingTroubleCard(getActivity(), SettingsCardsFragment.this);
            h.init();
            cards.add(h);
        } else {
            currentlyStoppedLayout = true;
            HotwordRecognitionCard h = new HotwordRecognitionCard(getActivity(), SettingsCardsFragment.this);
            h.init();
            cards.add(h);
            WaveDetectionCard w = new WaveDetectionCard(getActivity(), SettingsCardsFragment.this);
            w.init();
            cards.add(w);
            ShakeDetectionCard s = new ShakeDetectionCard(getActivity(), SettingsCardsFragment.this);
            s.init();
            cards.add(s);
            WhenToRunCard wh = new WhenToRunCard(getActivity(), SettingsCardsFragment.this);
            wh.init();
            cards.add(wh);
            BlacklistedCard b = new BlacklistedCard(getActivity(), SettingsCardsFragment.this);
            b.init();
            cards.add(b);
            TaskerCard t = new TaskerCard(getActivity(), SettingsCardsFragment.this);
            t.init();
            cards.add(t);
            AdvancedCard a = new AdvancedCard(getActivity(), SettingsCardsFragment.this);
            a.init();
            cards.add(a);
        }

        mCardArrayAdapter = new CardGridStaggeredArrayAdapter(getActivity(), cards);
        mCardArrayAdapter.setInnerViewTypeCount(2);
        mCardArrayAdapter.setNotifyOnChange(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(dismissCard);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_settings_cards, container, false);


        final CardGridStaggeredView listView = (CardGridStaggeredView) v.findViewById(R.id.card_list_view);
        if (listView != null && listView.getAdapter() == null) {
            animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
            animCardArrayAdapter.setAbsListView(listView);
            listView.setExternalAdapter(animCardArrayAdapter, mCardArrayAdapter);
            listView.setEnabled(true);
        }
        return v;
    }

    public void dismissAll(final Runnable onFinishTask) {
        if (dismissCard.isRunning) {
            dismissCard.onFinishTask = onFinishTask;
        } else {
            dismissCard.onFinishTask = onFinishTask;
            try {
                mCardArrayAdapter.getCardGridView().removeCallbacks(dismissCard.onFinish);
                handler.removeCallbacks(dismissCard);
            } catch (Exception e) {
            }
            viewsToResetProperties = new ArrayList<View>();
            dismissCard.run();
        }


    }

    public void dismissCard(View cardView) {
        cardView.animate()
                .translationX(cardView.getWidth())
                .alpha(0)
                .setDuration(mAnimationTime);
    }

    public void switchToStoppedLayout() {
        if (!currentlyStoppedLayout) {

            currentlyStoppedLayout = true;
            mCardArrayAdapter.getCardGridView().resetToTop();
            mCardArrayAdapter.getCardGridView().setEnabled(false);
            mCardArrayAdapter.getCardGridView().post(new Runnable() {
                @Override
                public void run() {
                    dismissAll(addStoppedCards);
                }
            });
            mCardArrayAdapter.notifyDataSetChanged();

        }
    }

    public void switchToStartedLayout() {
        if (currentlyStoppedLayout) {

            currentlyStoppedLayout = false;
            mCardArrayAdapter.getCardGridView().resetToTop();
            mCardArrayAdapter.getCardGridView().setEnabled(false);

            dismissAll(addStartedCards);
        }
    }

    public void refresh() {
        while (mCardArrayAdapter.getCount() > 0) {
            mCardArrayAdapter.remove(mCardArrayAdapter.getItem(0));
        }

        mCardArrayAdapter.getCardGridView().setEnabled(true);
        mCardArrayAdapter.notifyDataSetChanged();

        mCardArrayAdapter.getCardGridView().post(new Runnable() {
            @Override
            public void run() {

                if (MyService.isRunning || MainActivity.listenScreenOffActivated) {

                    addStartedCards.run();
                    animCardArrayAdapter.reset();

                    animCardArrayAdapter.notifyDataSetChanged();
                    currentlyStoppedLayout = false;
                } else {
                    addStoppedCards.run();
                    animCardArrayAdapter.reset();

                    animCardArrayAdapter.notifyDataSetChanged();
                    currentlyStoppedLayout = true;
                }
            }
        });

    }

    public class DismissCardRunnable implements Runnable {
        public volatile boolean isRunning = false;
        public volatile Runnable onFinishTask;
        public volatile Runnable onFinish;

        @Override
        public void run() {
            isRunning = true;
            if (mCardArrayAdapter == null || mCardArrayAdapter.getCardGridView() == null || mCardArrayAdapter.getCardGridView().getChildAt(0) == null) {
                return;
            }
            if (!mCardArrayAdapter.getCardGridView().canScrollVertically(-1)) { //if can't scroll up
                if (!hasWaited) {
                    hasWaited = true;
                    handler.postDelayed(this, 500);
                }
                Card cardToDismiss = null;
                for (Card card : cards) {
                    //WOW THIS IS SO BAD
                    try {
                        MyListCard settingsCard = (MyListCard) card;
                        if (!settingsCard.isDismissing) {
                            cardToDismiss = card;
                            settingsCard.isDismissing = true;
                            break;
                        }
                    } catch (Exception e) {
                        VolumeCard volumeCard = (VolumeCard) card;
                        if (!volumeCard.isDismissing) {
                            cardToDismiss = card;
                            volumeCard.isDismissing = true;
                            break;
                        }
                    }
                }
                if (cardToDismiss != null) {
                    try {
                        viewsToResetProperties.add(cardToDismiss.getCardView());
                        dismissCard(cardToDismiss.getCardView()); //may throw nullpointer
                        handler.postDelayed(this, 300);
                    } catch (Exception e) {
                        //mCardArrayAdapter.remove(cardToDismiss);
                        handler.post(this);
                    }


                } else {
                    for (View v : viewsToResetProperties) {
                        if (v == null) {
                            continue;
                        }
                        v.setAlpha(1f);
                        v.setTranslationX(0);
                    }
                    while (mCardArrayAdapter.getCount() > 0) {
                        mCardArrayAdapter.remove(mCardArrayAdapter.getItem(0));
                    }


                    mCardArrayAdapter.notifyDataSetChanged();
                    isRunning = false;
                    mCardArrayAdapter.getCardGridView().setEnabled(true);
                    onFinish = new Runnable() {
                        @Override
                        public void run() {
                            onFinishTask.run();
                            animCardArrayAdapter.reset();
                            animCardArrayAdapter.notifyDataSetChanged();


                        }
                    };
                    mCardArrayAdapter.getCardGridView().post(onFinish);

                }
            } else {
                hasWaited = false;
                handler.postDelayed(this, 200);
            }
        }
    }
}
