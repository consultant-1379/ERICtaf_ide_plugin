package com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot;

import java.util.List;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertTextContains;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTEclipseBot;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.Activator;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;

public class UnresolvedJavaTestBase {

    private static SWTWorkbenchBot bot;
    protected static CompilationUnit minimalTestsWithImportUnresolved;
    protected static CompilationUnit minimalTestsWithImportWithAbortUnresolved;
    protected static CompilationUnit minimalTestsWithoutImportUnresolved;

    @BeforeClass
    public static void beforeClass() throws Exception {
        SWTBotPreferences.TIMEOUT = 5000;
        bot = new SWTWorkbenchBot();
        for (SWTBotView view : bot.views()) {
            if ("Welcome".equals(view.getTitle())) {
                view.close();
            }
        }
        // Change the perspective via the Open Perspective dialog
        bot.menu("Window").menu("Open Perspective").menu("Other...").click();
        SWTBotShell openPerspectiveShell = bot.shell("Open Perspective");
        openPerspectiveShell.activate();
        // select the dialog
        bot.table().select("Java");
        bot.button("OK").click();

        bot.menu("File").menu("New").menu("Project...").click();
        SWTBotShell shell = bot.shell("New Project");
        shell.activate();
        bot.tree().expandNode("Java").select("Java Project");
        bot.button("Next >").click();
        bot.textWithLabel("Project name:").setText("MyFirstProject");
        bot.button("Finish").click();

        minimalTestsWithImportUnresolved = getAST("MinimalTestsWithImport");
        minimalTestsWithoutImportUnresolved = getAST("MinimalTestsWithoutImport");
        minimalTestsWithImportWithAbortUnresolved = getAST("minimalTestsWithImportWithAbort");
    }

    @AfterClass
    public static void afterClass() throws Exception {
        SWTBotView packageExplorerView = bot.viewByTitle("Package Explorer");
        packageExplorerView.show();
        Tree swtTree = (Tree) bot.widget(IsInstanceOf.<Tree> instanceOf(Tree.class), packageExplorerView.getWidget());

        SWTBotTree tree = new SWTBotTree(swtTree);

        tree.select("MyFirstProject");

        bot.menu("Edit").menu("Delete").click();

        // the project deletion confirmation dialog
        SWTBotShell shell = bot.shell("Delete Resources");
        shell.activate();
        bot.checkBox("Delete project contents on disk (cannot be undone)").select();
        bot.button("OK").click();
        bot.waitUntil(Conditions.shellCloses(shell));
    }

    private static CompilationUnit getAST(String name) throws InterruptedException, PartInitException {
        // create new java class
        bot.toolbarDropDownButtonWithTooltip("New Java Class").menuItem("Class").click();

        bot.shell("New Java Class").activate();
        bot.textWithLabel("Source folder:").setText("MyFirstProject/src");
        bot.textWithLabel("Package:").setText("org.eclipsecon.project");
        bot.textWithLabel("Name:").setText(name);
        bot.button("Finish").click();

        Bundle bundle = Activator.getDefault().getBundle();
        String contents = FileUtils.read(bundle.getEntry("test-files/" + name + ".java"));
        Thread.sleep(1000);
        SWTBotEclipseEditor editor = bot.editorByTitle(name + ".java").toTextEditor();
        editor.setText(contents);
        editor.save();
        IEditorInput ei = editor.getReference().getEditorInput();
        ITypeRoot input = JavaUI.getEditorInputTypeRoot(ei);
        return SharedASTProvider.getAST((ICompilationUnit) input, SharedASTProvider.WAIT_YES, null);
    }

}
