package org.superpichu.kernelupdater;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text1 = (TextView) findViewById(R.id.text1);
        String sku = Build.PRODUCT;
        String device = "";
        switch (sku){
            case "wx_na_wf":device = "WiFi";
                break;
            case "wx_na_do":device = "LTE US";
                break;
            case "wx_un_do":device = "LTE ROW";
                break;
            case "wx_un_mo":device = "Non-existent Modem Model";
                break;
        }
        text1.setText("Your Shield Tablet is the " + device +" version!\n" + sku);
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
