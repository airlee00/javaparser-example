package com.toms.scm.svn.core;

import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.io.CommitHistoryReporter;
import com.toms.scm.io.ConsoleSvnlogEntryCommitHistoryReporter;
import com.toms.scm.model.CommitHistory;

public class SvnClientTest {

	public static void main(String[] args) throws Exception {

        SVNRepository repository = SingleRepositoryFactory.getInstance().getRepository();

        String start = "20220214";//airlee00@gmail.com
        String end = "20220214";//rnekswlhw1!
        System.out.println("start--");
        long elsp = System.currentTimeMillis();
        Collection<SVNLogEntry> entry =  SvnClient.searchSVN(repository, "trunk/hone-integration-zookeeper", "", "", start, end);

        CommitHistoryReporter reporter = new ConsoleSvnlogEntryCommitHistoryReporter();
        @SuppressWarnings("unused")
		List<CommitHistory> historys = reporter.report(repository, entry, ',', null);

        System.out.println("--end (" + (System.currentTimeMillis() - elsp ) + ")");
        System.exit(0);
    }

}