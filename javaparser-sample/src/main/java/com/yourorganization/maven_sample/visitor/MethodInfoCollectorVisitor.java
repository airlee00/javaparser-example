package com.yourorganization.maven_sample.visitor;

import static com.github.javaparser.utils.Utils.normalizeEolInTextBlock;
import static com.github.javaparser.utils.Utils.trimTrailingSpaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.configuration.ConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption;
import com.yourorganization.maven_sample.model.Cls;
import com.yourorganization.maven_sample.model.Lsi;
import com.yourorganization.maven_sample.model.Mth;

public class MethodInfoCollectorVisitor extends VoidVisitorAdapter<Cls> {

	@Override
	public void visit(final CompilationUnit n, Cls cls) {
		if (n.getPackageDeclaration().isPresent()) {
			cls.setPackageName(n.getPackageDeclaration().get().getNameAsString());
		}
		List<ClassOrInterfaceDeclaration> list2 = n.findAll(ClassOrInterfaceDeclaration.class);
		list2.forEach( c -> {
			c.accept(this, cls);
		});
		
		//super.visit(n, cls);
		List<MethodDeclaration> list4 = n.findAll(MethodDeclaration.class);
		list4.forEach( m -> {
			m.accept(this, cls);
		});
	}

	@Override
	public void visit(final ClassOrInterfaceDeclaration n, Cls cls) {
		cls.setClassName(n.getNameAsString());
		cls.getLoc().set(n.getEnd().get().line);
		if(n.getComment().isPresent())
			cls.setComment( ""+getComment(n.getComment().get(), cls));

		//super.visit(n, cls);
	}
    
	    public List<String> getComment(final Comment n, final Cls cls) {
	            final String commentContent = normalizeEolInTextBlock(n.getContent(), "\n");
	            String[] lines = commentContent.split("\\R");
	            List<String> strippedLines = new ArrayList<>();
	            for (String line : lines) {
	                final String trimmedLine = line.trim();
	                if (trimmedLine.contains("@author") || trimmedLine.contains("@since")) {
	                    line = trimTrailingSpaces(line);
	                    strippedLines.add(line);
	                }
	            }
	            return strippedLines;
	        }
  
	private boolean isAnonymousMethod(final Node node, Cls cls) {

		Node parent = node.getParentNode().orElse(null);
		if (parent == null) {
			return false;
		}
		else if(parent instanceof ClassOrInterfaceDeclaration) {
			return false;
		}
		return true;
	}
	
	@Override
	public void visit(MethodDeclaration md, Cls cls) {

		if( isAnonymousMethod(md, cls)) {
			return;
		}
		// if( md.getModifiers().get(0).getKeyword() == Modifier.Keyword.PUBLIC ) {
		Mth mth = new Mth();
		mth.setMethodName(md.getNameAsString());
		mth.setModifier(md.getModifiers().get(0).getKeyword().asString());
		int locTotal = md.getEnd().get().line - md.getBegin().get().line ;
		mth.getLoc().set(locTotal);
		mth.setLocTotal(locTotal);
		cls.add(mth);
		cls.getCurrent().incrementAndGet();
		// }
		if(md.getBody().isPresent())
			calcCommentsThisNode(md.getBody().get(), cls);
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

	private void calcCommentsThisNode(final Node node, Cls cls) {

		Node currentNode = node;//node.getParentNode().orElse(null);
		if (currentNode == null)
			return;
		List<Node> everything = new ArrayList<>(currentNode.getChildNodes());
		everything.forEach( nodeToPrint -> {
			if ( (nodeToPrint instanceof Comment)) {
				int diff = nodeToPrint.getBegin().get().line -  nodeToPrint.getEnd().get().line - 1;
				cls.getMths().get(cls.getCurrent().get()).getLoc().addAndGet(diff);
				//System.out.println(diff + "---------nodeto print:" + nodeToPrint);
			}else if(nodeToPrint.getComment().isPresent() ) {
				Node comment = nodeToPrint.getComment().get();
				int diff = comment.getBegin().get().line -  comment.getEnd().get().line - 1;
				cls.getMths().get(cls.getCurrent().get()).getLoc().addAndGet(diff);
				//System.out.println(diff + "---------nodeToPrint.getComment()print:" + nodeToPrint.getComment());
			}else if( nodeToPrint instanceof Statement) {
				calcCommentsThisNode(((Statement)nodeToPrint), cls);
			}
		});
	}
}