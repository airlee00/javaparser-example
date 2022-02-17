package com.toms.scm.svn.core;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import org.joda.time.format.DateTimeFormat;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.toms.scm.model.DiffResult;

public class SvnClient {

    private static String YYYYMMDD = "yyyyMMdd";

	public static Collection<SVNLogEntry> searchSVN(SVNRepository repository, String url, long startRevision, long endRevision,
			final String searchTerm, final String svnUser) throws Exception {

		final ArrayList<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

		repository.log(new String[] { url }, startRevision, endRevision, true, true, 0, false, null, new ISVNLogEntryHandler() {
		           public void handleLogEntry(SVNLogEntry logEntry) {
		            	if(logEntry == null)
		            		return;
		            	//if (logEntry.getMessage().indexOf(searchTerm) > -1) {
		            	   //commit message에서 찾고자 할때
		            	//}
						if ( (svnUser == null || svnUser.equals(""))
								|| svnUser.equals(logEntry.getAuthor())) {
							//boolean check = checkBetween(logEntry.getDate(), start, end);
							//boolean check2 = logEntry.getDate().after(end);
							//if( check ) { // && !check2 ) {
								logEntries.add(logEntry);
							//}
						}
		            }
		        });

		Collections.reverse(logEntries);

		return logEntries;
	}

	public static Collection<SVNLogEntry> searchSVN(SVNRepository repository, String url,
			final String searchTerm, final String svnUser,String start, String end) throws Exception {
		//DateTimeFormatter formatter =
        Date startDate = DateTimeFormat.forPattern(YYYYMMDD).parseLocalDateTime(start).plusHours(24).toDate();
        Date endDate = DateTimeFormat.forPattern(YYYYMMDD).parseLocalDateTime(end).plusHours(24).toDate();
        long s = repository.getDatedRevision(startDate);
        long e = repository.getDatedRevision(endDate);
        System.out.println(s +"~" + e);
        return searchSVN(repository, url, s,  e, searchTerm, svnUser);
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

	public static DiffResult diff(SVNRepository repository,
			  String path,
			  long srcRevision) {

		try {
			//long lastVersion = repository.getLatestRevision();
			SVNLogEntry preLog =  getPreviousLogEntry(repository, srcRevision, 0);
		    if(preLog != null)
		    	return runDiff(repository, path, srcRevision, preLog.getRevision());
		    else
		    	return runDiff(repository, path, srcRevision, repository.getLatestRevision() );
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new DiffResult();
	}


    private static DiffResult runDiff(SVNRepository repository, String path, long srcRevision, long previousRevision) throws SVNException  {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();

        SVNURL srcUrl = SVNURL.parseURIEncoded(repository.getLocation().toString() + "/" + path);

		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        final SvnDiff diff = svnOperationFactory.createDiff();
        diff.setSource(SvnTarget.fromURL(srcUrl, SVNRevision.create(srcRevision)), SVNRevision.create(previousRevision), SVNRevision.create(srcRevision));//SvnTarget.fromURL(url2, svnRevision2));
        diff.setOutput(byteArrayOutputStream);
        diff.setDiffGenerator(diffGenerator);
        diff.run();
        String data = new String(byteArrayOutputStream.toByteArray()).replace(System.getProperty("line.separator"), "\n");
        int lineCount = data.split("\n").length - 5; //index 부분을 제외
        return new DiffResult(data, lineCount);
    }

	// 다건
	public static Collection<SVNLogEntry> getPreviousLogEntrys(SVNRepository repository, final long startRevision,
			final long endRevision, long limit) throws Exception {

		final LinkedList<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "" }, startRevision, endRevision, true, true, limit, false, null,
				new ISVNLogEntryHandler() {
					public void handleLogEntry(SVNLogEntry logEntry) {
						if (logEntry == null)
							return;
						if (logEntry.getRevision() != startRevision) {
							logEntries.add(logEntry);
						}
					}
				});
		return logEntries;
	}

	// 단건
	public static SVNLogEntry getPreviousLogEntry(SVNRepository repository, final long startRevision,
			final long endRevision) throws Exception {

		Collection<SVNLogEntry> logEntries = getPreviousLogEntrys(repository, startRevision, endRevision, 2);
		if (logEntries.size() > 0) {
			for (SVNLogEntry entry : logEntries) {
				return entry;
			}
		}
		return null;
	}
}