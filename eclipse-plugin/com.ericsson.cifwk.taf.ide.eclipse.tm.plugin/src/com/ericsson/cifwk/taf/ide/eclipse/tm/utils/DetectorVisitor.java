package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class DetectorVisitor extends ASTVisitor {
    private int offset;
    private String encoding;

    public DetectorVisitor(int offset, String encoding) {
        this.offset = offset;
        this.encoding = encoding;
    }

    @Override
    public boolean visit(MethodDeclaration method) {
        // See if it NOT fits in our offset
        final int endPosition = method.getBody() == null ? method.getStartPosition() + method.getLength() : method
                .getBody().getStartPosition();
        // optimization, to not walk rest of methods
        if (method.getStartPosition() > offset) {
            throw new VisitAbortedException();
        }
        if (!(method.getStartPosition() <= offset && offset <= endPosition)) {
            return false;
        }
        NormalAnnotation testIdAnnotation = ASTUtil.getAnnotation(method, "com.ericsson.cifwk.taf.annotations.TestId");
        NormalAnnotation contextAnnotation = ASTUtil
                .getAnnotation(method, "com.ericsson.cifwk.taf.annotations.Context");
        NormalAnnotation testAnnotation = ASTUtil.getAnnotation(method, "org.testng.annotations.Test");
        if (testIdAnnotation == null) {
            return false;
        }
        String id = ASTUtil.getValue(testIdAnnotation, "id");
        String title = ASTUtil.getValue(testIdAnnotation, "title");
        if (id == null) {
            return false;
        }

        List<String> contexts = ASTUtil.getValues(contextAnnotation, "context");
        List<String> groups = ASTUtil.getValues(testAnnotation, "groups");

        ASTNode clazz = method.getParent();
        if (clazz instanceof TypeDeclaration) {
            TypeDeclaration type = (TypeDeclaration) clazz;
            NormalAnnotation classTestAnnotation = ASTUtil.getAnnotation(type, "org.testng.annotations.Test");
            if (groups.isEmpty() && classTestAnnotation != null) {
                // attempt to get groups from class level
                groups.addAll(ASTUtil.getValues(classTestAnnotation, "groups"));
            }
            NormalAnnotation classContextAnnotation = ASTUtil.getAnnotation(type,
                    "com.ericsson.cifwk.taf.annotations.Context");
            if (contexts.isEmpty() && classContextAnnotation != null) {
                contexts.addAll(ASTUtil.getValues(classContextAnnotation, "context"));
            }
        }
        try {
            String url = BrowserUtil.buildUrl(id, title, contexts, groups, encoding);
            IRegion urlRegion = new Region(method.getStartPosition(), endPosition - method.getStartPosition());
            throw new VisitCompleteException(url, urlRegion);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }
}
