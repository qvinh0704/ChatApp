# ChatApp
1. Run demo
Install demo_chatapp.apk in folder apk
2. Edit and build app
2.1 Edit google map api key
You need to change google map api key in AndroidManifest.xml. If not, google map will not display when you build app.
2.2 Change provider
- Run by Genymotion emulator:     public static final String PROVIDER = LocationManager.GPS_PROVIDER; (in Const.java)
- Run by device : public static final String PROVIDER = LocationManager.NETWORK_PROVIDER; (in Const.java)
