package com.toms.scm.svn.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.joda.time.format.DateTimeFormat;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.model.ChangedPath;
import com.toms.scm.model.CommitHistory;
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

	public static CommitHistory print(SVNLogEntry logEntry) {
		System.out.println(String.format("%5s %2s %10s %2s %10s %10s %16s", logEntry.getRevision(), "|",
				logEntry.getAuthor(), "|", formatDate(logEntry.getDate()), "", logEntry.getMessage()));
		
		CommitHistory history = new CommitHistory();
		history.setDate(logEntry.getDate());
		history.setMessage(logEntry.getMessage());
		history.setRevision(logEntry.getRevision());
		return history;
	}

	public static ChangedPath print(SVNRepository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) {
		if (entryPath.getKind() == SVNNodeKind.FILE) {
			DiffResult dr = SvnClient.diff(repository, entryPath.getPath(), logEntry.getRevision());
			String changePaths = String.format("%33s %10s %10d %2s %40s"," ", "|", dr.getLine(), "|", entryPath.getType() + " " + entryPath.getPath() ) ;
			System.out.println(changePaths) ;
			ChangedPath path = new ChangedPath();
			path.setDiffResult(dr);
			path.setPath(entryPath.getPath());
			path.setType(entryPath.getType());
			return path;
		}
		return null;
	}

	public static List<CommitHistory> report(SVNRepository repository,Collection<SVNLogEntry> entry, char delimiter, OutputStream output) {
		if (output == null) {
			output = new ByteArrayOutputStream();
		}

		printHeader();

		List<CommitHistory> historys = new LinkedList<>();
		for (Iterator entries = entry.iterator(); entries.hasNext();) {
			/*
			 * gets a next SVNLogEntry
			 */
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();

			CommitHistory history = print(logEntry);
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
					ChangedPath path  = print(repository, logEntry, entryPath);
					history.getChangePaths().add(path);
					historys.add(history);
				}
			}
		}
		return historys;
	}

}