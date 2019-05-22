package inmobise.IMAB.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import com.inmobi.ads.InMobiAudienceBidder;
import com.inmobi.sdk.InMobiSdk;

import party.parrot.partyparrot.R;

import static inmobise.IMAB.sample.Constants.IMAB_APPID;
import static inmobise.IMAB.sample.Constants.MP_BannerAdUnitID;
import static inmobise.IMAB.sample.Constants.log;

public class IMAB_MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_main);
        initializeAdSDK();
        getDisplaySDKVersions();        // Update the view!
    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                //  MoPub SDK initialized.
                Log.d(log, "MoPub SDK initialized");

            }
        };
    }

    public void initializeAdSDK() {

        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(MP_BannerAdUnitID).build();
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

        InMobiAudienceBidder.initialize(this, IMAB_APPID);

    }

    public void getDisplaySDKVersions() {
        TextView mpv = findViewById(R.id.MPSdkVersion);
        mpv.setText("MoPub SDK Version:" + MoPub.SDK_VERSION);

        TextView imv = findViewById(R.id.IMSdkVersion);
        imv.setText("IM SDK Version:" + InMobiSdk.getVersion());
    }

    public void showBannerActivity(View view){
        Intent intent = new Intent(this, IMAB_BannerActivity.class);
        startActivity(intent);

    }

    public void showInterstitialActivity(View view){
        Intent intent = new Intent(this,IMAB_InterstitialActivity.class);
        startActivity(intent);

    }

}


