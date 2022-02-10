package com.toms.javaparser.sample;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import com.toms.javaparser.model.Cls;
import com.toms.javaparser.visitor.MethodInfoCollectorVisitor;

/**
 * Some code that uses JavaParser.
 */
public class SinglePscParserMain {

    public static void main(String[] args) {
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(SinglePscParserMain.class).resolve("src/main/resources"));
       // CompilationUnit cu = sourceRoot.parse("", "CustInqyCLC.java");
        CompilationUnit cu = sourceRoot.parse("", "GuestbookPsc.java");
       // CompilationUnit cu = sourceRoot.parse("", "FepControl.java");
       // CompilationUnit cu = sourceRoot.parse("", "AdupStofPSC.java");


        DefaultPrinterConfiguration conf = new DefaultPrinterConfiguration();
        DefaultPrettyPrinterVisitor vv = new DefaultPrettyPrinterVisitor(conf);

        vv.visit(cu, null);

        System.out.println(vv.toString());

        Cls cls = new Cls();
        VoidVisitor<Cls> methodNameCollector = new MethodInfoCollectorVisitor();
        methodNameCollector.visit(cu, cls);
        System.out.println("Method Name Collected: " + cls);
    }

}


