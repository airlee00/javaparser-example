package com.yourorganization.maven_sample.visitor;

import static com.github.javaparser.utils.PositionUtils.sortByBeginPosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.yourorganization.maven_sample.model.Cls;
import com.yourorganization.maven_sample.model.Lsi;
import com.yourorganization.maven_sample.model.Mth;

public class MethodNameCollector1 extends VoidVisitorAdapter<Cls> {

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
    
//    @Override
//    public void visit(final AnnotationMemberDeclaration n, final  Cls cls) {
//		Mth mth = new Mth();
//		mth.setMethodName(n.getNameAsString());
//    	//super.visit(n, cls);
//     }
//    @Override
//    public void visit(final AnnotationDeclaration n, final  Cls cls) {
//    	cls.setClassName(n.getNameAsString());
//    	cls.getLoc().set(n.getEnd().get().line + 1);
//    	//super.visit(n, cls);
//    }
    
    
	@Override
	public void visit(final MethodCallExpr n, Cls cls) {
		boolean isAnonymous = false;
		NodeList<Expression> nodelist = n.getArguments();
		for (Expression e : nodelist) {
			if (e instanceof ObjectCreationExpr) {
				ObjectCreationExpr oce = (ObjectCreationExpr) e;
				if (oce.getAnonymousClassBody().isPresent()) {
					isAnonymous = true;
				}
			}
		}
		if (!isAnonymous)
			super.visit(n, cls);
	}

	@Override
	public void visit(MethodDeclaration md, Cls cls) {

		// if( md.getModifiers().get(0).getKeyword() == Modifier.Keyword.PUBLIC ) {
		Mth mth = new Mth();
		mth.setMethodName(md.getNameAsString());
		mth.setModifier(md.getModifiers().get(0).getKeyword().asString());
		int locTotal = md.getEnd().get().line - md.getBegin().get().line + 1;
		mth.getLoc().set(locTotal);
		mth.setLocTotal(locTotal);
		cls.add(mth);
		cls.getCurrent().incrementAndGet();
		// }
		super.visit(md, cls);
	}

	@Override
	public void visit(final SingleMemberAnnotationExpr n, Cls cls) {
		if ("ServiceId".contentEquals(n.getNameAsString()))
			cls.getMths().get(cls.getCurrent().get()).setServiceId(n.getMemberValue().toString());

		else if ("ServiceName".contentEquals(n.getNameAsString()))
			cls.getMths().get(cls.getCurrent().get()).setServiceName(n.getMemberValue().toString());

		super.visit(n, cls);
	}

	@Override
	public void visit(final NormalAnnotationExpr n, Cls cls) {
		if ("ServiceId".contentEquals(n.getNameAsString())) {
			if (n.getPairs() != null) {
				for (final Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext();) {
					final MemberValuePair m = i.next();
					if ("value".equals(m.getNameAsString())) {
						cls.getMths().get(cls.getCurrent().get()).setServiceId(m.getValue().toString());
					}
				}
			}
		}
		super.visit(n, cls);
	}

	@Override
	public void visit(final VariableDeclarationExpr n, Cls cls) {
		for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext();) {
			final VariableDeclarator v = i.next();
			if (v.getInitializer().isPresent()) {
				Expression ini = v.getInitializer().get();
				if (ini.toString().contains("TargetServiceInfo")) {
					NodeList<Expression> args = ((ObjectCreationExpr) ini).getArguments();
					Lsi lsi = new Lsi();
					lsi.setApiCode("" + args.get(0));
					lsi.setApiUrl("" + args.get(1));
					cls.getMths().get(cls.getCurrent().get()).add(lsi);
				}
			}
		} //
		super.visit(n, cls);
	}

	@Override
	public void visit(final VariableDeclarator n, final Cls cls) {
		/*
		 * boolean isAnonymous = false; if(n.getInitializer().isPresent() ) {//
		 * Expression l = n.getInitializer().get();// if (l instanceof MethodCallExpr) {
		 * MethodCallExpr mce = (MethodCallExpr) l; Optional<Expression> e =
		 * mce.getArguments().getFirst(); if (e.isPresent() && e.get() instanceof
		 * ObjectCreationExpr) { ObjectCreationExpr oce = (ObjectCreationExpr) e.get();
		 * if (oce.getAnonymousClassBody().isPresent()) { isAnonymous = true; } } } } if
		 * (!isAnonymous)
		 */
		super.visit(n, cls);
	}
//
//	@Override
//	public void visit(final LineComment n, Cls cls) {
//		try {
//			if (n.getCommentedNode().isPresent() ) {
//				Node node = n.getCommentedNode().get();
//				if( node instanceof FieldDeclaration || node instanceof AnnotationDeclaration) {
//					return;
//				}
//			}else {
//				cls.getMths().get(cls.getCurrent().get()).getLoc().decrementAndGet();
//				// System.out.println(cls.getMths().get(cls.getCurrent().get()).getMethodName()+
//				// "====>" + i);
//			}
//		} catch (Exception e) {
//			System.out.println(cls + e.getMessage());
//			throw e;
//		}
//		super.visit(n, cls);
//	}
//
//	@Override
//	public void visit(final BlockComment n, Cls cls) {
//		try {
//			if (n.getCommentedNode().isPresent() ) {
//				Node node = n.getCommentedNode().get();
//				if( node instanceof FieldDeclaration || node instanceof AnnotationDeclaration) {
//					return;
//				}
//			} else {
//				int diff = n.getBegin().get().line - n.getEnd().get().line - 1;
//				cls.getMths().get(cls.getCurrent().get()).getLoc().addAndGet(diff);
//			}
//		} catch (Exception e) {
//			System.out.println(cls + e.getMessage());
//			throw e;
//		}
//		super.visit(n, cls);
//	}
//	
//    @Override
//    public void visit(final JavadocComment n, final Cls cls) {
//    	
//    	super.visit(n, cls);
//    }
    
	@Override
	public void visit(final ExpressionStmt n, Cls cls) {
		//printOrphanCommentsBeforeThisChildNode(n, cls);
		int diff = n.getEnd().get().line - n.getBegin().get().line;
		cls.getMths().get(cls.getCurrent().get()).getLoc().addAndGet(diff);
		super.visit(n, cls);
	}

	private void printOrphanCommentsBeforeThisChildNode(final Node node, Cls cls) {
		if (node instanceof Comment)
			return;

		Node parent = node.getParentNode().orElse(null);
		if (parent == null)
			return;
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
			if (!(everything.get(i) instanceof Comment))
				positionOfPreviousChild = i;
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