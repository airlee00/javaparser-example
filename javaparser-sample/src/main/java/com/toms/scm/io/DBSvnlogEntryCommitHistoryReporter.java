package com.toms.scm.io;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.model.CommitHistory;

public class DBSvnlogEntryCommitHistoryReporter implements CommitHistoryReporter {

	@Override
	public List<CommitHistory> report(SVNRepository repository, Collection<SVNLogEntry> entry, char delimiter,
			OutputStream output) {
		// TODO Auto-generated method stub
		return null;
	}

}
