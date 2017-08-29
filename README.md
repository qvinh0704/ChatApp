# ChatApp
1. Run demo</br>
Install demo_chatapp.apk in folder apk
2. Edit and build app</br>
2.1 Edit google map api key</br>
You need to change google map api key in AndroidManifest.xml. If not, google map will not display when you build app.</br>
2.2 Change provider</br>
- Run by Genymotion emulator:     </br>
public static final String PROVIDER = LocationManager.GPS_PROVIDER; (in Const.java)</br>
- Run by device : </br>
public static final String PROVIDER = LocationManager.NETWORK_PROVIDER; (in Const.java)</br>
