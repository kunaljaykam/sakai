/**********************************************************************************
 * $URL: $
 * $Id: $
 ***********************************************************************************
 *
 * Author: Charles Hedrick, hedrick@rutgers.edu
 *
 * Copyright (c) 2010 Rutgers, the State University of New Jersey
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.lessonbuildertool.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;

import uk.org.ponder.messageutil.MessageLocator;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean.UrlItem;
import org.sakaiproject.lti.api.LTIService;
import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.util.ResourceLoader;

import org.tsugi.lti.LTIUtil;
import org.sakaiproject.lti.util.SakaiLTIUtil;

/**
 * Interface to LTI Content Items
 *
 * @author Charles Hedrick <hedrick@rutgers.edu>
 * @author Charles Severance <csev@umich.edu>
 *
 */

@Slf4j
public class BltiEntity implements LessonEntity, BltiInterface {
    private static Cache bltiCache = null;
    protected static final int DEFAULT_EXPIRATION = 10 * 60;
    protected static ResourceLoader rb = new ResourceLoader("lessons");

    private SimplePageBean simplePageBean;

    protected static LTIService ltiService = null;

    public void setSimplePageBean(SimplePageBean simplePageBean) {
        this.simplePageBean = simplePageBean;
    }

    private LessonEntity nextEntity = null;
    public void setNextEntity(LessonEntity e) {
        nextEntity = e;
    }
    public LessonEntity getNextEntity() {
        return nextEntity;
    }

    static MemoryService memoryService = null;
    public void setMemoryService(MemoryService m) {
        memoryService = m;
    }

    private static ToolManager toolManager;
    public void setToolManager(ToolManager tm) {
        if (toolManager == null ) toolManager = tm;
    }

    private static SiteService siteService;
    public void setSiteService(SiteService sm) {
        if (siteService == null ) siteService = sm;
    }

    static MessageLocator messageLocator = null;
    public void setMessageLocator(MessageLocator m) {
        messageLocator = m;
    }

    static String returnUrl = null;
    public void setReturnUrl(String m) {
        returnUrl = m;
    }

    public void init () {
        log.info("init()");
        bltiCache = memoryService
            .getCache("org.sakaiproject.lessonbuildertool.service.BltiEntity.cache");

        if ( ltiService == null ) {
            Object service = ComponentManager.get("org.sakaiproject.lti.api.LTIService");
            if (service == null) {
                log.info("can't find LTI Service -- disabling LTI support");
                return;
            }
            ltiService = (LTIService)service;
            log.info("LTI initialized");
        }

    }

    public void destroy()
    {
        //    bltiCache.destroy();
        //    bltiCache = null;

        log.info("destroy()");
    }


    // to create bean. the bean is used only to call the pseudo-static
    // methods such as getEntitiesInSite. So type, id, etc are left uninitialized

    public boolean servicePresent() {
        return ltiService != null;
    }

    protected BltiEntity() {
    }

