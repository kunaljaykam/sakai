/*************************************************************************************
 * Copyright 2006, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.

 *************************************************************************************/

package org.sakaiproject.commons.api.datamodel;

import java.time.Instant;
import java.util.Stack;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.text.StringEscapeUtils;
import org.sakaiproject.commons.api.CommonsReferenceReckoner;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.util.BaseResourceProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Comment implements Entity {

    private String id = "";
    private String content = "";
    private long createdDate = -1L;
    private long modifiedDate = -1L;
    private String creatorId;
    private String creatorDisplayName;
    private String creatorUserName;
    private String postId;
    private Post post;
    private String url;

    public Comment() {
        this("");
    }

    public Comment(ResultSet rs) throws SQLException {

        this.setId(rs.getString("ID"));
        this.setPostId(rs.getString("POST_ID"));
        this.setContent(rs.getString("CONTENT"));
        this.setCreatorId(rs.getString("CREATOR_ID"));
        this.setCreatedDate(rs.getTimestamp("CREATED_DATE").getTime());
        this.setModifiedDate(rs.getTimestamp("MODIFIED_DATE").getTime());
    }

    public Comment(String text) {
        this(text, Instant.now().toEpochMilli());
    }

    public Comment(String text, long createdDate) {

        setContent(text);
        this.createdDate = createdDate;
        modifiedDate = createdDate;
    }

    /**
     * If the supplied is different to the current, sets the modified date to
     * the current date so ... be careful!
     *
     * @param text
     */
    public void setContent(String text) {
        setContent(text, true);
    }

    public void setContent(String text, boolean modified) {
        if (!this.content.equals(text) && modified) {
            modifiedDate = Instant.now().toEpochMilli();
        }

        this.content = StringEscapeUtils.unescapeHtml4(text.trim());
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
        this.modifiedDate = createdDate;
    }

    public ResourceProperties getProperties() {
        ResourceProperties rp = new BaseResourceProperties();

        rp.addProperty("id", getId());
        return rp;
    }

    public String getReference() {
        return CommonsReferenceReckoner.reckoner().comment(this).reckon().getReference();
    }

    public String getReference(String base) {
        return getReference();
    }

    public String getUrl(String arg0) {
        return getUrl();
    }

    public Element toXml(Document arg0, Stack arg1) {
        return null;
    }
}
