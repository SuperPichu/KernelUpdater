package org.superpichu.kernelupdater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import java.io.File;

public class AddMount extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mount);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accept:
                saveMount();
                finish();
            case R.id.action_cancel:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveMount() {
        Share share = new Share();
        EditText et = (EditText)findViewById(R.id.displayName);
        share.displayName = et.getText().toString();
        et = (EditText)findViewById(R.id.ipAddress);
        share.ipAddress = et.getText().toString();
        et = (EditText)findViewById(R.id.shareName);
        share.shareName = et.getText().toString();
        et = (EditText)findViewById(R.id.userName);
        share.userName = et.getText().toString();
        et = (EditText)findViewById(R.id.password);
        share.password = et.getText().toString();
        et = (EditText)findViewById(R.id.mountPoint);
        share.mountPoint = et.getText().toString();
        share.output = new File(getFilesDir(), "shares.xml");
        SaveMount.write(share);
        finish();
    }
}
