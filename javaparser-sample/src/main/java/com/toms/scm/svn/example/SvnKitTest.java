package com.toms.scm.svn.example;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SvnKitTest {

	public static void main(String[] args) throws Exception {
		printSVNChangeLog();
	}

	public static void checkout() throws Exception {

	}
	public static void printSVNChangeLog() throws Exception {

		String url = "svn://localhost:3690/test_repo";
		String svnUser = "test";
		String svnPassword = "test1234";

		SVNURL svnUrl = SVNURL.parseURIDecoded(url);
		SVNRepository svnRepo = SVNRepositoryFactory.create(svnUrl);

		BasicAuthenticationManager authManager = new
				BasicAuthenticationManager(svnUser, svnPassword);
		svnRepo.setAuthenticationManager(authManager);

		long latestRevision = svnRepo.getLatestRevision();
		System.out.println("latestRevision : " + latestRevision);

		long startRevision = latestRevision;
		long endRevision = latestRevision;

		Collection<SVNLogEntry> logEntries = null;
		logEntries = svnRepo.log(new String[] { "" }, null, startRevision, endRevision, true, true);

		Iterator entries = logEntries.iterator();
		while (entries.hasNext()) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			if (logEntry == null) {
				continue;
			}

			System.out.println("---------------------------------------------");
			System.out.println("revision: " + logEntry.getRevision());
			System.out.println("author: " + logEntry.getAuthor());
			System.out.println("date: " + logEntry.getDate());
			System.out.println("log message: " + logEntry.getMessage());

			if (logEntry.getChangedPaths() == null || logEntry.getChangedPaths().size() == 0) {
				continue;
			}

			System.out.println();
			System.out.println("changed paths:");
			Set changedPathsSet = logEntry.getChangedPaths().keySet();
			Iterator changedPaths = changedPathsSet.iterator();
			while (changedPaths.hasNext()) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

				if (entryPath.getCopyPath() != null) {
					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath() + "(from "
							+ entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")");
				} else {
					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath());
				}
			}
		}
	}
}
