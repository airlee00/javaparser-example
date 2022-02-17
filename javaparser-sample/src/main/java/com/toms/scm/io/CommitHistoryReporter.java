package com.toms.scm.io;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.model.ChangedPath;
import com.toms.scm.model.CommitHistory;

public interface CommitHistoryReporter {

	//public CommitHistory report(SVNLogEntry logEntry) ;

	//public  ChangedPath report(SVNRepository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) ;

	public  List<CommitHistory> report(SVNRepository repository,Collection<SVNLogEntry> entry, char delimiter, OutputStream output) ;

}