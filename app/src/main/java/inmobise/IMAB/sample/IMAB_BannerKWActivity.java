package inmobise.IMAB.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.inmobi.plugin.mopub.IMAudienceBidder;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

import static inmobise.IMAB.sample.Constants.IMAB_BannerPLC;
import static inmobise.IMAB.sample.Constants.IMAB_KW_Banner_Explanation;
import static inmobise.IMAB.sample.Constants.MP_BannerAdUnitID;
import static inmobise.IMAB.sample.Constants.MP_BannerHeight;
import static inmobise.IMAB.sample.Constants.MP_BannerWidth;
import static inmobise.IMAB.sample.Constants.log;

public class IMAB_BannerKWActivity extends AppCompatActivity implements MoPubView.BannerAdListener {

    private MoPubView moPubView;
    private IMAudienceBidder inMobiAudienceBidder;

    public Boolean bannerLoaded = false;                                        // Use a Boolean to keep track so we don't call loadAd on the banner repeatedly.
    private IMAudienceBidder.BidToken bannerBidToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imab__banner_kw);

        configureBanner();
        setBannerHelperText();
    }

    // Configure the banner and IMAB bid token
    public void configureBanner() {

        moPubView = (MoPubView) findViewById(R.id.adviewKW);
        moPubView.setBannerAdListener(this);
        moPubView.setAdUnitId(MP_BannerAdUnitID);

        // NOTE: This MUST be set upon creation of the banner! Not doing so will cause keyword targeting to fail
        Map<String, Object> localExtras = new HashMap();
        localExtras.put(IMAudienceBidder.AD_KEY, IMAB_BannerPLC);
        moPubView.setLocalExtras(localExtras);

        inMobiAudienceBidder = IMAudienceBidder.getInstance();

        bannerBidToken = inMobiAudienceBidder.createBidToken(this, IMAB_BannerPLC, MP_BannerWidth, MP_BannerHeight,
                new IMAudienceBidder.IMAudienceBidderBannerKeywordListener() {
            @Override
            public void onBidReceived(IMAudienceBidder.IMABBidResponse imabBidResponse) {

                // Parse the imaBidResponse for any additional information you need, and set them here as desired.
                moPubView.setKeywords(imabBidResponse.getBidKeyword());

                // Optional UI event to update the view with bid information
                updateSuccessBidWithInformation(imabBidResponse);

                // If the banner has not yet been loaded, call loadAd to load the ad into the view
                if (!bannerLoaded) {
                    moPubView.loadAd();
                }

            }

            @Override
            public void onBidFailed(Error error) {
                // No Bid received from InMobi Audience Bidder. Call loadAd on your MoPub view once you are ready.

                // Error handling here as desired, Optional UI event to update the view with bid
                updateFailedBidWithInformation();

                // If the banner has not yet been loaded, call loadAd to load the ad into the view
                if (!bannerLoaded) {
                    moPubView.loadAd();
                }


            }
        });

        bannerBidToken.updateBid();

    }


    public void onBannerLoaded(MoPubView banner) {

        // Ensure that we do not call loadAd again on the MoPubView
        bannerLoaded = true;

        // Update the bid for the next MoPub refresh ad call
        bannerBidToken.updateBid();

    }

    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

        // Ensure that we do not call loadAd again on the MoPubView
        bannerLoaded = true;

        // Update the bid for the next MoPub refresh ad call
        bannerBidToken.updateBid();

    }




    public void onBannerClicked(MoPubView banner) {
        Log.d(log, "Banner clicked");
    }

    public void onBannerExpanded(MoPubView banner) {
        Log.d(log, "Banner expanded");
    }

    public void onBannerCollapsed(MoPubView banner) {
        Log.d(log, "Banner collapsed");
    }


    @Override
    protected void onDestroy() {
        moPubView.destroy();
        super.onDestroy();
    }


    // Various UI methods, ignore these. Or if you can improve on them, make a PR! Much thanks.

    public void setBannerHelperText() {
        TextView tv = findViewById(R.id.bannerKWTextView);
        tv.setText(IMAB_KW_Banner_Explanation);
    }


    public void updateSuccessBidWithInformation(IMAudienceBidder.IMABBidResponse imabBidResponse){

        setCurrentBidKeywordText(imabBidResponse.getBidKeyword());
        setCurrentBidGranularKeywordText(imabBidResponse.getGranularBidKeyword());
        setCurrentBidBuyerText(imabBidResponse.getBuyer());
        setCurrentBidPriceText(String.valueOf(imabBidResponse.getPrice()));
        setCurrentBidPlacementText(imabBidResponse.getPlacement());


    }

    public void updateFailedBidWithInformation(){

        setCurrentBidKeywordText("");
        setCurrentBidGranularKeywordText("");
        setCurrentBidBuyerText("");
        setCurrentBidPriceText("");
        setCurrentBidPlacementText("");

    }


    public void setCurrentBidKeywordText(String text){

        TextView tv = findViewById(R.id.text_banner_bid_kw);
        text = "Bid Keyword: " + text;
        tv.setText(text);



    }

    public void setCurrentBidGranularKeywordText(String text){

        TextView tv = findViewById(R.id.text_banner_bid_kw_granular);
        text = "Bid Granular KW: " + text;
        tv.setText(text);



    }

    public void setCurrentBidPriceText(String text){

        TextView tv = findViewById(R.id.text_banner_bid_kw_price);
        text = "Bid Actual Price: " + text;
        tv.setText(text);


    }

    public void setCurrentBidPlacementText(String text){

        TextView tv = findViewById(R.id.text_banner_bid_kw_placement);
        text = "Bid Placement: " + text;
        tv.setText(text);


    }

    public void setCurrentBidBuyerText(String text){

        TextView tv = findViewById(R.id.text_banner_kw_buyer);
        text = "Bid Buyer: " + text;
        tv.setText(text);


    }


}
