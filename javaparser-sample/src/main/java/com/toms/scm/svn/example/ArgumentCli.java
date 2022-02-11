package com.toms.scm.svn.example;

	import org.apache.commons.cli.CommandLine;
	    import org.apache.commons.cli.Option;
	    import org.apache.commons.cli.Options;
	    import org.apache.commons.cli.Option.Builder;
	    import org.apache.commons.cli.CommandLineParser;
	    import org.apache.commons.cli.DefaultParser;
	    import org.apache.commons.cli.ParseException;
	    public class ArgumentCli {

	    public static void main(String[] parameters)
	    {
	        CommandLine commandLine;
	        Option option_A = Option.builder("A")
	            .required(true)
	            .desc("The A option")
	            .longOpt("opt3").hasArg()
	            .build();
	        Option option_r = Option.builder("r")
	            .required(true)
	            .desc("The r option")
	            .longOpt("opt1")
	            .build();
	        Option option_S = Option.builder("S")
	            .required(true)
	            .desc("The S option")
	            .longOpt("opt2")
	            .build();
	        Option option_test = Option.builder()
	            .required(true)
	            .desc("The test option")
	            .longOpt("test")
	            .build();
	        Options options = new Options();
	        CommandLineParser parser = new DefaultParser();

	        String[] testArgs =
	        { "-r", "opt1", "-S", "opt2", "arg1", "arg2",
	          "arg3", "arg4", "--test", "-A", "opt3", };

	        options.addOption(option_A);
	        options.addOption(option_r);
	        options.addOption(option_S);
	        options.addOption(option_test);

	        try
	        {
	            commandLine = parser.parse(options, testArgs);

	            if (commandLine.hasOption("A"))
	            {
	                System.out.print("Option A is present.  The value is: ");
	                System.out.println(commandLine.getOptionValue("A"));
	            }

	            if (commandLine.hasOption("r"))
	            {
	                System.out.print("Option r is present.  The value is: ");
	                System.out.println(commandLine.getOptionValue("r"));
	            }

	            if (commandLine.hasOption("S"))
	            {
	                System.out.print("Option S is present.  The value is: ");
	                System.out.println(commandLine.getOptionValue("S"));
	            }

	            if (commandLine.hasOption("test"))
	            {
	                System.out.println("Option test is present.  This is a flag option.");
	            }

	            {
	                String[] remainder = commandLine.getArgs();
	                System.out.print("Remaining arguments: ");
	                for (String argument : remainder)
	                {
	                    System.out.print(argument);
	                    System.out.print(" ");
	                }

	                System.out.println();
	            }

	        }
	        catch (ParseException exception)
	        {
	            System.out.print("Parse error: ");
	            System.out.println(exception.getMessage());
	        }
	    }

}

