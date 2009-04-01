/*
 * Copyright (C) 2006 TopCoder Inc., All Rights Reserved.
 */
package com.cronos.onlinereview.project.admin.functionaltests;

import junit.framework.Assert;
import com.cronos.onlinereview.project.UserSimulator;
import com.cronos.onlinereview.project.AbstractTestCase;
import com.cronos.onlinereview.project.Project;
import com.cronos.onlinereview.project.Messages;

import java.util.Map;
import java.util.Iterator;
import java.io.InputStream;

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.IDataSet;

/**
 * <p>A test case for <code>Download Final Fix</code> Use Case.</p>
 *
 * @author  TCSDEVELOPER
 * @version 1.0
 */
public class DownloadFinalFixFunctionalTest extends AbstractTestCase {

    /**
     * <p>A <code>String</code> providing the name of the XML file providing the initial test data to be inserted into
     * database tables prior to executing any test. This file provides the data for all active projects assigned to
     * users.</p>
     */
    public static final String PROJECTS_TEST_DATA_FILE_NAME = "data/initial/DataSet.xml";

    /**
     * <p>A <code>String</code> array providing the usernames for users who are not granted the permission to download
     * the final fix for selected project.</p>
     */
    public static final String[] UNAUTHORIZED_USERS
        = new String[] {UserSimulator.DESIGNER, UserSimulator.SECOND_PLACE_SUBMITTER,
                        UserSimulator.THIRD_PLACE_SUBMITTER, UserSimulator.REVIEWER1, UserSimulator.REVIEWER2};

    /**
     * <p>A <code>String</code> array providing the usernames for users who are not granted the permission to download
     * the final fix for selected project.</p>
     */
    public static final String[] AUTHORIZED_USERS
        = new String[] {UserSimulator.PRIMARY_REVIEWER, UserSimulator.WINNING_SUBMITTER,
                        UserSimulator.APPROVER, UserSimulator.MANAGER, UserSimulator.OBSERVER};

    /**
     * <p>Scenario #142(FTC</p>
     * <pre>
     * 1.  User clicks on "All Open Projects" tab
     * 2.  User views project list and selects a project
     * 3.  User clicks on "Final Fix" tab
     * </pre>
     * <p> <b>Expected Outcome:</b><br/>
     *
     * Validation Error is shown indicating User does NOT have permission to download selected file. </p>
     */
    public void testScenario142() throws Exception {
        Map projects = Project.loadAllProjects(getActiveProjectsData());
        for (int i = 0; i < UNAUTHORIZED_USERS.length; i++) {
            setUser(UNAUTHORIZED_USERS[i]);
            Iterator iterator = projects.entrySet().iterator();
            Map.Entry entry;
            while (iterator.hasNext()) {
                entry = (Map.Entry) iterator.next();
                Project project = (Project) entry.getValue();
                this.user.openFinalFixReviewTab(project.getName() + " version " + project.getVersion());
                this.user.downloadFinalFix();
                assertDisplayedMessage(Messages.getNoPermissionDownloadFinalFix());
            }
        }
    }

    /**
     * <p>Scenario #143</p>
     * <pre>
     * Note: User is logged-in as one of the following: Manager, Observer, Submitter, Final
     * Reviewer and Approver
     * 1.  User clicks on "All Open Projects" tab
     * 2.  User views project list and selects a project
     * 3.  User clicks on "Download"
     * </pre>
     * <p> <b>Expected Outcome:</b><br/>
     *
     * Final Fix is downloaded to users' machine. </p>
     */
    public void testScenario143() throws Exception {
        Map projects = Project.loadAllProjects(getActiveProjectsData());
        for (int i = 0; i < AUTHORIZED_USERS.length; i++) {
            setUser(AUTHORIZED_USERS[i]);
            Iterator iterator = projects.entrySet().iterator();
            Map.Entry entry;
            while (iterator.hasNext()) {
                entry = (Map.Entry) iterator.next();
                Project project = (Project) entry.getValue();
                this.user.openFinalFixReviewTab(project.getName() + " version " + project.getVersion());
                String contentType = this.user.downloadFinalFix();
                Assert.assertEquals("The final fix is not downloaded correctly",
                                    "application/x-java-archive", contentType);
            }
        }
    }

    /**
     * <p>Gets the data sets specific to test case which must be used to populate the database tables with initial data.
     * </p>
     *
     * @return an <code>IDataSet</code> array providing the data sets specific to test case.
     * @throws Exception if an unexpected error occurs.
     */
    protected IDataSet[] getDataSets() throws Exception {
        return new IDataSet[] {getActiveProjectsData()};
    }

    /**
     * <p>Gets the data set providing the initial data for active projects.</p>
     *
     * @return an <code>IDataSet</code> providing details for active projects.
     * @throws Exception if an unexpected error occurs.
     */
    protected static IDataSet getActiveProjectsData() throws Exception {
        InputStream projectDataStream
            = ViewMyProjectFunctionalTest.class.getClassLoader().getResourceAsStream(PROJECTS_TEST_DATA_FILE_NAME);
        return new FlatXmlDataSet(projectDataStream);
    }
}