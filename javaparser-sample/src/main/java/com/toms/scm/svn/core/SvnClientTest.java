package com.toms.scm.svn.core;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnClientTest {

	public static void main(String[] args) throws Exception {

        SVNRepository repository = RepositoryFactory.getInstance().getRepository();
        long latestRevision = -1;
        try {
            latestRevision = repository.getLatestRevision();
        } catch (SVNException svne) {
            System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
            System.exit(1);
        }
        String start = "20220101";
        String end = "20220203";

        Collection<SVNLogEntry> entry =  SvnClient.searchSVN(repository, "svn://localhost:3690/test_repo/trunk/hone-integration-zookeeper", 0, latestRevision, "", "test", start, end);

        SvnLogEntrySummary.report(repository, entry, ',', null);


        System.exit(0);
    }

}