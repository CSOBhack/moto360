package cz.gug.csobhack;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    private NotificationCompat.WearableExtender wearableExtender;
    private List<News> news;
    private ListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a WearableExtender to add functionality for wearables
        wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);
        expandableListView = (ListView) findViewById(R.id.expandableListView);


        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(i);
                downloadNews();


            }
        });

    }

    private void downloadNews() {
        try {
            new MyDownloadTask().execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void updateUI(){
        fillList();
        News news1 = news.get(news.size()-1);
        final Notification notif = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(news1.getHeadline())
                .setContentText(news1.getContent())
                .setSmallIcon(R.drawable.ic_drawer)
                .extend(wearableExtender).build();
        NotificationManagerCompat.from(getApplicationContext()).notify(0, notif);
    }

    private void fillList() {
        ListAdapter adapter = new MyListAdapterMain(getApplicationContext());
        expandableListView.setAdapter(adapter);
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

    private class MyDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                news = HackClient.getNews();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });

        }
    }

    public class MyListAdapterMain extends ArrayAdapter<String> {
        private final Context context;

        public MyListAdapterMain(Context context) {
            super(context, R.layout.lst_item, new String[news.size()]);
            this.context = context;
        }

        public View getView(int index, View convertView, ViewGroup parent) {
            View row = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.lst_item, parent, false);
            }

            TextView title = (TextView) row.findViewById(R.id.txvTitle);
            TextView time = (TextView) row.findViewById(R.id.txvTime);
            TextView content = (TextView) row.findViewById(R.id.txvContent);

            if (news == null) return row;
            News n = news.get(index);
            if (n == null) return row;
            title.setText(n.getHeadline());
            content.setText(n.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ENGLISH);
            time.setText(sdf.format(n.getCreated_at()));

            return row;
        }

    }
}
