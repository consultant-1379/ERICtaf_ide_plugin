package com.ericsson.cifwk.taf.ide.eclipse.tm.hyperlink;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ericsson.cifwk.taf.ide.eclipse.tm.Activator;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.BrowserUtil;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitCompleteException;

public class TestIdHyperlinkDetector extends AbstractHyperlinkDetector {

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        try {
            ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);
            if (region == null || textViewer == null || !(textEditor instanceof JavaEditor)) {
                return null;
            }
            ITypeRoot input = JavaUI.getEditorInputTypeRoot(textEditor.getEditorInput());
            if(!(input instanceof ICompilationUnit)) {
                return null;
            }
            ICompilationUnit cu = (ICompilationUnit) input;

            final CompilationUnit root = SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_YES, null);
            if (root == null) {
                return null;
            }
            final int offset = region.getOffset();
            root.accept(new DetectorVisitor(offset, ResourcesPlugin.getEncoding()));
        } catch (VisitCompleteException e) {
            return new IHyperlink[] { new Hyperlink(e.getRegion(), e.getURL()) };
        } catch (VisitAbortedException e) {
            return null;            
        } catch (Exception e) {
            Activator.log("Fatal error in hyperlink detector:"+e.getMessage(), e);
            return null;
        }
        return null;
    }

    private static class Hyperlink extends org.eclipse.jface.text.hyperlink.URLHyperlink {

        public Hyperlink(IRegion region, String urlString) {
            super(region, urlString);
        }

        @Override
        public void open() {
            BrowserUtil.open(getURLString());
        }
    }

}
