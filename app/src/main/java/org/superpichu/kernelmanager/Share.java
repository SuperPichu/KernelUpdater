package org.superpichu.kernelmanager;
import org.simpleframework.xml.Element;

import java.io.File;

public class Share {
    @Element
    public String ipAddress;
    @Element
    public String displayName;
    @Element
    public String userName;
    @Element
    public String password;
    @Element
    public String mountPoint;
    @Element
    public String shareName;
    @Element
    public int id;

    public File output;

    public boolean status;
    public static final String ROOT="share";
    public static final String SHARE="shareName";
    public static final String IP="ipAddress";
    public static final String DISPLAY="displayName";
    public static final String USER="userName";
    public static final String PASS="password";
    public static final String POINT="mountPoint";
    public static final String OUT="shares.xml";
}
