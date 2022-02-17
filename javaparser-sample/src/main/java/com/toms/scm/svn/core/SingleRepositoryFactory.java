package com.toms.scm.svn.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SingleRepositoryFactory implements RepositoryFactory{

    private static SingleRepositoryFactory factory =  new SingleRepositoryFactory();

    public static SingleRepositoryFactory getInstance() {
        return factory;
    }

    private SVNRepository repository = null;


    //private construct
    private SingleRepositoryFactory() {

    	String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    	String appConfigPath = rootPath + "svn.properties";

    	Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(appConfigPath));
		} catch (IOException e) {
            System.err
            .println("error while creating an SVNRepository for the location '"
                    + appConfigPath + "': " + e.getMessage());
            System.exit(1);
		}
    	String url =  props.getProperty("svn.url");//"svn://localhost:3690/test_repo";
    	String name = props.getProperty("svn.username");//"test";
    	String password =props.getProperty("svn.password");// "test1234";
        /*
         * Initializes the library (it must be done before ever using the
         * library itself)
         */
        setupLibrary();


        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException svne) {
            System.err
                    .println("error while creating an SVNRepository for the location '"
                            + url + "': " + svne.getMessage());
            System.exit(1);
        }

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password.toCharArray());
        repository.setAuthenticationManager(authManager);

    }

    @Override
    public  SVNRepository getRepository() {
		return repository;
	}


	/*
     * Initializes the library to work with a repository via
     * different protocols.
     */
    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }
}