    protected BltiEntity(int type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getToolId() {
        return "sakai.blti";
    }

    // the underlying object, something Sakaiish
    protected String id;
    protected int type;
    // not required fields. If we need to look up
    // the actual objects, lets us cache them

    protected Map<String,Object> content;
    protected Map<String,Object> tool;

    public Map<String, Object> getContent() { loadContent(); return content; };
    public Map<String, Object> getTool() { loadContent(); return tool; };

    // type of the underlying object
    public int getType() {
        return type;
    }

    public int getLevel() {
        return 0;
    }

    public int getTypeOfGrade() {
        return 1;
    }

    public boolean showAdditionalLink() {
        return false;
    }

    // hack for forums. not used for assessments, so always ok
    public boolean isUsable() {
        return true;
    }

    public String getReference() {
        return "/" + BLTI + "/" + id;
    }

    public List<LessonEntity> getEntitiesInSite() {
        return getEntitiesInSite(null, null);
    }

    public List<LessonEntity> getEntitiesInSite(SimplePageBean bean) {
        return getEntitiesInSite(bean, null);
    }

    // find tools in site, but organized by forum
    public List<LessonEntity> getEntitiesInSite(SimplePageBean bean, Integer bltiToolId) {
        List<LessonEntity> ret = new ArrayList<LessonEntity>();
        if (ltiService == null)
            return ret;
        String search = null;
        if (bltiToolId != null)
            search = "tool_id=" + bltiToolId;
        List<Map<String,Object>> contents = ltiService.getContents(search,null,0,0, bean.getCurrentSiteId());
        for (Map<String, Object> content : contents ) {
            Long id = getLong(content.get(LTIService.LTI_ID));
            if ( id == -1 ) continue;
            BltiEntity entity = new BltiEntity(TYPE_BLTI, id.toString());
            entity.content = content;
            entity.setSimplePageBean(bean);
            ret.add(entity);
        }
        return ret;
    }

    public LessonEntity getEntity(String ref, SimplePageBean o) {
        return getEntity(ref);
    }

    public LessonEntity getEntity(String ref) {
        int i = ref.indexOf("/",1);

        String typeString = ref.substring(1, i);
        String idString = ref.substring(i+1);
        String id = "";
        try {
            id = idString;
        } catch (Exception ignore) {
            return null;
        }

        if (typeString.equals(BLTI)) {
            return new BltiEntity(TYPE_BLTI, id);
        } else if (nextEntity != null) {
            // in case we chain to a different implementation. Not likely for BLTI
            return nextEntity.getEntity(ref);
        } else
            return null;
    }

    public void preShowItem(SimplePageItem item) {
        loadContent();
        if ( content != null ) {
            Long contentKey = getLong(id);
            String item_name = item.getName();
            String item_description = item.getDescription();
            String item_format = item.getFormat();
            String content_title = (String) content.get(LTIService.LTI_TITLE);
            String content_description = (String) content.get(LTIService.LTI_DESCRIPTION);

            if ( (item_name != null && ! item_name.equals(content_title)) ||
                    (item_description != null && ! item_description.equals(content_description)) ) {

                Map<String, Object> updates = new HashMap();
                if ( item_name != null ) updates.put(LTIService.LTI_TITLE, item_name);
                if ( item_description != null ) updates.put(LTIService.LTI_DESCRIPTION, item_description);

                // This uses the Dao access since 99% of the time we are launching as a student
                // after the instructor updates the assignment, and the student is
                // the first to launch after the change.
                if ( ltiService != null && contentKey != null ) {
                    ltiService.updateContentDao(contentKey, updates);
                    log.debug("Content Item id={} updated.", contentKey);
                }
            }
        }
    }

    protected void loadContent() {
        if ( content != null ) return;
        if ( id == null ) return; // Likely a failure
        if ( ltiService == null) return;  // not basiclti or old
        Long key = getLong(id);
        content = ltiService.getContent(key, toolManager.getCurrentPlacement().getContext());
        if ( content == null ) return;
        Long toolKey = getLongNull(content.get("tool_id"));
        if (toolKey != null ) tool = ltiService.getTool(toolKey, toolManager.getCurrentPlacement().getContext());
        // Treat a content item with no associated tool as non-existant
        if ( tool == null ) content = null;
    }

    // properties of entities
    public String getTitle() {
        loadContent();
        if ( content == null ) return null;
        return (String) content.get(LTIService.LTI_TITLE);
    }

    public String getDescription() {
        if(tool != null){
            return (String) tool.get(LTIService.LTI_DESCRIPTION);
        }
        loadContent();
        return tool == null ? null : (String) tool.get(LTIService.LTI_DESCRIPTION);
    }

    public String getIcon() {
        loadContent();
        if ( content == null ) return null;
        String result = (String) content.get(LTIService.LTI_FA_ICON);
        if (result == null && tool != null ) {
            // Inherit the tool's custom icon if set
            result = (String) tool.get(LTIService.LTI_FA_ICON);
        }
        return result;
    }

    private String getErrorUrl() {
        return "javascript:alert('" + messageLocator.getMessage("simplepage.format.item_removed_text").replace("'", "\\'") + "')";
    }

    public String getEditNote() {
        loadContent();
        if ( content == null ) return null;  // Lessons will show *deleted*

        if ( tool == null ) {
            return rb.getString("lti.tool.missing");
        }


        String siteId = getSiteId();
        if ( StringUtils.isNotEmpty(siteId) && siteId.equals((String) tool.get(LTIService.LTI_SITE_ID))
             && LTIService.LTI_SECRET_INCOMPLETE.equals((String) tool.get(LTIService.LTI_SECRET))
             && LTIService.LTI_SECRET_INCOMPLETE.equals((String) tool.get(LTIService.LTI_CONSUMERKEY)) ) {
            return rb.getString("lti.tool.import.incomplete");
        }

        if ( ltiService.isDraft(tool) ) {
            return rb.getString("lti.tool.is.draft");
        }

        return null;
    }

    public String getUrl() {
        loadContent();
        if ( content == null ) return getErrorUrl();
        String ret = (String) content.get("launch_url");
        if ( ret == null ) return getErrorUrl();
        ret = ServerConfigurationService.getServerUrl() + ret;
        return ret;
    }

    public Date getDueDate() {
        return null;
    }

    public LessonSubmission getSubmission(String userId) {
        // students don't have submissions to BLTI
        return null;
    }

    // we can do this for real, but the API will cause us to get all the submissions in full, not just a count.
    // I think it's cheaper to get the best assessment, since we don't actually care whether it's 1 or >= 1.
    public int getSubmissionCount(String user) {
        return 0;
    }

    // URL to create a new item. Normally called from the generic entity, not a specific one
    // can't be null
    public List<UrlItem> createNewUrls(SimplePageBean bean) {
        return null;
    }

    public List<UrlItem> createNewUrls(SimplePageBean bean, Integer bltiToolId) {
        return null;
    }

    public boolean isPopUp() {
        loadContent();
        return SakaiLTIUtil.getNewpage(tool, content, false);
    }

    public int frameSize() {
        loadContent();
        if ( content == null  ) return -1;
        Long frameSize = getLong(content.get(LTIService.LTI_FRAMEHEIGHT));
        return frameSize.intValue();
    }
    // URL to edit an existing entity.
    // Can be null if we can't get one or it isn't needed
    public String editItemUrl(SimplePageBean bean) {
        String toolId = bean.getCurrentTool("sakai.siteinfo");
        if ( toolId == null ) return null;
        return editItemUrl(toolId);
    }

    public String editItemUrl(String toolId) {
        if ( toolId == null ) return null;
        loadContent();
        if (content == null)
            return null;
        String url = ServerConfigurationService.getToolUrl() + "/" + toolId + "/sakai.lti.admin.helper.helper?panel=ContentConfig&flow=lessons&id=" +
            content.get(LTIService.LTI_ID);
        if ( returnUrl != null ) {
            url = url + "&returnUrl=" + URLEncoder.encode(returnUrl);
        } else {
            url = url + "&returnUrl=about:blank";
        }
        return url;
    }

    // for most entities editItem is enough, however tests allow separate editing of
    // contents and settings. This will be null except in that situation
    public String editItemSettingsUrl(SimplePageBean bean) {
        return null;
    }

    public boolean objectExists() {
        loadContent();
        return content != null;
    }

    public boolean notPublished(String ref) {
        return false;
    }

    public boolean notPublished() {
        return !objectExists();
    }

    // return the list of groups if the item is only accessible to specific groups
    // null if it's accessible to the whole site.
    public Collection<String> getGroups(boolean nocache) {
        // done entirely within LB, this item type is not group-aware
        return null;
    }

    // set the item to be accessible only to the specific groups.
    // null to make it accessible to the whole site
    public void setGroups(Collection<String> groups) {
        // not group aware
    }

    public String doImportTool(String launchUrl, String bltiTitle, String strXml, String custom, boolean open_same_window)
    {
        if ( ltiService == null ) return null;
        log.debug("bltiTitle={} launchUrl={} open_same_window=", bltiTitle, bltiTitle, open_same_window);

        String toolBaseUrl = SakaiLTIUtil.stripOffQuery(launchUrl);

        // Lets find the right tool to assiociate with
        List<Map<String,Object>> tools = ltiService.getTools(null,null,0,0, simplePageBean.getCurrentSiteId());
        Map<String, Object> theTool = SakaiLTIUtil.findBestToolMatch(launchUrl, null, tools);

        if ( theTool == null ) {
            log.debug("Inserting new tool title={} url={}",bltiTitle, launchUrl);
            Properties props = new Properties ();
            props.setProperty(LTIService.LTI_LAUNCH,toolBaseUrl);
            if ( toolBaseUrl.equals(launchUrl) ) {
                props.setProperty(LTIService.LTI_TITLE, bltiTitle);
            } else {
                props.setProperty(LTIService.LTI_TITLE, toolBaseUrl);
            }
            props.setProperty(LTIService.LTI_CONSUMERKEY, LTIService.LTI_SECRET_INCOMPLETE);
            props.setProperty(LTIService.LTI_SECRET, LTIService.LTI_SECRET_INCOMPLETE);
            props.setProperty(LTIService.LTI_ALLOWOUTCOMES, "1");
            props.setProperty(LTIService.LTI_SENDNAME, "1");
            props.setProperty(LTIService.LTI_SENDEMAILADDR, "1");
            // Allow the content item to choose open in popup so the user can change it in the Lessons UI
            props.setProperty(LTIService.LTI_NEWPAGE, "2");
            props.setProperty(LTIService.LTI_XMLIMPORT,strXml);
            props.setProperty(LTIService.LTI13,"0");
            if (custom != null)
                props.setProperty(LTIService.LTI_CUSTOM, custom);
            Object result = ltiService.insertTool(props, simplePageBean.getCurrentSiteId());
            if ( result instanceof String ) {
                log.info("Could not insert tool - "+result);
            }
            if ( result instanceof Long ) theTool = ltiService.getTool((Long) result, simplePageBean.getCurrentSiteId());
        }

        Map<String,Object> theContent = null;
        Long contentKey = null;
        if ( theTool != null ) {
            Properties props = new Properties ();
            String toolId = getLong(theTool.get(LTIService.LTI_ID)).toString();
            log.debug("Inserting new content toolId={} title={} url={}", toolId, bltiTitle, launchUrl);
            props.setProperty(LTIService.LTI_TOOL_ID, toolId);
            props.setProperty(LTIService.LTI_TITLE, bltiTitle);
            props.setProperty(LTIService.LTI_LAUNCH,launchUrl);
            props.setProperty(LTIService.LTI_NEWPAGE, SakaiLTIUtil.getNewpage(tool, null, open_same_window) ? "1" : "0");
            props.setProperty(LTIService.LTI_XMLIMPORT,strXml);
            if ( custom != null ) props.setProperty(LTIService.LTI_CUSTOM,custom);
            Object result = ltiService.insertContent(props, simplePageBean.getCurrentSiteId());
            if ( result instanceof String ) {
                log.info("Could not insert content - "+result);
            }
            if ( result instanceof Long ) theContent = ltiService.getContent((Long) result, simplePageBean.getCurrentSiteId());
        }

        String sakaiId = null;
        if ( theContent != null ) {
            sakaiId = "/blti/" + theContent.get(LTIService.LTI_ID);
            log.info("Adding LTI content "+sakaiId);
        }
        return sakaiId;
    }

    // TODO: Could we get simplePageBean populated here and not build our own get
    public String getCurrentTool(String commonToolId) {
        try {
            String currentSiteId = toolManager.getCurrentPlacement().getContext();
            Site site = siteService.getSite(currentSiteId);
            ToolConfiguration toolConfig = site.getToolForCommonId(commonToolId);
            if (toolConfig == null) return null;
            return toolConfig.getId();
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLong(Object key) {
        Long retval = getLongNull(key);
        if (retval != null)
            return retval;
        return Long.valueOf(-1);
    }

    public Long getLongNull(Object key) {
        if (key == null)
            return null;
        if (key instanceof Number)
            return Long.valueOf(((Number) key).longValue());
        if (key instanceof String) {
            try {
                return Long.valueOf((String) key);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String getObjectId(){
        return null;
    }

    public String findObject(String objectid, Map<String,String>objectMap, String siteid) {
        if (nextEntity != null)
            return nextEntity.findObject(objectid, objectMap, siteid);
        return null;
    }

    public String getSiteId() {
        loadContent();
        if ( content == null ) return null;
        return (String) content.get(LTIService.LTI_SITE_ID);
    }

}
