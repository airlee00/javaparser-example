package com.toms.scm.svn.core;

import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
/**
 * ex)
 * java SvnClientMain -p trunk -u 1076001 -s 20210601 -e 20210630  
 * 
 * @author 
 *
 */
public class SvnClientMain {

	public static void main(String[] args) throws Exception {

		Option pathOption = Option.builder("p").required(true).desc("svn path").longOpt("path").hasArg().build();
		Option messageOption = Option.builder("m").required(false).desc("svn commit message").longOpt("message").hasArg().build();
		Option usernameOption = Option.builder("u").required(false).desc("svn user id").longOpt("username").hasArg().build();
		Option startOption = Option.builder("s").required(true).desc("svn search start date yyyyMMdd").longOpt("start").hasArg().build();
		Option endOption = Option.builder("e").required(true).desc("svn search end date yyyyMMdd").longOpt("end").hasArg().build();
		Options options = new Options();
		options.addOption(pathOption);
		options.addOption(messageOption);
		options.addOption(usernameOption);
		options.addOption(startOption);
		options.addOption(endOption);

		String start = null;// = "20210601";
		String end = null;//= "20210630";
		String path ="trunk";
		String username="";//="1076001";
		String message="";// ="";

		CommandLineParser clp = new DefaultParser();
		try {
			CommandLine command = clp.parse(options, args);		
			if(command.hasOption("p")) {
				path = command.getOptionValue("p");
			}
			if(command.hasOption("m")) {
				message = command.getOptionValue("m");
			}
			if(command.hasOption("u")) {
				username = command.getOptionValue("u");
			}
			if(command.hasOption("s")) {
				start = command.getOptionValue("s");
			}
			if(command.hasOption("e")) {
				end = command.getOptionValue("e");
			}
			
		}catch(Exception pe) {
			System.out.println("아규면트 오류 입니다:");
			System.out.println(pe.getMessage());
			System.exit(0);
		}		
			
		long current = System.currentTimeMillis();
		System.out.print("------start-----");
		
        SVNRepository repository = RepositoryFactory.getInstance().getRepository();
        Collection<SVNLogEntry> entry =  SvnClient.searchSVN(repository, path, message, username, start, end);

        SvnLogEntrySummary.report(repository, entry, ',', null);

        System.out.println("------end-----" + (System.currentTimeMillis() - current));
    }

}