package com.toms.scm.svn.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.joda.time.format.DateTimeFormat;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.model.DiffResult;

public class SvnLogEntrySummary {

	public static String formatDate(java.util.Date d) {
		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(d.getTime());
	}

	public static void printHeader() {
		System.out.println(String.format("%5s %2s %10s %2s %10s %10s %10s %2s %10s", "rev", "|", "author", "|", "date", "|","line", "|",
				"changed paths"));
		System.out.println(String.format("%s",
				"----------------------------------------------------------------------------------------------------------------------------------------------"));
	}

	public static void print(SVNLogEntry logEntry) {
		System.out.println(String.format("%5s %2s %10s %2s %10s %10s %16s", logEntry.getRevision(), "|",
				logEntry.getAuthor(), "|", formatDate(logEntry.getDate()), "", logEntry.getMessage()));
	}

	public static void print(SVNRepository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) {
		if (entryPath.getKind() == SVNNodeKind.FILE) {
			DiffResult dr = SvnClient.diff(repository, entryPath.getPath(), logEntry.getRevision());
			String a = String.format("%33s %10s %10d %2s %40s"," ", "|", dr.getLine(), "|", entryPath.getType() + " " + entryPath.getPath() ) ;
			System.out.println( a) ;
			//System.out.println(dr.getContents());
		}
	}

	public static void report(SVNRepository repository,Collection<SVNLogEntry> entry, char delimiter, OutputStream output) {
		if (output == null) {
			output = new ByteArrayOutputStream();
		}

		printHeader();

		for (Iterator entries = entry.iterator(); entries.hasNext();) {
			/*
			 * gets a next SVNLogEntry
			 */
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();

			print(logEntry);
			/*
			 * displaying all paths that were changed in that revision; cahnged path
			 * information is represented by SVNLogEntryPath.
			 */
			if (logEntry.getChangedPaths().size() > 0) {
				/*
				 * keys are changed paths
				 */
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
					/*
					 * obtains a next SVNLogEntryPath
					 */
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					//if( entryPath.getPath().contains("StateChangeCuratorListener")) {
						print(repository, logEntry, entryPath);
					//}

				}
			}
		}
	}

}