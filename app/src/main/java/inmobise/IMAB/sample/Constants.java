package inmobise.IMAB.sample;


public final class Constants {

    private static final Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    // Make the constructor private
    private Constants() {
    }

    final static String log = "IMAB-Example";


    final static String MP_BannerAdUnitID = "549952a8447d4911b8d690c21b66abac";
    final static String MP_InterstitialAdUnitID = "2beb37597378451f85ef0bfba0cd7908";


    final static String IMAB_APPID = "1017084";
    final static String IMAB_BannerPLC = "1055520";
    final static String IMAB_InterstitialPLC = "1064877";

    final static Long IMAB_InterstitialSuccessDelay= 5000L;
    final static Long IMAB_InterstitialFailureDelay = 10000L;


    final static String IMAB_Banner_Explanation = "This example is how IMAB should be implemented for a refreshing banner scenario. The first ad call to InMobi will be made before MoPub is called. Any ad calls to InMobi are made before loadAd is called on the MoPub ad view. This allows MoPub to control the ad refresh rate.";

    final static String IMAB_Interstitial_Explanation = "This example is how IMAB should be implemented for an interstitial. Any ad calls to InMobi should be made before MoPub is called. On onInterstitialDismissed or onInterstitialFailed, this example will start a 10 second timer before making another bid request.";



}
