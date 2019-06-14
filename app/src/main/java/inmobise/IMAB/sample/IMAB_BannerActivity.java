package inmobise.IMAB.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
                moPubView, new IMAudienceBidder.IMAudienceBidderBannerListener() {

                    @Override
                    public void onBidReceived(@NonNull MoPubView m) {

                        // If the banner has not yet been loaded, call loadAd to load the ad into the view
                        if (!bannerLoaded) {
                            moPubView.loadAd();
                        }

                    }

                    @Override
                    public void onBidFailed(@NonNull MoPubView m, @NonNull final Error error) {

                        // If the banner has not yet been loaded, call loadAd on the updated ad view
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
        TextView tv = findViewById(R.id.bannerTextView);
        tv.setText(IMAB_Banner_Explanation);
    }



}
