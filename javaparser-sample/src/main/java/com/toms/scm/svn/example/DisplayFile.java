/*
 * ====================================================================
 * Copyright (c) 2004-2011 TMate Software Ltd.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://svnkit.com/license.html
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package com.toms.scm.svn.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.joda.time.format.DateTimeFormat;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/*
 * This example shows how to fetch a file and its properties from the repository
 * at the latest (HEAD) revision . If the file is a text (either it has no
 * svn:mime-type property at all or if has and the property value is text/-like)
 * its contents as well as properties will be displayed in the console,
 * otherwise - only properties.
 * As an example here's a part of one of the
 * program layouts (for the default url and file path used in the program):
 *
 * File property: svn:entry:revision=2802
 * File property: svn:entry:checksum=435f2f0d33d12907ddb6dfd611825ec9
 * File property: svn:wc:ra_dav:version-url=/repos/svnkit/!svn/ver/2795/trunk/www/license.html
 * File property: svn:entry:last-author=alex
 * File property: svn:entry:committed-date=2006-11-13T21:34:27.908657Z
 * File property: svn:entry:committed-rev=2795
 * File contents:
 *
 * <html>
 * <head>
 * <link rel="shortcut icon" href="img/favicon.ico"/>
 * <title>SVNKit&nbsp;::&nbsp;License</title>
 * </head>
 * <body>
 * <h1>The TMate Open Source License.</h1>
 * <pre>
 * ......................................
 * ---------------------------------------------
 * Repository latest revision: 2802
 */
public class DisplayFile {


	public static Collection<SVNLogEntry> searchSVN(SVNRepository repository, String url, long startRevision, long endRevision,
			final String searchTerm, final String svnUser, java.util.Date start, java.util.Date end) throws Exception {

		// changed the config folder to avoid conflicting with anthill svn use
//		ISVNAuthenticationManager authManager = SVNWCUtil
//				.createDefaultAuthenticationManager( name,	password);
						//createDefaultAuthenticationManager(new File("/tmp"), name,	password, false);
		//Collection<SVNLogEntry> resultLogEntries = new LinkedList();
/*		Collection<SVNLogEntry> logEntries = repository.log(
				new String[] { "" }, null, startRevision, endRevision, true,
				true);*/
		final Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();

		repository.log(new String[] { "" }, startRevision, endRevision, true
				, true, 0, false, null, new ISVNLogEntryHandler() {
				//, true, 0, false, new String[] { "--search","#0012" }, new ISVNLogEntryHandler() {
		            public void handleLogEntry(SVNLogEntry logEntry) {

		            	if(logEntry == null || logEntry.getMessage() ==null)
		            		return;
			    		//if (logEntry.getMessage().indexOf(searchTerm) > -1) {
							if ((svnUser == null || svnUser.equals(""))
									|| logEntry.getAuthor().equals(svnUser)) {
							//	boolean c = checkBetween(logEntry.getDate(), start, end);
							//	if( c) {
										logEntries.add(logEntry);
							//	}
							}
						//}
		            }
		        });

		return logEntries;
	}

	public static boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
		LocalDateTime checkDateTime = dateToCheck.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		long diff1 = ChronoUnit.DAYS.between(start, checkDateTime);
		long diff2 = ChronoUnit.DAYS.between(checkDateTime, end);

		//return !dateToCheck.before (startDate) && !dateToCheck.after (endDate);
	    return diff1 >= 0 && diff2 >=0;
	}
    /*
     * args parameter is used to obtain a repository location URL, user's
     * account name & password to authenticate him to the server, the file path
     * in the rpository (the file path should be relative to the the
     * path/to/repository part of the repository location URL).
     */
    public static void main(String[] args) throws Exception {
        /*
         * Default values:
         */
		String url = "svn://localhost:3690/test_repo";
        String name = "test";
        String password = "test1234";
        //String filePath = "www/license.html";
        /*
         * Initializes the library (it must be done before ever using the
         * library itself)
         */
        setupLibrary();


        SVNRepository repository = null;
        try {
            /*
             * Creates an instance of SVNRepository to work with the repository.
             * All user's requests to the repository are relative to the
             * repository location used to create this SVNRepository.
             * SVNURL is a wrapper for URL strings that refer to repository locations.
             */
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException svne) {
            /*
             * Perhaps a malformed URL is the cause of this exception
             */
            System.err
                    .println("error while creating an SVNRepository for the location '"
                            + url + "': " + svne.getMessage());
            System.exit(1);
        }
        long latestRevision = -1;
        try {
            latestRevision = repository.getLatestRevision();
        } catch (SVNException svne) {
            System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
            System.exit(1);
        }
        //repository.get LocalDateTime ldt =DateTimeFormat.forPattern(pattern).parseLocalDateTime(sDate);
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendPattern("yyyyMMdd").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

        Date start = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDateTime("20220125").toDate();
        Date end = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDateTime("20220203").toDate();
        Collection<SVNLogEntry> entry =  DisplayFile.searchSVN(repository, "svn://localhost:3690/test_repo/trunk/hone-integration-zookeeper", 0, latestRevision, "", "test", start, end);

        entry.forEach(a -> {

        	System.out.println(a);//.getRevisionProperties());
        });
        /*
         * Gets the latest revision number of the repository
         */

        System.out.println("");
        System.out.println("---------------------------------------------");
        System.out.println("Repository latest revision: " + latestRevision);
        System.exit(0);
    }

    /*
     * Initializes the library to work with a repository via
     * different protocols.
     */
    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }
}