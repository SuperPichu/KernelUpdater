package org.superpichu.kernelupdater;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

/**
 * Created by chris on 2/17/15.
 */
@Root
public class data{
    @ElementList
    public List<Share> share;
}
