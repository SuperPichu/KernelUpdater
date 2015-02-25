package org.superpichu.kernelupdater;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import eu.chainfire.libsuperuser.Shell;


public class CIFSMounts extends ListActivity {

    public data data;
    public Share share;
    public boolean status = false;
    MenuItem mount;
    MenuItem unmount;

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
                    status = MountStatus.check(share);
                    String statusStr;
                    if(status){
                        statusStr = " Mounted";
                    }else{
                        statusStr = " Not Mounted";
                    }
                    System.out.println("*"+share.password+"*");
                    share.password = new String( Base64.decode( share.password, Base64.DEFAULT ) );
                    System.out.println("*"+share.password+"*");
                    item.put("displayName", share.displayName);
                    item.put("Summary", share.mountPoint + statusStr);
                    list.add(item);
                }
                SimpleAdapter sa = new SimpleAdapter(this, list, android.R.layout.two_line_list_item, new String[]{"displayName", "Summary"}, new int[]{android.R.id.text1, android.R.id.text2});
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
        share = data.share.get(position);
        registerForContextMenu(l);
        openContextMenu(l);
        unregisterForContextMenu(l);
        //mount(share);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cifs_context, menu);
        mount = menu.findItem(R.id.mount);
        mount.setEnabled(!status);
        unmount = menu.findItem(R.id.unmount);
        unmount.setEnabled(status);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Intent intent;
        closeContextMenu();
        switch (item.getItemId()) {
            case R.id.edit:
                intent = new Intent(this, EditMount.class);
                intent.putExtra("name",share.displayName);
                intent.putExtra("mountpoint",share.mountPoint);
                intent.putExtra("address",share.ipAddress);
                intent.putExtra("share",share.shareName);
                intent.putExtra("user",share.userName);
                intent.putExtra("pass",share.password);
                intent.putExtra("id",share.id);
                startActivity(intent);
                recreate();
                return true;
            case R.id.delete:
                share.output = new File(getFilesDir(), "shares.xml");
                DeleteMount.run(share);
                recreate();
                return true;
            case R.id.mount:
                mount(share);
                recreate();
                return true;
            case R.id.unmount:
                unmount(share);
                recreate();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    public void mount(Share share){

        File dir = new File("/mnt/cifs" + share.mountPoint);
        if (!dir.exists()) {
            List<String> commands = new ArrayList<>();
            commands.add("mount -o rw,remount /");
            commands.add("mkdir /mnt/cifs");
            commands.add("chmod 777 /mnt/cifs");
            commands.add("mkdir /mnt/cifs/" + share.mountPoint);
            Shell.SU.run(commands);
        }
        String[] command = {"mount -t cifs -o ip=" + share.ipAddress + ",unc=//" + share.ipAddress + "/" + share.shareName + ",user=" + share.userName + ",pass=" + share.password + ",noperm //" + share.ipAddress + "/" + share.shareName + " /mnt/cifs/" + share.mountPoint};
        List<String> result = new Shell().run(Shell.SU.shellMountMaster(), command, null, true);
        if(result != null && result.size()>0) System.out.println(result.get(0));
        Toast.makeText(this, "Share mounted", Toast.LENGTH_SHORT).show();
        status = MountStatus.check(share);
        mount.setEnabled(!status);
        unmount.setEnabled(status);
    }
    public void unmount(Share share){
        String[] command = {"umount /mnt/cifs/"+share.mountPoint};
        new Shell().run(Shell.SU.shellMountMaster(), command, null, true);
        Toast.makeText(this, "Share unmounted", Toast.LENGTH_SHORT).show();
        status = MountStatus.check(share);
        mount.setEnabled(!status);
        unmount.setEnabled(status);
    }
}
