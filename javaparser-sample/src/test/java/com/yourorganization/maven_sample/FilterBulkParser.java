package com.yourorganization.maven_sample;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.utils.SourceRoot;
import com.yourorganization.maven_sample.model.Cls;
import com.yourorganization.maven_sample.visitor.MethodInfoCollectorVisitor;

/**
 * Some code that uses JavaParser.
 */
public class FilterBulkParser {

    public static void main(String[] args) throws IOException {

    	//String rootPath = "C:\\hlicp_ide\\workspace-temp5\\hlicp-ini-olt/src/main/java";
    	String rootPath = "C:\\hlicp_ide\\workspace-temp2\\hlicp-svp-olt/src/main/java";
    	Path projectRoot = FileSystems.getDefault().getPath(rootPath);
    	
    	SourceRoot sourceRoot = new SourceRoot(projectRoot.resolve(""));

    	List<String> files = Files.walk(Paths.get(rootPath))
    			.filter(Files::isRegularFile)
    			.filter(path ->path.toString().endsWith(".java") )
    			//.filter(path ->path.toString().endsWith("CSC.java") )
    			//.filter(path ->path.toString().endsWith("LSC.java") )
    			//.filter(path ->path.toString().endsWith("CSC.java") )
    			.filter(path -> !path.toString().endsWith("DTO.java") )
    			.filter(path -> !path.toString().endsWith("DTO2.java") )
    			.filter(path -> !path.toString().endsWith("I.java") )
    			.map(Path::toAbsolutePath)
    			.map(Path::toString)
    			.collect(Collectors.toList())
    			;
    			
    			
        // Our sample is in the root of this directory, so no package name.
        ParserConfiguration conf = new ParserConfiguration();//.setLanguageLevel(JAVA_9)
        conf.setLanguageLevel(LanguageLevel.JAVA_8);
        files.forEach( f -> {
        	CompilationUnit cu = sourceRoot.parse("", f) ;
        	Cls cls = new Cls();
        	VoidVisitor<Cls> methodNameCollector = new MethodInfoCollectorVisitor();
        	methodNameCollector.visit(cu, cls);
        	System.out.println(cls);
        	
        });

    }

}


