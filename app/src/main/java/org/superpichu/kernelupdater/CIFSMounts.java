package org.superpichu.kernelupdater;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import eu.chainfire.libsuperuser.Shell;


public class CIFSMounts extends ListActivity {

    public data data;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cifs_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        File source = new File(getFilesDir(), "shares.xml");
        if (!source.exists()) {
            setContentView(R.layout.cifs_empty);
        } else {
            Serializer serializer = new Persister();

            try {
                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> item;
                data = serializer.read(data.class, source);
                for (Share share : data.share) {
                    item = new HashMap<String, String>();
                    item.put("displayName", share.displayName);
                    item.put("mountPoint", share.mountPoint);
                    list.add(item);
                }
                SimpleAdapter sa = new SimpleAdapter(this, list, android.R.layout.two_line_list_item, new String[]{"displayName", "mountPoint"}, new int[]{android.R.id.text1, android.R.id.text2});
                setListAdapter(sa);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(this, AddMount.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Share share = data.share.get(position);
        mount(share);
    }
    public void mount(Share share){

        File dir = new File("/mnt/cifs" + share.mountPoint);
        if (!dir.exists()) {
            List<String> commands = new ArrayList<>();
            commands.add("mount -o rw,remount /");
            commands.add("mkdir /mnt/cifs/" + share.mountPoint);
            Shell.SU.run(commands);
        }
        String[] command = {"mount -t cifs -o ip=" + share.ipAddress + ",unc=//" + share.ipAddress + "/" + share.shareName + ",user=" + share.userName + ",pass=" + share.password + ",noperm //" + share.ipAddress + "/" + share.shareName + " /mnt/cifs/" + share.mountPoint};
        new Shell().run(Shell.SU.shellMountMaster(),command,null,true);

    }
    public void unmount(Share share){
        Shell.SU.run("umount /mnt/cifs/"+share.mountPoint);
        Toast.makeText(this, "Share unmounted", Toast.LENGTH_SHORT).show();
    }
}
