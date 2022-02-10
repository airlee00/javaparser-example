package com.toms.scm.svn.example;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.toms.scm.svn.example.svnserver.ExportSVNEditor;
import com.toms.scm.svn.example.svnserver.FilterSVNEditor;

public class DiffTest {

	public static void main(String[] args) throws Exception {
		printSVNChangeLog();
	}
	  private static void compareRevision(@NotNull SVNRepository srcRepo, long srcRev) throws SVNException {
		    final ExportSVNEditor srcExport = new ExportSVNEditor(true);
		    srcRepo.diff(srcRepo.getLocation(), srcRev , srcRev - 1, null, false, SVNDepth.INFINITY, true, reporter -> {
		      //reporter.setPath("", null, 0, SVNDepth.INFINITY, true);
		      reporter.setPath("", null, srcRev, SVNDepth.INFINITY.IMMEDIATES, true);
		      reporter.finishReport();
		   // }, new FilterSVNEditor(srcExport, true));
		      }, srcExport);

System.out.println(srcExport.toString());
		   // Assert.assertEquals(srcExport.toString(), dstExport.toString());
		  }
	public static void printSVNChangeLog() throws Exception {

		String url = "svn://localhost:3690/test_repo";
		String name = "test";
		String password = "test1234";


		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));


        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);


		long latestRevision = repository.getLatestRevision();
		System.out.println("latestRevision : " + latestRevision);

		long startRevision = latestRevision;
		long endRevision = latestRevision;

		Collection<SVNLogEntry> logEntries = null;
		//logEntries = repository.diff(url, targetRevision, revision, target, ignoreAncestry, depth, getContents, reporter, editor);

		compareRevision(repository, 4);
//
//		Iterator entries = logEntries.iterator();
//		while (entries.hasNext()) {
//			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
//			if (logEntry == null) {
//				continue;
//			}
//
//			System.out.println("---------------------------------------------");
//			System.out.println("revision: " + logEntry.getRevision());
//			System.out.println("author: " + logEntry.getAuthor());
//			System.out.println("date: " + logEntry.getDate());
//			System.out.println("log message: " + logEntry.getMessage());
//
//			if (logEntry.getChangedPaths() == null || logEntry.getChangedPaths().size() == 0) {
//				continue;
//			}
//
//			System.out.println();
//			System.out.println("changed paths:");
//			Set changedPathsSet = logEntry.getChangedPaths().keySet();
//			Iterator changedPaths = changedPathsSet.iterator();
//			while (changedPaths.hasNext()) {
//				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
//
//				if (entryPath.getCopyPath() != null) {
//					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath() + "(from "
//							+ entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")");
//				} else {
//					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath());
//				}
//			}
//		}
	}
}
