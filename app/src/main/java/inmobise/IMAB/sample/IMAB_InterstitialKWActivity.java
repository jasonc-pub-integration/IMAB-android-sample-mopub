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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static inmobise.IMAB.sample.Constants.IMAB_InterstitialFailureDelay;
import static inmobise.IMAB.sample.Constants.IMAB_InterstitialPLC;
import static inmobise.IMAB.sample.Constants.IMAB_InterstitialSuccessDelay;
import static inmobise.IMAB.sample.Constants.IMAB_Interstitial_Explanation;
import static inmobise.IMAB.sample.Constants.IMAB_KW_Interstitial_Explanation;
import static inmobise.IMAB.sample.Constants.MP_InterstitialAdUnitID;
import static inmobise.IMAB.sample.Constants.log;

public class IMAB_InterstitialKWActivity extends AppCompatActivity implements MoPubInterstitial.InterstitialAdListener{

    private MoPubInterstitial mInterstitial;                                    // MoPub interstitial view
    private IMAudienceBidder inMobiAudienceBidder;                              // Recommended we keep a reference to the IMAudienceBidder singleton
    private IMAudienceBidder.BidToken interstitialBidToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imab__interstitial_kw);

        configureInterstitial();
        setInterstitialHelperText();
        updateUIForInterstitialNotReady();

    }

    public void configureInterstitial() {

        mInterstitial = new MoPubInterstitial(this, MP_InterstitialAdUnitID);
        mInterstitial.setInterstitialAdListener(this);

        Map<String, Object> localExtras = new HashMap();
        localExtras.put(IMAudienceBidder.AD_KEY, IMAB_InterstitialPLC);
        mInterstitial.setLocalExtras(localExtras);

        inMobiAudienceBidder = IMAudienceBidder.getInstance();



        // Create a bid token and define the listener methods
        interstitialBidToken = inMobiAudienceBidder.createBidToken(this, IMAB_InterstitialPLC, new IMAudienceBidder.IMAudienceBidderInterstitialKeywordListener() {
            @Override
            public void onBidReceived(IMAudienceBidder.IMABBidResponse imabBidResponse) {

                // Bid was received from InMobi Audience Bidder.
                updateSuccessBidWithInformation(imabBidResponse);

                // Parse the imaBidResponse for any additional information you need, and set them here as desired.
                mInterstitial.setKeywords(imabBidResponse.getBidKeyword());

                // Call load on the interstitial after all keywords have been set
                mInterstitial.load();
            }

            @Override
            public void onBidFailed(Error error) {

                updateFailedBidWithInformation();

                // No bid received - you may call load on the interstitial after any additional keywords have been set
                mInterstitial.load();

            }
        });


    }

    // You may optionally update bid.
    public void loadKWInterstitial(View view) {
        updateInterstitialBidWithDelay(0);
    }

    public void showKWInterstitial(View view){
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
        clearBidInformation();
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



    @Override
    protected void onDestroy() {
        mInterstitial.destroy();
        super.onDestroy();
    }

    // Various UI methods, ignore these. Or if you can improve on them, make a PR! Much thanks.

    public void updateUIForInterstitialIsReady(){

        Button load = findViewById(R.id.buttonLoadKWInterstitial);
        load.setVisibility(View.INVISIBLE);
        load.setEnabled(false);
        Button show = findViewById(R.id.buttonShowKWInterstitial);
        show.setVisibility(View.VISIBLE);
        ProgressBar spinner = findViewById(R.id.intKWProgressBar);
        spinner.setVisibility(View.INVISIBLE);

    }

    public void updateUIForInterstitialNotReady(){

        Button load = findViewById(R.id.buttonLoadKWInterstitial);
        load.setVisibility(View.VISIBLE);
        load.setEnabled(true);
        Button show = findViewById(R.id.buttonShowKWInterstitial);
        show.setVisibility(View.INVISIBLE);

    }

    public void updateUIForInterstitialLoading(){

        Button load = findViewById(R.id.buttonLoadKWInterstitial);
        load.setVisibility(View.VISIBLE);
        load.setEnabled(false);
        Button show = findViewById(R.id.buttonShowKWInterstitial);
        show.setVisibility(View.INVISIBLE);

        ProgressBar spinner = findViewById(R.id.intKWProgressBar);
        spinner.setVisibility(View.VISIBLE);

    }

    public void setInterstitialHelperText() {
        TextView tv = findViewById(R.id.intKWTextView);
        tv.setText(IMAB_KW_Interstitial_Explanation);
    }


    public void updateSuccessBidWithInformation(IMAudienceBidder.IMABBidResponse imabBidResponse){

        setCurrentBidKeywordText(imabBidResponse.getBidKeyword());
        setCurrentBidGranularKeywordText(imabBidResponse.getGranularBidKeyword());
        setCurrentBidBuyerText(imabBidResponse.getBuyer());
        setCurrentBidPriceText(String.valueOf(imabBidResponse.getPrice()));
        setCurrentBidPlacementText(imabBidResponse.getPlacement());


    }

    public void clearBidInformation(){

        setCurrentBidKeywordText("");
        setCurrentBidGranularKeywordText("");
        setCurrentBidBuyerText("");
        setCurrentBidPriceText("");
        setCurrentBidPlacementText("");

    }

    public void updateFailedBidWithInformation(){

        setCurrentBidKeywordText("");
        setCurrentBidGranularKeywordText("");
        setCurrentBidBuyerText("");
        setCurrentBidPriceText("");
        setCurrentBidPlacementText("");

    }


    public void setCurrentBidKeywordText(String text){

        TextView tv = findViewById(R.id.text_int_bid_kw);
        text = "Bid Keyword: " + text;
        tv.setText(text);



    }

    public void setCurrentBidGranularKeywordText(String text){

        TextView tv = findViewById(R.id.text_int_bid_kw_granular);
        text = "Bid Granular KW: " + text;
        tv.setText(text);



    }

    public void setCurrentBidPriceText(String text){

        TextView tv = findViewById(R.id.text_int_bid_kw_price);
        text = "Bid Actual Price: " + text;
        tv.setText(text);


    }

    public void setCurrentBidPlacementText(String text){

        TextView tv = findViewById(R.id.text_int_bid_kw_placement);
        text = "Bid Placement: " + text;
        tv.setText(text);


    }

    public void setCurrentBidBuyerText(String text){

        TextView tv = findViewById(R.id.text_int_kw_buyer);
        text = "Bid Buyer: " + text;
        tv.setText(text);


    }



}
