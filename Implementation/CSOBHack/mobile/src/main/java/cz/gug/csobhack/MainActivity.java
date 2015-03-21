package cz.gug.csobhack;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    private NotificationCompat.WearableExtender wearableExtender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a WearableExtender to add functionality for wearables
        wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);

        // Create a NotificationCompat.Builder to build a standard notification
        // then extend it with the WearableExtender
        Notification notif = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_drawer)
        .extend(wearableExtender).build();


        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(i);



            }
        });

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
}
