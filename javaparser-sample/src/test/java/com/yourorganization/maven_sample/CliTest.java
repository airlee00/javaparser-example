package com.yourorganization.maven_sample;

import java.text.ParseException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
/**
 * ex)
 * java SvnClientMain trunk  
 * 
 * @author 
 *
 */
public class CliTest {

	public static void main(String[] args) throws Exception {
		CommandLine command ;
		Option pathOption = Option.builder("p").required(true).desc("svn path").longOpt("path").hasArg().build();
		Options options = new Options();
		options.addOption(pathOption);
		
		CommandLineParser clp = new DefaultParser();
		try {
			command = clp.parse(options, args);
			
			if(command.hasOption("path")) {
				System.out.println(command.getOptionValue("path"));
			}
			
			if(command.hasOption("p")) {
				System.out.println(command.getOptionValue("p"));
			}
			
			
				System.out.println("--------");
			
		}catch(Exception pe) {
			System.out.println("Parse error:");
			System.out.println(pe.getMessage());
		}
		
    }

}