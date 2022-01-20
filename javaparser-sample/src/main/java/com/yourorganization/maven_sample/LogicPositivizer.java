package com.yourorganization.maven_sample;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

/**
 * Some code that uses JavaParser.
 */
public class LogicPositivizer {
	/*
	 private String prettyPrintField(String code) {
	        CompilationUnit cu = parse(code);
	        return new DefaultPrettyPrinter().print(cu.findFirst(FieldDeclaration.class).get());
	    }

	    private String prettyPrintVar(String code) {
	        CompilationUnit cu = parse(code);
	        return new DefaultPrettyPrinter().print(cu.findAll(VariableDeclarationExpr.class).get(0));
	    }

	    private Optional<ConfigurationOption> getOption(PrinterConfiguration config, ConfigOption cOption) {
	        return config.get(new DefaultConfigurationOption(cOption));
	    }
	*/
    public static void main(String[] args) {
        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class).resolve("src/main/resources"));

        // Our sample is in the root of this directory, so no package name.
        CompilationUnit cu = sourceRoot.parse("", "GuestbookPsc.java");


        List list = cu.getChildNodes();

        List<ClassOrInterfaceDeclaration> list2 = cu.findAll(ClassOrInterfaceDeclaration.class);


        DefaultPrinterConfiguration conf = new DefaultPrinterConfiguration();
        DefaultPrettyPrinterVisitor vv = new DefaultPrettyPrinterVisitor(conf);

        vv.visit(cu, null);
        //conf.setIndentSize(2);
       // conf.addOption(ConfigOption.SPACE_AROUND_OPERATORS);
        //conf.setPrintComments(false);

       //String a33 = vv.toString();//
       //String a34 = new DefaultPrettyPrinter(conf).print(list2.get(0));//.findFirst(FieldDeclaration.class).get();
       //System.out.println(a33);
       //System.out.println(a34);
       //Log.info("" +  a33);
        List<MethodDeclaration> list3=  list2.get(0).findAll(MethodDeclaration.class);

        list3.forEach( a -> {

        	NodeList<Statement> stmt = a.getBody().get().getStatements();
        	//stmt.get(0).
        	stmt.forEach( a2 ->{

        		if( a2 instanceof ExpressionStmt && ((ExpressionStmt)a2).getExpression() instanceof VariableDeclarationExpr) {
        			//((ExpressionStmt)a2).getExpression().get
        			VariableDeclarationExpr n = (VariableDeclarationExpr)((ExpressionStmt)a2).getExpression();
        			//ve.get
        			for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
        		        final VariableDeclarator v = i.next();
        		        Log.info("" +v.getName());
        		        if( v.getInitializer().isPresent()) {
        		        /*	NodeList<Expression> ini = ((ObjectCreationExpr)v.getInitializer().get()).getArguments();
        		        	ini.forEach( aaa ->{
        		        		Log.info("--" + aaa);
        		        	});
        		        	*/
        		        }
        		        if (i.hasNext()) {
        		        	Log.info(", ");
        		        }
        		    }
        		}


        	});

        });
        Log.info("Positivizing!");

        cu.accept(new ModifierVisitor<Void>() {
            /**
             * For every if-statement, see if it has a comparison using "!=".
             * Change it to "==" and switch the "then" and "else" statements around.
             */
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                // Figure out what to get and what to cast simply by looking at the AST in a debugger!
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        /* It's a good idea to clone nodes that you move around.
                            JavaParser (or you) might get confused about who their parent is!
                        */
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    }
                });
                return super.visit(n, arg);
            }
        }, null);

        // This saves all the files we just read to an output directory.
        //System.out.println(sourceRoot.getPrinter().toString());
        sourceRoot.saveAll(
                // The path of the Maven module/project which contains the LogicPositivizer class.
               CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class)
               //            // appended with a path to "output"
                        .resolve(Paths.get("output")));
    }
}
