package org.superpichu.kernelupdater;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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
    public File output;

    public static final String ROOT="share";
    public static final String SHARE="shareName";
    public static final String IP="ipAddress";
    public static final String DISPLAY="displayName";
    public static final String USER="userName";
    public static final String PASS="password";
    public static final String POINT="mountPoint";
}
