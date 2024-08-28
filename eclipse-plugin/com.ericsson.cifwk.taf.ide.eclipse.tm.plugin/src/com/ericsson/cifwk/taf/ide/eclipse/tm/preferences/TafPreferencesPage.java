package com.ericsson.cifwk.taf.ide.eclipse.tm.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;

public class TafPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public TafPreferencesPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("TAF TM Server");
	}
	
    @Override
    public void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.TM_HOST, "Host:", getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.TM_API_PREFIX, "API Prefix:", getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.TM_WEB_PREFIX, "WEB Prefix:", getFieldEditorParent()));      
    }

    @Override
    public void init(IWorkbench workbench) {
    }
	
}