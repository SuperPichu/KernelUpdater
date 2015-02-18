package org.superpichu.kernelupdater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import eu.chainfire.libsuperuser.Shell;


public class MainActivity extends Activity {

    //Initialize variables that made sense to be global, rather than try to pass them around.
    public boolean checked = false;
    public boolean downloaded = false;
    public String[] data = new String[3];
    public String path;
    String bbPath = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Allow network access
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Set layout
        setContentView(R.layout.activity_main);

        //Initialize text and get sku
        TextView skuText = (TextView) findViewById(R.id.skuText);
        String sku = Build.PRODUCT;
        //Store sku
        data[0] = sku;

        //Display device version
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
        skuText.setText("Your Shield Tablet is the " + device + " version!");
		
		//Check for root
        if(!Shell.SU.available()){
            DialogFragment newFragment = new displayDialog();
            newFragment.show(getFragmentManager(), "error");
        }

        //Copy busybox binary if first run, set path regardless
        bbPath = "/data/data/" + this.getPackageName()+"/busybox";
		
        //Check if first run
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
			//Intialize variable for empty mounts
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("cifsEmpty",true);
            //Copy busybox binary.
            AssetManager am = getAssets();
            try{
                InputStream in = am.open("busybox");
                OutputStream out=new FileOutputStream(bbPath);
                byte[] buffer = new byte[1024];
                int read;
                while((read = in.read(buffer)) != -1){
                    out.write(buffer, 0, read);
                }
                in.close();
                out.flush();
                out.close();
				
				//Make busybox executable
                new SUMethod().execute("busybox");
            }catch (Exception e){
                e.printStackTrace();
            }
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        try {
			//Get current Version
            TextView version = (TextView) findViewById(R.id.currentText);
            Resources res = getResources();
            version.setText(res.getString(R.string.currentLabel) + " " + new SUMethod().execute("version").get());
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
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
		
        if (id == R.id.action_settings) {
			//Send email.
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"feedback@superpichu.org"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Kernel Updater Feedback");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.cifs_settings) {
            if (Shell.SU.available()){
                Intent intent = new Intent(this, CIFSMounts.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Root Required", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadUpdate() {
		//Initialize DownloadManager
        DownloadManager dm;
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		
		//Set source and destination paths
        Uri uri = Uri.parse(data[2]);
        String[] urlArray = data[2].split("/");
        String fileName = urlArray[urlArray.length - 1];
        path = "/storage/emulated/0/Kernel/" + fileName;
        File file = new File(path);
		
		//Check if file has already been downloaded
        if (file.exists()) {
            Toast.makeText(this, "File already downloaded", Toast.LENGTH_SHORT).show();
        } else {
			
			//Download file
            Toast.makeText(this,"Downloading",Toast.LENGTH_SHORT).show();
            Uri dest = Uri.parse("file://"+path);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Kernel Download")
                    .setDescription(fileName);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationUri(dest);
            dm.enqueue(request);

        }
        downloaded = true;
    }

    public void updateCheck() throws XmlPullParserException{
        Resources res = getResources();
        String url = res.getString(R.string.feed);
		
		//Parse latest version and appropriate link from server xml
        try {
            InputStream in = getXML(url);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (name != null) {
                    if (name.equals("version")){
                        data[1] = parser.nextText();
                    }else if (name.equals(data[0])){
                        data[2] = parser.getAttributeValue(null, "url");
                    }
                }
                eventType = parser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getXML(String urlString) throws IOException {
        //Fast enough that I don't care that networking is in the main thread.
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn.getInputStream();
    }


    public void updateButton(View view) throws XmlPullParserException{
        Toast.makeText(this,"Checking for updates",Toast.LENGTH_SHORT).show();
        TextView version = (TextView) findViewById(R.id.latestText);
		
		//Check for updates
        updateCheck();
        Resources res = getResources();
		
		//Set latest version
        version.setText(res.getString(R.string.latestLabel) +" "+ data[1]);
        checked = true;
    }

    public void downloadButton(View view){

        if(checked){
            downloadUpdate();
        }else{
            Toast.makeText(this, "Check for updates first", Toast.LENGTH_SHORT).show();
        }


    }

    public void installButton(View view){
		//Check for root
        if(Shell.SU.available() && checked && downloaded){
            new SUMethod().execute("install");
        }else{
            DialogFragment newFragment = new displayDialog();
            newFragment.show(getFragmentManager(), "error");
        }

    }

    private class SUMethod extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String s="";
            switch (params[0]) {
                case "version":
				
					//Parse version from uname
                    s = Shell.SH.run(bbPath+" uname -r |"+bbPath+" cut -d+ -f2").get(0);
                    break;
                case "install":
                    List<String> commands = new ArrayList<>();
					
					//Remove any existing file
                    commands.add("rm /cache/recovery/*.zip");
					//Copy update
                    commands.add("cp "+path+" /cache/recovery/update.zip");
					//Signal recovery to install it on boot
                    commands.add("echo \"--update_package=/cache/recovery/update.zip\" > /cache/recovery/command");
					//Reboot tablet
                    commands.add("reboot recovery");
                    Shell.SU.run(commands).get(0);
                    break;
                case "busybox":
					//Make busybox executable
                    Shell.SU.run("chmod 755 "+bbPath);
                    break;
            }
            return s;
        }
    }
	
	//Error message
    public static class displayDialog extends DialogFragment{
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Error");
            builder.setMessage("Do you have root?\nDid you hit update & download first?");
            builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //OK Button
                }
            });

            return builder.create();
        }
    }
}
