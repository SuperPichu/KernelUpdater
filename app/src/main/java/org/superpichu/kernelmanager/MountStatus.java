package org.superpichu.kernelmanager;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by chris on 2/24/15.
 */
public class MountStatus {
    public static boolean check (Share share){
        boolean status = false;
        String[] command = {Busybox.path+" mountpoint /mnt/cifs/"+share.mountPoint};
        List<String> result = Shell.run(Shell.SU.shellMountMaster(), command, null, true);
        if(result.size()>0 && !result.get(0).contains("not")) {
            status = true;
        }else{
            status = false;
        }
        return status;
    }
}
