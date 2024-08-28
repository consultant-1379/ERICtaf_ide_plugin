package com.ericsson.cifwk.taf.ide.eclipse.tm;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "com.ericsson.cifwk.taf.ide.eclipse.tm.TestIdValidator";

    // The shared instance
    private static Activator plugin;

    public Activator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    @Override
    public void earlyStartup() {
        // eager invalidate cache during eclipse startup
        TestIdChecker.startBackgroundTasks();
    }

    public static void log(String msg) {
         Activator.log(msg, null);
    }

    public static void log(String msg, Exception e) {
        if(plugin != null) {
            plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, msg, e));
        }
    }
}
