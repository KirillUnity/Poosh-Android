package me.appstockus.poosh_android.helpers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by CITILINK-PC on 06.10.16.
 */
public class DownloadTask extends AsyncTask<Void, Integer, Void>
{
    private String fromURL;
    private String toPath;
    private ProgressListener progressListener;


    public DownloadTask(String fromPath, String toPath, ProgressListener progressListener) {
        this.fromURL = fromPath;
        this.toPath = toPath;
        this.progressListener = progressListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        int count = -1;

        try {
            final URL url = new URL(fromURL);
            final URLConnection connection = url.openConnection();
            connection.connect();

            final int lenghtOfFile = connection.getContentLength();

            final InputStream input = new BufferedInputStream(url.openStream(), 1024 * 1024);
            final OutputStream output = new FileOutputStream(toPath);

            final byte data[] = new byte[1024];
            long total = 0;

            while ( (count = input.read(data)) != -1 ) {
                total += count;

                publishProgress(
                        (int) (total * 100 / lenghtOfFile)
                );

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        }
        catch (Exception e) {
            Log.e("DownloadTask error: ", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(progressListener != null)
            progressListener.onProgressUpdate( values[0] );
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(progressListener != null)
            progressListener.onProgressUpdate(100);
    }


    /****************************************
     *            ProgressListener
     ***************************************/

    public interface ProgressListener
    {
        void onProgressUpdate(int progress);
    }
}
