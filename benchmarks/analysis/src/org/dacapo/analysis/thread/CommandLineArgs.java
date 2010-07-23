package org.dacapo.analysis.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineArgs {
	
	public static final String DEFAULT_CLASSPATH  = "store";
	public static final String DEFAULT_LOG_FILE   = "log_file";
	
	public static final String OPT_LOG_FILE       = "logfile";
	public static final String OPT_HELP           = "help";
	public static final String OPT_OUTPUT_FILE    = "output";
	public static final String OPT_LOCK_LOG_FILE  = "locklogfile";
	
	public static final String STDOUT_NAME        = "-";

	public static final int EXIT_BAD_COMMANDLINE  = 1;
	
	private static final Option[] OPTIONS = {
		makeOption("f", OPT_LOG_FILE, "specify the log file", "log_file"),
		makeOption("l", OPT_LOCK_LOG_FILE, "specify the lock log file", "lock_log_file"),
		makeOption("o", OPT_OUTPUT_FILE, "output CSV file for metrics", "output"),
		makeOption("h", OPT_HELP, "help", null)
	};
	
	private static CommandLineParser parser = new PosixParser();
	private static Options options = new Options();
	private static Options visibleOptions = new Options();
	private static OutputStream os = null;
	
	{
		// Construct the option list and the visibleOption list.
		// The option list is used for parsing the command line,
		// where as the visibleOption is a subset of the option list
		// and is used for producing the usage help.
		for (int i = 0; i < OPTIONS.length; i++) {
			options.addOption(OPTIONS[i]);
			if (OPTIONS[i].getDescription() != null)
				visibleOptions.addOption(OPTIONS[i]);
		}
	}

	private CommandLine line;
	private LinkedList<String> classpath = null;

	CommandLineArgs(String[] args) throws Exception {
		try {
			boolean reportAndExitOk = false;
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Command line exception: " + e.getMessage());
			System.exit(EXIT_BAD_COMMANDLINE);
		} catch (Exception e) {
			System.err.println("Exception processing command line values: "
					+ e.getMessage());
			System.exit(EXIT_BAD_COMMANDLINE);
		}
	}
	
	String getLogFile() {
		return line.getOptionValue(OPT_LOG_FILE,DEFAULT_LOG_FILE);
	}
	
	String getLockLogFile() {
		return line.getOptionValue(OPT_LOCK_LOG_FILE,null);
	}
	
	OutputStream getOutputStream() throws FileNotFoundException {
		if (os != null)
			return os;
		
		String fileName = line.getOptionValue(OPT_OUTPUT_FILE,STDOUT_NAME);
		
		if (fileName.equals(STDOUT_NAME)) {
			os = System.out;
		} else { 
			if (fileName.startsWith("-")) fileName = fileName.substring(1);
			os = new FileOutputStream(new File(fileName));
		}
		return os;
	}
	
	/*
	 * Define a commandline option.
	 * 
	 * @param shortName An optional short form name for the command line option.
	 * 
	 * @param longname A commandline option must have a long form name.
	 * 
	 * @param description All commandline options that are visible options must
	 * have a description, commandline options that are for internal development
	 * usage must not have a description and must instead be documented in the
	 * code.
	 * 
	 * @param argName A commandline option that requires has an argument must
	 * specify an argument name.
	 */
	private static Option makeOption(String shortName, String longName,
			String description, String argName) {
		assert longName != null;

		Option option = new Option(shortName, longName, argName != null,
				description);

		if (argName != null) {
			option.setValueSeparator('=');
			option.setArgName(argName);
		}

		return option;
	}

}
