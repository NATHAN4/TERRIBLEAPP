package projects.terribleapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public final UUID PEBBLE_APP_UID = UUID.fromString("074f788e-b5f0-4748-9f79-95a7fc9310a8");

    public void setField(String value) {
        TextView answer = (TextView)findViewById(R.id.answer);
        answer.setText(value);
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            CheckForResponse checkTask = new CheckForResponse();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            checkTask.execute();
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText)findViewById(R.id.codeBox);
        final Button loginButton = (Button)findViewById(R.id.runButton);
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(editText.getText().toString());
            }
        });
        callAsynchronousTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                String sender  = URLEncoder.encode("value", "UTF-8")
                        + "=" + URLEncoder.encode(params[0], "UTF-8");

                URL url = new URL("http://www.watwhenwhere.ca/TerribleRacketIDE/datainput.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(sender);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                resp = rd.readLine();

                wr.close();
                rd.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                resp = "error 1";
            } catch (IOException e) {
                e.printStackTrace();
                resp = "error 2";
            }

            if(resp.equals("success")){
                System.out.println("SUCCESSFULLY UPLOADED");
            }else{
                System.out.println("ERROR: " + resp);
            }
            return resp;
        }

        /*v
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    //PINGS PHP CHECKING FOR A FINISHED OUTPUT TO SEND TO PEBBLE
    private class CheckForResponse extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            try {
                String sender  = URLEncoder.encode("value", "UTF-8")
                        + "=" + URLEncoder.encode("", "UTF-8");

                URL url = new URL("http://www.watwhenwhere.ca/TerribleRacketIDE/androidcheck.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(sender);
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                resp = rd.readLine();

                wr.close();
                rd.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                resp = "error 1";
            } catch (IOException e) {
                e.printStackTrace();
                resp = "error 2";
            }

            return resp;
        }

        /*v
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if(resp.equals("error 1") || resp.equals("error 2")){
                System.out.println("ERROR: " + resp);
            }else{
                if(!resp.equals("empty")){
                    System.out.println("GOT A RESPONSE: " + resp);
                    setField(resp);
                }
            }

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
