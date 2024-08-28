package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;
import com.ericsson.cifwk.taf.ide.eclipse.tm.preferences.PreferenceConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class TestIdChecker {
    protected final static Set<String> cachedIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    protected final static String TEST_CASES_PATH = "/api/test-cases/ids?id=";

    public final static int BATSH_SIZE = 256;
    public final static String USER_AGENT_HEADER = "User-Agent";
    public final static String USER_AGENT = "TMS Eclipse Plugin v";
    public final static String CACHE_FILE = getTempDir() + "tms_plugin_" + Version.VERSION + ".cache";

    private static CountDownLatch initializationBarrier = new CountDownLatch(1);

    protected TestIdChecker() {

    }

    private static String getTempDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir.endsWith("/")) {
            return tmpDir;
        }
        return tmpDir + "/";
    }

    private static TestIdChecker instance;
    static {
        instance = new TestIdChecker();
    }

    public static TestIdChecker getInstance() {
        return instance;
    }

    @VisibleForTesting
    public static void setInstance(TestIdChecker checker) {
        instance = checker;
    }

    private Set<String> readAll() {
        File cacheFile = null;
        try {
            cacheFile = new File(CACHE_FILE);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            String cacheLine = Files.toString(cacheFile, Charset.forName("UTF-8"));
            Set<String> newCache = new HashSet<>();
            for (String id : cacheLine.split("[|]")) {
                if (!"".equals(id)) {
                    newCache.add(id);
                }
            }
            return newCache;
        } catch (IOException e) {
            Activator.log("IO Error, trying to read cache from:" + cacheFile + ", error:" + e.getMessage(), e);
            //
            return new HashSet<>();
        }
    }

    private void writeAll(Set<String> values) {
        File cacheFile = null;
        try {
            cacheFile = new File(CACHE_FILE);
            Files.write(Joiner.on("|").join(values), cacheFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Activator.log("IO Error, trying to write cache to:" + cacheFile + ", error:" + e.getMessage(), e);
        }
    }

    private Set<String> fetchInvalidTestsIds(Set<String> externalTestIds, String encoding) {
        // reading plugin configuration
        Set<String> invalid = new HashSet<>();
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String host = store.getString(PreferenceConstants.TM_HOST);
        String apiPrefix = store.getString(PreferenceConstants.TM_API_PREFIX);
        //
        List<String> allTestIds = new ArrayList<>(externalTestIds);
        Collections.sort(allTestIds);
        if (!allTestIds.isEmpty()) {
            try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
                for (List<String> testIds : Lists.partition(allTestIds, BATSH_SIZE)) {
                    HttpGet httpget = new HttpGet(host + apiPrefix + TEST_CASES_PATH
                            + Joiner.on(",").join(urlencode(testIds, encoding)));
                    httpget.addHeader(USER_AGENT_HEADER, USER_AGENT + Version.VERSION);
                    String responseBody = httpclient.execute(httpget, new BasicResponseHandler());
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> existingIds = mapper.readValue(responseBody, new TypeReference<List<String>>() {
                    });
                    // Compute All invalid IDs which are not present in output
                    // of GET
                    ArrayList<String> ids = new ArrayList(testIds);
                    ids.removeAll(existingIds);
                    invalid.addAll(ids);
                }
            } catch (IOException e) {
                Activator.log("IOError communicating with server:" + host + " error=" + e.getMessage(), e);
                invalid.addAll(allTestIds);
            }
        }
        return invalid;
    }

    private static Thread backgroundTask = new Thread() {
        private Date expires = nextExpires();
        private Date write = nextWrite();

        private Date nextExpires() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 9);
            return cal.getTime();
        }

        private Date nextWrite() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 15);
            return cal.getTime();
        }

        @Override
        public void run() {
            try {
                TestIdChecker checker = TestIdChecker.getInstance();
                // read cache
                cachedIds.addAll(checker.readAll());
                // Initial cache invalidation
                Set<String> invalidTests = checker.fetchInvalidTestsIds(cachedIds, ResourcesPlugin.getEncoding());
                cachedIds.removeAll(invalidTests);
                // Allow validation to proceed
                initializationBarrier.countDown();
                // start periodic tasks
                while (true) {
                    Date current = new Date();
                    if (current.after(expires)) {
                        expires = nextExpires();
                        invalidTests = checker.fetchInvalidTestsIds(cachedIds, ResourcesPlugin.getEncoding());
                        cachedIds.removeAll(invalidTests);
                    }
                    if (current.after(write)) {
                        write = nextWrite();
                        checker.writeAll(cachedIds);
                    }
                    long sleep = Math.min(expires.getTime(), write.getTime()) - new Date().getTime();
                    if (sleep > 0) {
                        Thread.sleep(sleep);
                    }
                }
            } catch (InterruptedException ex) {
                // simply exit this thread
            } catch (Exception ex) {
                Activator.log("Fatal Error executing background task:" + ex.getMessage(), ex);
            }
        }
    };

    public static void startBackgroundTasks() {
        backgroundTask.start();
    }

    public List<Boolean> checkAll(final List<String> testIds) {
        try {
            initializationBarrier.await();
        } catch (InterruptedException e) {
            // TODO: check javadoc
            Thread.currentThread().interrupt();
        }
        Set<String> missingInCache = Sets.difference(new HashSet<>(testIds), cachedIds);
        Set<String> missing = new HashSet<>();
        if (!missingInCache.isEmpty()) {
            missing = fetchInvalidTestsIds(missingInCache, ResourcesPlugin.getEncoding());
        }
        // Add to cache those which were actually present in remote
        cachedIds.addAll(Sets.difference(missingInCache, missing));
        List<Boolean> result = new ArrayList<>();
        for (String id : testIds) {
            if (missing.contains(id)) {
                result.add(false);
            } else {
                result.add(true);
            }
        }
        return result;
    }

    private List<String> urlencode(List<String> in, String encoding) throws UnsupportedEncodingException {
        List<String> result = new ArrayList<String>();
        for (String s : in) {
            result.add(URLEncoder.encode(s, encoding));
        }
        return result;
    }
}