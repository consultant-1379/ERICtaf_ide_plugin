package com.ericsson.cifwk.taf.ide.eclipse.tm.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.TM_HOST, "http://taftm.lmera.ericsson.se");
        store.setDefault(PreferenceConstants.TM_API_PREFIX, "/tm-server");
        store.setDefault(PreferenceConstants.TM_WEB_PREFIX, "/");
		// development environment
		if ("dev".equals(System.getProperty("env"))) {
			store.setDefault(PreferenceConstants.TM_HOST, "http://localhost:8080");
		}
	}

}
