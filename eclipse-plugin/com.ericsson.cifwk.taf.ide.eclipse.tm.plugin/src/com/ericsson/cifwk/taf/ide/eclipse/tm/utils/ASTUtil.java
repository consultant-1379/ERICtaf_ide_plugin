package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ASTUtil {

    private static String fullyQualifiedName(Name typeName) {
        if (!typeName.isQualifiedName()) {
            IBinding binding = typeName.resolveBinding();
            if (binding != null && binding instanceof ITypeBinding) {
                ITypeBinding typeBinding = (ITypeBinding) binding;
                return typeBinding.getQualifiedName();
            }
        }
        return typeName.getFullyQualifiedName();

    }

    public static NormalAnnotation getAnnotation(BodyDeclaration node, String name) {
        List<?> modifiers = node.modifiers();
        for (Object m : modifiers) {
            if (m instanceof NormalAnnotation) {
                NormalAnnotation a = (NormalAnnotation) m;
                if (name.equals(fullyQualifiedName(a.getTypeName()))) {
                    return a;
                }
            }
        }
        return null;
    }

    public static String getValue(NormalAnnotation annotation, String field) {
        List<MemberValuePair> values = annotation == null ? new ArrayList<>() : annotation.values();
        for (MemberValuePair v : values) {
            if (field.equals(v.getName().getIdentifier())) {
                Object cons = v.getValue().resolveConstantExpressionValue();
                return cons == null ? null : cons.toString();
            }
        }
        return null;
    }

    public static MemberValuePair getMember(NormalAnnotation annotation, String field) {
        List<MemberValuePair> values = annotation == null ? new ArrayList<>() : annotation.values();
        for (MemberValuePair v : values) {
            if (field.equals(v.getName().getIdentifier())) {
                return v;
            }
        }
        return null;
    }

    public static List<String> getValues(NormalAnnotation annotation, String field) {
        List<MemberValuePair> values = annotation == null ? new ArrayList<>() : annotation.values();
        List<String> result = new ArrayList<>();
        for (MemberValuePair v : values) {
            if (field.equals(v.getName().getIdentifier())) {
                Expression cons = v.getValue();
                if (cons instanceof ArrayInitializer) {
                    ArrayInitializer arr = (ArrayInitializer) cons;
                    List<Expression> expressions = arr.expressions();
                    for (Expression ex : expressions) {
                        Object strVal = ex.resolveConstantExpressionValue();
                        if (strVal != null) {
                            result.add(strVal.toString());
                        }
                    }
                } else {
                    Object strVal = cons.resolveConstantExpressionValue();
                    if (strVal != null) {
                        result.add(strVal.toString());
                    }
                }
            }
        }
        return result;
    }

}
