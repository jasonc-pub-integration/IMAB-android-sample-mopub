package inmobise.IMAB.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.plugin.mopub.IMAudienceBidder;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import static inmobise.IMAB.sample.Constants.IMAB_BannerPLC;
import static inmobise.IMAB.sample.Constants.IMAB_Banner_Explanation;
import static inmobise.IMAB.sample.Constants.MP_BannerAdUnitID;
import static inmobise.IMAB.sample.Constants.log;

public class IMAB_BannerActivity extends AppCompatActivity implements MoPubView.BannerAdListener {

    private MoPubView moPubView;                                                // MoPub banner view
    private IMAudienceBidder inMobiAudienceBidder;                              // Recommended we keep a reference to the IMAudienceBidder singleton

    public Boolean bannerLoaded = false;                // Use a Boolean to keep track so we don't call loadAd on the banner repeatedly.
    private IMAudienceBidder.BidToken bannerBidToken;                    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imab__banner);

        configureBanner();
        setBannerHelperText();
    }

    // Configure the banner and IMAB bid token
    public void configureBanner() {

        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setBannerAdListener(this);
        moPubView.setAdUnitId(MP_BannerAdUnitID);

        inMobiAudienceBidder = IMAudienceBidder.getInstance();

        bannerBidToken = inMobiAudienceBidder.createBidToken(this, IMAB_BannerPLC,
                moPubView, 320, 50, new IMAudienceBidder.IMAudienceBidderBannerListener() {

                    @Override
                    public void onBidReceived(@NonNull MoPubView m) {

                        // If the banner has not yet been loaded, call loadAd to load the ad into the view
                        if (!bannerLoaded) {
                            moPubView.loadAd();
                            toast_updateMPAdViewLoadCalled(true);
                        } else {
                            toast_updateNewBidMade(true);
                        }

                    }

                    @Override
                    public void onBidFailed(@NonNull MoPubView m, @NonNull final Error error) {

                        // If the banner has not yet been loaded, call loadAd on the updated ad view
                        if (!bannerLoaded) {
                            moPubView.loadAd();
                            toast_updateMPAdViewLoadCalled(false);
                        } else {
                            toast_updateNewBidMade(false);
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


    // Various UI helper methods, ignore these. Or if you can improve on them, make a PR! Much thanks.

    // Semi-helpful method to set a long winded explanation
    public void setBannerHelperText() {
        TextView tv = findViewById(R.id.bannerTextView);
        tv.setText(IMAB_Banner_Explanation);
    }

    // Semi-helpful toast method to help with debugging
    public void toast_updateMPAdViewLoadCalled(boolean b){
        Toast.makeText(this, "Load called on MPAdView, with a " + (b?"successful":"failed") + " bid attempt", Toast.LENGTH_SHORT).show();
    }

    // Semi-helpful toast method to help with debugging
    public void toast_updateNewBidMade(boolean b){
        Toast.makeText(this, "New " + (b?"successful":"failed") + " bid attempt made.", Toast.LENGTH_SHORT).show();
    }



}
