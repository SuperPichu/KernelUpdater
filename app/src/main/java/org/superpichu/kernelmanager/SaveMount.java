package org.superpichu.kernelmanager;


import android.util.Base64;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 2/17/15.
 */
public class SaveMount {

    public static void write(Share share,boolean edit) {
        try{
            share.password = Base64.encodeToString( share.password.getBytes(), Base64.DEFAULT );
            Serializer serializer = new Persister();
            List<Share> list = new ArrayList<>();
            File source = share.output;
            if(source.exists()) {
                data data2 = serializer.read(data.class, source);
                for (Share share2 : data2.share) {
                    if(share2.id == share.id){
                        list.add(share);
                    }else{
                        list.add(share2);
                    }
                }
            }
            if (!edit){
                list.add(share);
            }
            data data = new data();
            data.share = list;
            serializer.write(data, share.output);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
