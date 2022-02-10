package com.toms.scm.svn.core;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnClientTest {

	public static void main(String[] args) throws Exception {

        SVNRepository repository = RepositoryFactory.getInstance().getRepository();

        String start = "20220101";//airlee00@gmail.com
        String end = "20220203";//rnekswlhw1!

        Collection<SVNLogEntry> entry =  SvnClient.searchSVN(repository, "trunk/hone-integration-zookeeper", "", "test", start, end);

        SvnLogEntrySummary.report(repository, entry, ',', null);


        System.exit(0);
    }

}