package org.superpichu.kernelupdater;

import android.content.Context;
import android.util.Xml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chris on 2/17/15.
 */
public class SaveMount {

    public static void write(Share share) {
        try{
            Serializer serializer = new Persister();
            List<Share> list = new ArrayList<>();
            File source = share.output;
            if(source.exists()) {
                data data2 = serializer.read(data.class, source);
                for (Share share2 : data2.share) {
                    list.add(share2);
                }
            }
            list.add(share);
            data data = new data();
            data.share = list;
            serializer.write(data, share.output);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
