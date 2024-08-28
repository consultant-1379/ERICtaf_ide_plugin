package com.ericsson.cifwk.taf.ide.eclipse.tm.validator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.ASTUtil;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;

public class TestIdValidator extends AbstractValidator {

    public static final String ID = "com.ericsson.cifwk.taf.ide.eclipse.tm.validator.TestIdValidator";

    @Override
    public ValidationResult validate(ValidationEvent event, ValidationState state, IProgressMonitor monitor) {
        final IResource resource = event.getResource();
        final ValidationResult result = validateResource(resource);
        return result;
    }

    private boolean validExtension(String ext) {
        for (String e : JavaCore.getJavaLikeExtensions()) {
            if (ext.equals(e)) {
                return true;
            }
        }
        return false;
    }

    public ValidationResult validateResource(final IResource resource) {
        final ValidationResult result = new ValidationResult();
        try {
            if (resource instanceof IFile && resource.exists()) {
                final IJavaElement javaResource = JavaCore.create(resource);
                if (!(javaResource instanceof ICompilationUnit)) {
                    return result;
                }
                if (!validExtension(resource.getFileExtension())) {
                    return result;
                }
                final CompilationUnit root;
                // sometimes fails on groovy projects for java files
                // "Path must include project and resource name: /taf-core-3.0.21.jar"
                root = SharedASTProvider.getAST((ICompilationUnit) javaResource, SharedASTProvider.WAIT_YES, null);
                if (root == null) {
                    return result;
                }

                final List<String> ids = new ArrayList<>();
                final List<Integer> lineNumbers = new ArrayList<>();
                final List<Integer> starts = new ArrayList<>();
                final List<Integer> ends = new ArrayList<>();

                root.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration method) {
                        NormalAnnotation testIdAnnotation = ASTUtil.getAnnotation(method, "com.ericsson.cifwk.taf.annotations.TestId");
                        if (testIdAnnotation == null) {
                            return false;
                        }
                        String id = ASTUtil.getValue(testIdAnnotation, "id");
                        if (id != null) {
                            ids.add(id);
                            lineNumbers.add(root.getLineNumber(testIdAnnotation.getStartPosition()));
                            MemberValuePair idMember = ASTUtil.getMember(testIdAnnotation, "id");
                            starts.add(idMember.getStartPosition());
                            ends.add(idMember.getStartPosition() + idMember.getLength());
                        }
                        return false;
                    }
                });
                List<Boolean> valids = TestIdChecker.getInstance().checkAll(ids);
                for (int i = 0; i < ids.size(); i++) {
                    if (!valids.get(i)) {
                        ValidatorMessage vm = ValidatorMessage.create("There is no such test ID on server", resource);
                        vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                        vm.setAttribute(IMarker.SOURCE_ID, IMarker.PROBLEM);
                        vm.setAttribute(IMarker.LINE_NUMBER, lineNumbers.get(i));
                        vm.setAttribute(IMarker.CHAR_START, starts.get(i));
                        vm.setAttribute(IMarker.CHAR_END, ends.get(i));
                        result.add(vm);
                    }
                }
            }
        } catch (Exception ex) {
            return result;
        }
        return result;
    }

}
