package com.ericsson.cifwk.taf.ide.eclipse.tm.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditorActionContributor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.BrowserUtil;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitCompleteException;

public class OpenHandler extends AbstractHandler {
    private static final String PARAM_ID_ELEMENT_REF = "elementRef"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (!(editor instanceof JavaEditor)) {
            return null;
        }
        ISelection sel = HandlerUtil.getActiveMenuSelection(event);
        if (sel == null || !(sel instanceof ITextSelection)) {
            return null;
        }
        ITextSelection textSel = (ITextSelection) sel;

        ITypeRoot input = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
        if (!(input instanceof ICompilationUnit)) {
            return null;
        }
        ICompilationUnit cu = (ICompilationUnit) input;
        try {
            final CompilationUnit root = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_YES, null);
            if (root == null) {
                return null;
            }
            root.accept(new DetectorVisitor(textSel.getOffset(), ResourcesPlugin.getEncoding()));
        } catch (VisitAbortedException e) {
            IEditorActionBarContributor contributorInterface = editor.getEditorSite().getActionBarContributor();            
            if (contributorInterface instanceof EditorActionBarContributor) {
                EditorActionBarContributor contributor = (EditorActionBarContributor) contributorInterface;
                contributor.getActionBars().getStatusLineManager()
                        .setErrorMessage("Invalid selection: Please select @TestId annotation");
            } else {
                Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
                MessageDialog.openInformation(shell, "Invalid selection", "Please select @TestId annotation");
            }            
            return null;
        } catch (VisitCompleteException e) {
            BrowserUtil.open(e.getURL());
            return null;
        } catch (Exception e) {
            Activator.log("Fatal error in open link handler:" + e.getMessage(), e);
            IEditorActionBarContributor contributorInterface = editor.getEditorSite().getActionBarContributor();            
            if (contributorInterface instanceof EditorActionBarContributor) {
                EditorActionBarContributor contributor = (EditorActionBarContributor) contributorInterface;
                contributor.getActionBars().getStatusLineManager()
                        .setErrorMessage("Invalid selection: Please select @TestId annotation");
            } else {
                Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
                MessageDialog.openInformation(shell, "Invalid selection", "Please select @TestId annotation");
            }            
            return null;
        }
        IEditorActionBarContributor contributorInterface = editor.getEditorSite().getActionBarContributor();
        if (contributorInterface instanceof EditorActionBarContributor) {
            EditorActionBarContributor contributor = (EditorActionBarContributor) contributorInterface;
            contributor.getActionBars().getStatusLineManager()
                    .setErrorMessage("Invalid selection: Please select @TestId annotation");
        } else {
            Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
            MessageDialog.openInformation(shell, "Invalid selection", "Please select @TestId annotation");
        }
        return null;
    }

}
