/**
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.onlinereview.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.onlinereview.migration.dto.oldschema.scorecard.QuestionTemplate;
import com.topcoder.onlinereview.migration.dto.oldschema.scorecard.ScSectionGroup;
import com.topcoder.onlinereview.migration.dto.oldschema.scorecard.ScorecardSectionOld;
import com.topcoder.onlinereview.migration.dto.oldschema.scorecard.ScorecardTemplate;
import com.topcoder.util.log.Level;
import com.topcoder.util.log.LogFactory;

/**
 * The test of Loader.
 *
 * @author brain_cn
 * @version 1.0
 */
public class ScorecardLoader {
	private Connection conn = null;

    /**
     * Creates a new Loader object.
     *
     * @param conn the connection to persist data
     */
	public ScorecardLoader(Connection conn) {
		this.conn = conn;
	}

    /**
     * Load ScorecardSection data to transform.
     *
     * @param templateId the scorecard template id
     * @param groupId the scorecard group id
     * @return the loaded ScorecardSection data
     *
     * @throws SQLException if error occurs while execute sql statement
     */
    private List loadScorecardSection(int templateId, int groupId) throws SQLException {
		Util.start("loadScorecardSection");
        // load ScorecardSection table from old online review
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + ScorecardSectionOld.TABLE_NAME +
        		" WHERE template_id = ? AND group_id = ?");
        stmt.setInt(1, templateId);
        stmt.setInt(2, groupId);
        ResultSet rs = stmt.executeQuery();

        List list = new ArrayList();

        while (rs.next()) {
        	ScorecardSectionOld table = new ScorecardSectionOld();
            table.setSectionId(rs.getInt(ScorecardSectionOld.SECTION_ID_NAME));
            table.setSectionName(rs.getString(ScorecardSectionOld.SECTION_NAME_NAME));
            table.setSectionWeight(rs.getInt(ScorecardSectionOld.SECTION_WEIGHT_NAME));
            table.setSectionSeqLoc(rs.getInt(ScorecardSectionOld.SECTION_SEQ_LOC_NAME));
            table.setQuestions(this.loadQuestionTemplate(templateId, table.getSectionId()));
            list.add(table);
        }

        Util.logAction(list.size(), "loadScorecardSection");
        DatabaseUtils.closeResultSetSilently(rs);
        DatabaseUtils.closeStatementSilently(stmt);

        return list;
    }

    /**
     * Load scorecard data to transform.
     *
     * @return the loaded scorecard data
     *
     * @throws SQLException if error occurs while execute sql statement
     */
    public List loadScorecardTemplate() throws SQLException {
		Util.start("loadScorecardTemplate");

        // load scorecard template table from old online review
        Statement stmt = conn.createStatement();
        if (DatabaseUtils.IS_TEST) {
        	// For test purpose, just fetch 10
            stmt.setFetchSize(10);
        }
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + ScorecardTemplate.TABLE_NAME + " where order by template_id");
        List list = new ArrayList();

        int size = 0;
        while (rs.next()) {
            if (DatabaseUtils.IS_TEST) {
	        	if (size++ > 1) {
	        		LogFactory.getLog().log(Level.DEBUG, "break since it's debug");
	        		break;
	        	}
            }
            ScorecardTemplate table = new ScorecardTemplate();
            table.setTemplateId(rs.getInt(ScorecardTemplate.TEMPLATE_ID_NAME));
            table.setProjectType(rs.getInt(ScorecardTemplate.PROJECT_TYPE_NAME));
            table.setScorecardType(rs.getInt(ScorecardTemplate.SCORECARD_TYPE_NAME));
            table.setStatusId(rs.getInt(ScorecardTemplate.STATUS_ID_NAME));
            table.setTemplateName(rs.getString(ScorecardTemplate.TEMPLATE_NAME_NAME));
            list.add(table);
            table.setGroups(this.loadScSectionGroup(table.getTemplateId()));
        }

        Util.logAction(list.size(), "ScorecardTemplate");
        DatabaseUtils.closeResultSetSilently(rs);
        DatabaseUtils.closeStatementSilently(stmt);

        return list;
    }

    /**
     * Load ScSectionGroup data to transform.
     *
     * @param templateId the scorecard template id
     * @return the loaded ScSectionGroup data
     *
     * @throws SQLException if error occurs while execute sql statement
     */
    private List loadScSectionGroup(int templateId) throws SQLException {
		Util.start("loadScSectionGroup");
        // load ScSectionGroup table from old online review
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + ScSectionGroup.TABLE_NAME +
					" WHERE template_id = ?");
        stmt.setInt(1, templateId);

        ResultSet rs = stmt.executeQuery();

        List list = new ArrayList();

        while (rs.next()) {
        	ScSectionGroup table = new ScSectionGroup();
            table.setGroupId(rs.getInt(ScSectionGroup.GROUP_ID_NAME));
            table.setGroupName(rs.getString(ScSectionGroup.GROUP_NAME_NAME));
            table.setGroupSeqLoc(rs.getInt(ScSectionGroup.GROUP_SEQ_LOC_NAME));
            table.setSections(loadScorecardSection(templateId, table.getGroupId()));
            list.add(table);
        }

        Util.logAction(list.size(), "loadScSectionGroup");
        DatabaseUtils.closeResultSetSilently(rs);
        DatabaseUtils.closeStatementSilently(stmt);

        return list;
    }

    /**
     * Load ScSectionGroup data to transform.
     *
     * @param templateId the scorecard template id
     * @param sectionId the scorecard section id
     * @return the loaded ScSectionGroup data
     *
     * @throws SQLException if error occurs while execute sql statement
     */
    private List loadQuestionTemplate(int templateId, int sectionId) throws SQLException {
		Util.start("loadQuestionTemplate");
        // load QuestionTemplate table from old online review
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + QuestionTemplate.TABLE_NAME +
				" WHERE cur_version = 1 and template_id = ? AND section_id = ?");
		stmt.setInt(1, templateId);
		stmt.setInt(2, sectionId);

		ResultSet rs = stmt.executeQuery();
        List list = new ArrayList();

        while (rs.next()) {
        	int qTemplateVId = rs.getInt(QuestionTemplate.Q_TEMPLATE_V_ID_NAME);
        	if (qTemplateVId == 16 || qTemplateVId == 49 || qTemplateVId == 51) {
        		// those are bad data
        		continue;
        	}
        	QuestionTemplate table = new QuestionTemplate();
            table.setQTemplateVid(qTemplateVId);
            table.setQTemplateId(rs.getInt(QuestionTemplate.Q_TEMPLATE_ID_NAME));
            table.setQuestionType(rs.getInt(QuestionTemplate.QUESTION_TYPE_NAME));
            table.setQuestionText(rs.getString(QuestionTemplate.QUESTION_TEXT_NAME));
            table.setQuestionWeight(rs.getInt(QuestionTemplate.QUESTION_WEIGHT_NAME));
            table.setQuestionSecLoc(rs.getInt(QuestionTemplate.QUESTION_SEC_LOC_NAME));
            list.add(table);
        }

        Util.logAction(list.size(), "loadQuestionTemplate");
        DatabaseUtils.closeResultSetSilently(rs);
        DatabaseUtils.closeStatementSilently(stmt);

        return list;
    }
}
