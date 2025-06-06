/**
 * Copyright (c) 2003-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.site.util;

public class SiteConstants {
	
	/** the list of criteria for sorting */
	public static final String SORTED_BY_TITLE = "title";

	public static final String SORTED_BY_DESCRIPTION = "description";

	public static final String SORTED_BY_TYPE = "type";

	public static final String SORTED_BY_STATUS = "status";

	public static final String SORTED_BY_CREATION_DATE = "creationdate";

	public static final String SORTED_BY_JOINABLE = "joinable";

	public static final String SORTED_BY_PARTICIPANT_NAME = "participant_name";

	public static final String SORTED_BY_PARTICIPANT_UNIQNAME = "participant_uniqname";

	public static final String SORTED_BY_PARTICIPANT_ROLE = "participant_role";

	public static final String SORTED_BY_PARTICIPANT_ID = "participant_id";

	public static final String SORTED_BY_PARTICIPANT_COURSE = "participant_course";

	public static final String SORTED_BY_PARTICIPANT_CREDITS = "participant_credits";
	
	public static final String SORTED_BY_PARTICIPANT_STATUS = "participant_status";

	public static final String SORTED_BY_MEMBER_NAME = "member_name";
	
	public static final String SORTED_BY_GROUP_TITLE = "group_title";
	
	public static final String SORTED_BY_GROUP_SIZE = "group_size";

	/**
	 * This stores the list of roles that a group should have as participants from the containing sites.
	 */
	public static final String GROUP_PROP_ROLE_PROVIDERID = "group_prop_role_providerid";

	public static final String GROUP_PROP_WSETUP_CREATED = "group_prop_wsetup_created";

	public static final int SITE_GROUP_TITLE_LIMIT = 99;
	
	// system property variable to hide PageOrder tab for certain types of sites, e.g. if set to "course,project", the PageOrder tool tab will be hidden for all course sites and project sites. 
	public final static String SAKAI_PROPERTY_HIDE_PAGEORDER_SITE_TYPES = "hide.pageorder.site.types";

	/**
	 * This property is used on groups to mark which roles in the site should automatically
	 * be members of this group.
	 */
	// site property variable to override the above settings. If true, the PageOrder tab will be shown.
	public final static String SITE_PROPERTY_OVERRIDE_HIDE_PAGEORDER_SITE_TYPES = "site.override.hide.pageorder.site.types";

	public final static String SITE_TYPE_MYWORKSPACE = "site_type_myworkspace";

	// All deleted sites.
	public final static String SITE_TYPE_DELETED = "site_type_deleted";
	
	public final static String SITE_TYPE_ALL = "site_type_all";

	public final static String SITE_ACTIVE = "pubView";

	public final static String SITE_INACTIVE = "inactive";

	// SAKAI-5903 Site Publish Type
	public final static String SITE_PUBLISH_TYPE = "publish_type";
	public final static String SITE_PUBLISH_TYPE_AUTO = "auto";
	public final static String SITE_PUBLISH_TYPE_SCHEDULED = "scheduled";
	public final static String SITE_PUBLISH_TYPE_MANUAL = "manual";
	public final static String SITE_PUBLISH_DATE = "publish_date";
	public final static String SITE_UNPUBLISH_DATE = "unpublish_date";

	//SAK-32127
	public final static String SITE_PROPERTY_HIDE_RESOURCES_SPECIAL_HIDDEN = "poh.resources.content.sync.visibility";
	public final static boolean SITE_PROPERTY_HIDE_RESOURCES_SPECIAL_HIDDEN_DEFAULT = true;

	public final static String PARTICIPANT_FILTER_TYPE_ALL = "[all]";
	public final static String PARTICIPANT_FILTER_TYPE_ROLE = "[role]";
	public final static String PARTICIPANT_FILTER_TYPE_GROUP = "[group]";
	public final static String PARTICIPANT_FILTER_TYPE_SECTION = "[section]";

	// For returning to Site Info landing page after clicking 'Cancel' from sub-projects
	public static final String STATE_TEMPLATE_INDEX = "site.templateIndex"; // The name of the Attribute for display template index
	public static final String SITE_INFO_TEMPLATE_INDEX = "12";

	// For returning to 'Manage Participants' after successful participant add
	public static final String MANAGE_PARTICIPANTS_TEMPLATE_INDEX = "63";

	// Sakai.properties keys
	public static final String SAK_PROP_SITE_SETUP_IMPORT_FILE = "site.setup.import.file";
	public static final boolean SAK_PROP_SITE_SETUP_IMPORT_FILE_DEFAULT = true;

	public static final String SAK_PROP_DISPLAY_USER_AUDIT_LOG = "user_audit_log_display";
	public static final boolean SAK_PROP_DISPLAY_USER_AUDIT_LOG_DEFAULT = true;

	public static final String SAK_PROP_ALLOW_DUPLICATE_SITE = "site.setup.allowDuplicateSite";
	public static final boolean SAK_PROP_ALLOW_DUPLICATE_SITE_DEFAULT = false;

	public static final String SAK_PROP_SITE_SETUP_ALLOW_EDIT_ROSTER = "site.setup.allow.editRoster";
	public static final boolean SAK_PROP_SITE_SETUP_ALLOW_EDIT_ROSTER_DEFAULT = true;

	public static final String SAK_PROP_CM_IMPLEMENTED = "site-manage.courseManagementSystemImplemented";
	public static final boolean SAK_PROP_CM_IMPLEMENTED_DEFAULT = true;

	public static final String SAK_PROP_CLEAN_IMPORT_SITE = "clean.import.site";
	public static final boolean SAK_PROP_CLEAN_IMPORT_SITE_DEFAULT = true;
}
