package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;
import com.ericsson.cifwk.taf.ide.eclipse.tm.preferences.PreferenceConstants;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

public class BrowserUtil {
    private final static String EDIT_TC = "/#tm/editTC/";
    private final static String CREATE_TC_START = "/#tm/createTC?executionType=Automated&testCaseId=";
    private final static String TITLE = "&title=";
    private final static String CONTEXT = "&context=";
    private final static String GROUP = "&group=";
           
    public static void open(String url) {
        // Open Browser
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        IWebBrowser browser;
        try {
            browser = support.createBrowser(null);
        } catch (PartInitException e) {
            return;
        }
        try {
            browser.openURL(new URL(url));
        } catch (PartInitException e) {
            return;
        } catch (MalformedURLException e) {
            return;
        }
        return;
    }
    
    public static String buildUrl(String testId, String title, List<String> contexts, List<String> groups, String encoding) throws UnsupportedEncodingException {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String tmHost = store.getString(PreferenceConstants.TM_HOST);
        String webPrefix = store.getString(PreferenceConstants.TM_WEB_PREFIX);

        boolean testExists = TestIdChecker.getInstance().checkAll(Lists.newArrayList(testId)).get(0);
        StringBuilder path = new StringBuilder();
        path.append(tmHost);
        if (!"/".equals(webPrefix)) {
            path.append(webPrefix);
        }
        if (testExists) {
            path.append(EDIT_TC);
            path.append(URLEncoder.encode(testId, encoding));
            return path.toString();
        } else {
            path.append(CREATE_TC_START);
            path.append(URLEncoder.encode(testId, encoding));
        }
        if (title != null) {
            path.append(TITLE);
            path.append(URLEncoder.encode(title, encoding));
        }
        for (String context : contexts) {
            path.append(CONTEXT);
            path.append(URLEncoder.encode(context, encoding));
        }
        for (String group : groups) {
            path.append(GROUP);
            path.append(URLEncoder.encode(group, encoding));
        }
        return path.toString();
    }
    
}
