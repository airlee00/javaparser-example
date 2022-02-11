package com.yourorganization.maven_sample.test;

import static com.github.javaparser.utils.PositionUtils.sortByBeginPosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import com.yourorganization.maven_sample.model.Cls;
import com.yourorganization.maven_sample.model.Lsi;
import com.yourorganization.maven_sample.model.Mth;

/**
 * Some code that uses JavaParser.
 */
public class LogicPositivizer2 {
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
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(LogicPositivizer2.class).resolve("src/main/resources"));

        // Our sample is in the root of this directory, so no package name.
        CompilationUnit cu = sourceRoot.parse("", "GuestbookPsc.java");


       // DefaultPrinterConfiguration conf = new DefaultPrinterConfiguration();
        //DefaultPrettyPrinterVisitor vv = new DefaultPrettyPrinterVisitor(conf);

        //vv.visit(cu, null);

        Cls cls = new Cls();
        VoidVisitor<Cls> methodNameCollector = new MethodNameCollector();
        methodNameCollector.visit(cu, cls);
         System.out.println("Method Name Collected: " + cls);
    }

    public static class MethodNameCollector extends VoidVisitorAdapter<Cls> {

    	@Override
    	public void visit(final CompilationUnit n, Cls cls) {

    		if (n.getPackageDeclaration().isPresent()) {
    			cls.setPackageName(n.getPackageDeclaration().get().getNameAsString());
    		}
    		super.visit(n, cls);
    	}

    	@Override
    	public void visit(final ClassOrInterfaceDeclaration n, Cls cls) {
    		cls.setClassName(n.getNameAsString());
    		 cls.getLoc().set(n.getEnd().get().line);

    		super.visit(n, cls);
    	}

    	@Override
        public void visit(MethodDeclaration md, Cls cls) {
	    	if( md.getModifiers().get(0).getKeyword() == Modifier.Keyword.PUBLIC ) {
	    		Mth mth = new Mth();
	    		mth.setMethodName(md.getNameAsString());
	    		mth.getLoc().set( md.getEnd().get().line - md.getBegin().get().line + 1);
	    		cls.add(mth);
	    		cls.getCurrent().incrementAndGet();

	    	}
            super.visit(md, cls);
        }

    	@Override
    	public void visit(final SingleMemberAnnotationExpr n, Cls cls) {
    		if("ServiceId".contentEquals(n.getNameAsString()))
    			cls.getMths().get(cls.getCurrent().get()).setServiceId(n.getMemberValue().toString());

    		else if("ServiceName".contentEquals(n.getNameAsString()))
    			cls.getMths().get(cls.getCurrent().get()).setServiceName(n.getMemberValue().toString());

    		super.visit(n, cls);
    	}
    	@Override
    	public void visit(final NormalAnnotationExpr n, Cls cls) {
    		if("ServiceId".contentEquals(n.getNameAsString())) {
    	        if (n.getPairs() != null) {
    	            for (final Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext(); ) {
    	                final MemberValuePair m = i.next();
    	                if("value".equals(m.getNameAsString())) {
    	                	cls.getMths().get(cls.getCurrent().get()).setServiceId(m.getValue().toString());
    	                }
    	            }
    	        }
    		}
    		super.visit(n, cls);
    	}

    	@Override
    	public void visit(final VariableDeclarationExpr n, Cls cls) {
            for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
                final VariableDeclarator v = i.next();
                if(v.getInitializer().isPresent()) {
                	Expression ini = v.getInitializer().get();
	                if(ini.toString().contains("TargetServiceInfo")) {
	                	NodeList<Expression> args = ((ObjectCreationExpr)ini).getArguments();
	                	Lsi lsi = new Lsi();
	                	lsi.setApiCode(""+args.get(0));
	                	lsi.setApiUrl(""+args.get(1));
	                	cls.getMths().get(cls.getCurrent().get()).add(lsi);
	                }
                }
            }//
    		super.visit(n, cls);
    	}

    	@Override
    	public void visit(final VariableDeclarator n, final Cls cls) {
			boolean isAnonymous = false;
			if(n.getInitializer().isPresent() ) {//
				Expression l = n.getInitializer().get();//
				if (l instanceof MethodCallExpr) {
					MethodCallExpr mce = (MethodCallExpr) l;
					Optional<Expression> e = mce.getArguments().getFirst();
					if (e.isPresent() && e.get() instanceof ObjectCreationExpr) {
						ObjectCreationExpr oce = (ObjectCreationExpr) e.get();
						if (oce.getAnonymousClassBody().isPresent()) {
							isAnonymous = true;
						}
					}
				}
			}
			if (!isAnonymous)
				super.visit(n, cls);
    	}


    	@Override
    	public void visit(final LineComment n, Cls cls) {
    		int i = cls.getMths().get(cls.getCurrent().get()).getLoc().decrementAndGet();
    		//System.out.println(cls.getMths().get(cls.getCurrent().get()).getMethodName() + "====>" + i);
    		super.visit(n, cls);
    	}

    	@Override
    	public void visit(final BlockComment n, Cls cls) {
    		int diff =  n.getBegin().get().line - n.getEnd().get().line - 1;
    		cls.getMths().get(cls.getCurrent().get()).getLoc().addAndGet(diff);
    		super.visit(n, cls);
    	}

        @Override
        public void visit(final ExpressionStmt n, Cls cls) {
            printOrphanCommentsBeforeThisChildNode(n, cls);
            super.visit(n, cls);
        }

        private void printOrphanCommentsBeforeThisChildNode(final Node node, Cls cls) {
            if (node instanceof Comment) return;

            Node parent = node.getParentNode().orElse(null);
            if (parent == null) return;
            List<Node> everything = new ArrayList<>(parent.getChildNodes());
            sortByBeginPosition(everything);
            int positionOfTheChild = -1;
            for (int i = 0; i < everything.size(); ++i) { // indexOf is by equality, so this is used to index by identity
                if (everything.get(i) == node) {
                    positionOfTheChild = i;
                    break;
                }
            }
            if (positionOfTheChild == -1) {
                throw new AssertionError("I am not a child of my parent.");
            }
            int positionOfPreviousChild = -1;
            for (int i = positionOfTheChild - 1; i >= 0 && positionOfPreviousChild == -1; i--) {
                if (!(everything.get(i) instanceof Comment)) positionOfPreviousChild = i;
            }
            for (int i = positionOfPreviousChild + 1; i < positionOfTheChild; i++) {
                Node nodeToPrint = everything.get(i);
                if (!(nodeToPrint instanceof Comment))
                    throw new RuntimeException(
                            "Expected comment, instead " + nodeToPrint.getClass() + ". Position of previous child: "
                                    + positionOfPreviousChild + ", position of child " + positionOfTheChild);
                nodeToPrint.accept(this, cls);
            }
        }
    }
}


