package org.superpichu.kernelmanager;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 2/24/15.
 */
public class DeleteMount {
    public static void run(Share share){
        try{
            Serializer serializer = new Persister();
            List<Share> list = new ArrayList<>();
            File source = share.output;
            data data2 = serializer.read(data.class, source);
            for(Share share2 : data2.share){
                if(!(share2.id == share.id)){
                    list.add(share2);
                }
            }
            if(list.size()==0){
                share.output.delete();
            }else {
                data data = new data();
                data.share = list;
                serializer.write(data, share.output);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
