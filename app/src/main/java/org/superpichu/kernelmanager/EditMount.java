package org.superpichu.kernelmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import java.io.File;


/**
 * Created by chris on 2/24/15.
 */
public class EditMount extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add_mount);
        EditText displayName = (EditText)findViewById(R.id.displayName);
        EditText address = (EditText)findViewById(R.id.ipAddress);
        EditText share = (EditText)findViewById(R.id.shareName);
        EditText user = (EditText)findViewById(R.id.userName);
        EditText pass = (EditText)findViewById(R.id.password);
        EditText mount = (EditText)findViewById(R.id.mountPoint);
        displayName.setText(intent.getStringExtra("name"));
        address.setText(intent.getStringExtra("address"));
        share.setText(intent.getStringExtra("share"));
        user.setText(intent.getStringExtra("user"));
        pass.setText(intent.getStringExtra("pass"));
        mount.setText(intent.getStringExtra("mountpoint"));
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
        SaveMount.write(share,true);
        finish();
    }
}
