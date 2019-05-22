package inmobise.IMAB.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inmobi.plugin.mopub.IMAudienceBidder;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.Timer;
import java.util.TimerTask;

import party.parrot.partyparrot.R;

import static inmobise.IMAB.sample.Constants.IMAB_InterstitialFailureDelay;
import static inmobise.IMAB.sample.Constants.IMAB_InterstitialPLC;
import static inmobise.IMAB.sample.Constants.IMAB_InterstitialSuccessDelay;
import static inmobise.IMAB.sample.Constants.IMAB_Interstitial_Explanation;
import static inmobise.IMAB.sample.Constants.MP_InterstitialAdUnitID;
import static inmobise.IMAB.sample.Constants.log;

public class IMAB_InterstitialActivity extends AppCompatActivity implements MoPubInterstitial.InterstitialAdListener {

    private MoPubInterstitial mInterstitial;                                    // MoPub interstitial view
    private IMAudienceBidder inMobiAudienceBidder;                              // Recommended we keep a reference to the IMAudienceBidder singleton
    private IMAudienceBidder.BidToken interstitialBidToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imab__interstitial);

        configureInterstitial();
        setInterstitialHelperText();

        updateUIForInterstitialNotReady();

        }



    public void configureInterstitial() {

        mInterstitial = new MoPubInterstitial(this, MP_InterstitialAdUnitID);
        mInterstitial.setInterstitialAdListener(this);

        inMobiAudienceBidder = IMAudienceBidder.getInstance();

        // Create a bid token and define the listener methods
        interstitialBidToken = inMobiAudienceBidder.createBidToken(this,
                IMAB_InterstitialPLC, mInterstitial, new IMAudienceBidder.IMAudienceBidderInterstitialListener() {

                    @Override
                    public void onBidReceived(@NonNull final MoPubInterstitial m) {

                        // A bid was received - you may call load on the updated bid object that is returned.
                        m.load();
                    }

                    @Override
                    public void onBidFailed(@NonNull MoPubInterstitial m, @NonNull final Error error) {

                        // No bid received - you may call load on the bid object
                        m.load();
                    }

                });

    }

    // You may optionally update bid.
    public void loadInterstitial(View view) {
        updateInterstitialBidWithDelay(0);
    }

    public void showInterstitial(View view){
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }


    public void updateInterstitialBidWithDelay(long delay) {
        TimerTask task = new TimerTask() {
            public void run() {
                interstitialBidToken.updateBid();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, delay);

        updateUIForInterstitialLoading();
    }


    // InterstitialAdListener methods

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        Log.d(log, "Interstitial loaded");

        updateUIForInterstitialIsReady();
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {

        Log.d(log, "Interstitial load failed: " + errorCode);

        // You may want to update the interstitial bid if we fail to load one.
        updateInterstitialBidWithDelay(IMAB_InterstitialFailureDelay);

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        Log.d(log, "Interstitial Dismissed, refreshing bid");

        // We want to update the interstitial bid when the the interstitial has being dismissed
        updateInterstitialBidWithDelay(IMAB_InterstitialSuccessDelay);

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) { Log.d(log, "Interstitial shown "); }



    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) { Log.d(log, "Interstitial clicked"); }



    public void updateUIForInterstitialIsReady(){

        Button load = findViewById(R.id.buttonLoadInterstitial);
        load.setVisibility(View.INVISIBLE);
        load.setEnabled(false);
        Button show = findViewById(R.id.buttonShowInterstitial);
        show.setVisibility(View.VISIBLE);

        ProgressBar spinner = findViewById(R.id.intProgressBar);
        spinner.setVisibility(View.INVISIBLE);

    }

    public void updateUIForInterstitialNotReady(){

        Button load = findViewById(R.id.buttonLoadInterstitial);
        load.setVisibility(View.VISIBLE);
        load.setEnabled(true);
        Button show = findViewById(R.id.buttonShowInterstitial);
        show.setVisibility(View.INVISIBLE);

    }

    public void updateUIForInterstitialLoading(){

        Button load = findViewById(R.id.buttonLoadInterstitial);
        load.setVisibility(View.VISIBLE);
        load.setEnabled(false);
        Button show = findViewById(R.id.buttonShowInterstitial);
        show.setVisibility(View.INVISIBLE);

        ProgressBar spinner = findViewById(R.id.intProgressBar);
        spinner.setVisibility(View.VISIBLE);

    }

    public void setInterstitialHelperText() {
        TextView tv = findViewById(R.id.intTextView);
        tv.setText(IMAB_Interstitial_Explanation);
    }


    @Override
    protected void onDestroy() {
        mInterstitial.destroy();
        super.onDestroy();
    }



}
