package com.toms.scm.io;

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
import com.toms.scm.svn.core.SvnClient;

public class DefaultSvnlogEntryCommitHistoryReporter implements CommitHistoryReporter {

	public static String formatDate(java.util.Date d) {
		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(d.getTime());
	}

	public CommitHistory report(SVNLogEntry logEntry) {
		CommitHistory history = new CommitHistory();
		history.setDate(formatDate(logEntry.getDate()));
		history.setMessage(logEntry.getMessage());
		history.setRevision(logEntry.getRevision());
		history.setAuthor(logEntry.getAuthor());
		return history;
	}

	public ChangedPath report(SVNRepository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) {
		if (entryPath.getKind() == SVNNodeKind.FILE) {
			DiffResult dr = SvnClient.diff(repository, entryPath.getPath(), logEntry.getRevision());
			ChangedPath path = new ChangedPath();
			path.setDiffResult(dr);
			path.setPath(entryPath.getPath() == null?"":entryPath.getPath());
			path.setType(entryPath.getType());
			return path;
		}
		return null;
	}

	@Override
	public List<CommitHistory> report(SVNRepository repository, Collection<SVNLogEntry> entry, char delimiter,
			OutputStream output) {
		if (output == null) {
			output = new ByteArrayOutputStream();
		}

		List<CommitHistory> historys = new LinkedList<>();
		for (Iterator entries = entry.iterator(); entries.hasNext();) {
			/*
			 * gets a next SVNLogEntry
			 */
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();

			CommitHistory history = report(logEntry);
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
					ChangedPath path  = report(repository, logEntry, entryPath);
					if(path != null) {
						history.getChangePaths().add(path);
						historys.add(history);
					}
				}
			}
		}
		return historys;
	}

}