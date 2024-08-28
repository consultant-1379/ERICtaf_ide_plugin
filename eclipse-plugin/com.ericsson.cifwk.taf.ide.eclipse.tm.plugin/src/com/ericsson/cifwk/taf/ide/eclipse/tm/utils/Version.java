package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import org.osgi.framework.FrameworkUtil;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;

public class Version {
    public static final String VERSION;
    static {
        String version = "unknown";
        try {
            org.osgi.framework.Version ver = FrameworkUtil.getBundle(Version.class).getVersion();
            if (ver != null) {
                version = ver.toString();
            }
        } catch (Exception e) {
            Activator.log("Failed to read version from /META-INF/MANIFEST.mf, using unknown", e);
        }
        VERSION = version;
    }
}
