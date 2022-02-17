package com.toms.scm.svn.core;

import org.tmatesoft.svn.core.io.SVNRepository;

public interface RepositoryFactory {

    public  SVNRepository getRepository() ;

}