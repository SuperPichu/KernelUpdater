package org.superpichu.kernelupdater;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends Activity {


    public boolean checked = false;
    public String[] data = new String[3];
    DownloadManager dm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        TextView skuText = (TextView) findViewById(R.id.skuText);
        String sku = Build.PRODUCT;
        data[0] = sku;
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

    public void downloadUpdate(){
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(data[2]);
        Uri dest = Uri.parse("file:///storage/emulated/0/kernel.zip");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Kernel Download")
               .setDescription("Version: "+data[1]);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationUri(dest);
        long reference = dm.enqueue(request);
    }

    public void updateCheck() throws XmlPullParserException{
        Resources res = getResources();
        String url = res.getString(R.string.feed);
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
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn.getInputStream();
    }


    public void updateButton(View view) throws XmlPullParserException{
        Toast.makeText(this,"Checking for updates",Toast.LENGTH_SHORT).show();
        TextView version = (TextView) findViewById(R.id.versionText);
        updateCheck();
        version.setText(data[1]);
        checked = true;
    }

    public void downloadButton(View view){
        String text = "";

        if(checked){
            text="Downloading...";
            Toast.makeText(this,"Downloading",Toast.LENGTH_SHORT).show();
            downloadUpdate();
        }else{
            Toast.makeText(this, "Check for updates first", Toast.LENGTH_SHORT).show();
        }


    }
}
