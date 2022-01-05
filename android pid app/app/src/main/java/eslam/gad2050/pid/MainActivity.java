package eslam.gad2050.pid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    final String TAG = "main activity";
    String http = "http://www.eslam-gad.com/";
    EditText k_p_edit_text, k_i_edit_text, k_d_edit_text;
    SeekBar set_point_seek_bar;
    TextView set_point_text_view, pid_text_view, error_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        k_p_edit_text = (EditText) findViewById(R.id.k_p);
        k_i_edit_text = (EditText) findViewById(R.id.k_i);
        k_d_edit_text = (EditText) findViewById(R.id.k_d);

        set_point_seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        set_point_text_view = (TextView) findViewById(R.id.set_point);

        pid_text_view = (TextView) findViewById(R.id.pid);
        error_text_view = (TextView) findViewById(R.id.error);


        set_point_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                set_point_text_view.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                http = "http://www.eslam-gad.com/";
                http += "#?";
                connect con = new connect();
                con.execute();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public void modify(View view) {
        http = "http://www.eslam-gad.com/";
        http += "*p" + k_p_edit_text.getText().toString() + "i"
                + k_i_edit_text.getText().toString() + "d" + k_d_edit_text.getText().toString()
                + "s" + (float) set_point_seek_bar.getProgress() + "?";
        connect con = new connect();
        con.execute();
    }

    public class connect extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");
            try {
                URL url = new URL(http);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response_code = connection.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "doInBackground: ok");
                    InputStream inputStream = connection.getInputStream();
                    String s = convertInputStream(inputStream, "UTF-8");
                    Log.d(TAG, "doInBackground: " + s);
                    set_response(s);

                }
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: " + e);
            }

            return null;
        }

        private String convertInputStream(InputStream is, String encoding) {
            Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }

    }

    public void set_response(String response) {
        pid_text_view.setText(response.substring(response.indexOf("p") + 1, response.indexOf("e")));
        error_text_view.setText(response.substring(response.indexOf("e") + 1));
        Toast.makeText(this, "hello toast", Toast.LENGTH_SHORT).show();
    }
}