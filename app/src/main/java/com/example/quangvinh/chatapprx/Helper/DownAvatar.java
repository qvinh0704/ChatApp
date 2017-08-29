package com.example.quangvinh.chatapprx.Helper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.content.QBContent;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.Performer;

import java.io.InputStream;

/**
 * Created by QuangVinh on 3/9/2017.
 */

public class DownAvatar extends AsyncTask<Integer,Void,Performer<InputStream>> {
    IGetAvatar iGetAvatar;
    public DownAvatar(IGetAvatar temp){
        iGetAvatar = temp;
    }
    @Override
    protected Performer<InputStream> doInBackground(Integer... params) {
        return QBContent.downloadFileById(params[0], new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int i) {
                int num = i;
                Log.e("AAA", "Load avatar " + i);
            }
        });
    }

    @Override
    protected void onPostExecute(Performer<InputStream> inputStreamPerformer) {
        super.onPostExecute(inputStreamPerformer);
        inputStreamPerformer.performAsync(new QBEntityCallback<InputStream>() {
            @Override
            public void onSuccess(InputStream inputStream, Bundle bundle) {
                iGetAvatar.onSuccess(inputStream);
            }

            @Override
            public void onError(QBResponseException e) {
                iGetAvatar.onError(e.getMessage());
                Log.e("AAA","inputStreamAva-- "+e.getMessage());
            }
        });
    }
}
