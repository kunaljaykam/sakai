/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		 http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.rubrics.impl;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.rubrics.api.RubricsService;
import org.sakaiproject.rubrics.api.model.Criterion;
import org.sakaiproject.rubrics.api.model.Rubric;
import org.sakaiproject.rubrics.api.repository.RubricRepository;
import org.sakaiproject.search.api.EntityContentProducer;
import org.sakaiproject.search.api.SearchIndexBuilder;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.search.api.SearchUtils;
import org.sakaiproject.search.model.SearchBuilderItem;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.SessionManager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class RubricsContentProducer implements EntityContentProducer {

	@Setter @Getter
	private SearchService searchService = null;

	@Setter @Getter
	private SearchIndexBuilder searchIndexBuilder = null;

	@Setter @Getter
	private EntityManager entityManager = null;

	@Setter @Getter
	private RubricsService rubricsService = null;
	
	@Setter @Getter
	private RubricRepository rubricRepository = null;

	@Setter @Getter
	private SiteService siteService = null;
	
	@Setter @Getter
	private SecurityService securityService = null;
	
	@Setter @Getter
	private ServerConfigurationService serverConfigurationService = null;
	
	@Setter @Getter
	private SessionManager sessionManager = null;

	// Events that add content to the index
	private final List<String> addEvents = new ArrayList<>();
	
	// Events that remove content from the index
	private final List<String> removeEvents = new ArrayList<>();

	protected void init() throws Exception {
		log.info("init()");
		
		if ("true".equals(serverConfigurationService.getString("search.enable", "false"))) {
			
			// Register this as a search content producer
			getSearchIndexBuilder().registerEntityContentProducer(this);
			
			// Register events that should trigger index updates
			for (String event : addEvents) {
				getSearchService().registerFunction(event);
			}
			for (String event : removeEvents) {
				getSearchService().registerFunction(event);
			}
			log.info("Rubrics search integration initialized");
		} else {
			log.info("Search is not enabled, rubrics search integration not started");
		}
	}

	protected void destroy() throws Exception {
		log.info("destroy()");
	}

	@Override
	public boolean canRead(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return false;
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return false;
		
		// Check if rubric is published (not draft)
		if (rubric.getDraft()) {
			return false;
		}
		
		// For shared rubrics, allow anyone to see them
		if (rubric.getShared()) {
			return true;
		}
		
		// Otherwise, check if user has access to the site where the rubric owner has access
		String ownerId = rubric.getOwnerId();
		
		// Basic permission check - if it's shared or user is the owner
		try {
			String currentUserId = sessionManager.getCurrentSessionUserId();
			return ownerId.equals(currentUserId) || rubric.getShared();
		} catch (Exception e) {
			log.debug("Error checking permissions for rubric reference: {}", ref.getReference(), e);
			return false;
		}
	}

	@Override
	public Integer getAction(Event event) {
		String eventName = event.getEvent();
		if (addEvents.contains(eventName)) {
			return SearchBuilderItem.ACTION_ADD;
		}
		if (removeEvents.contains(eventName)) {
			return SearchBuilderItem.ACTION_DELETE;
		}
		return SearchBuilderItem.ACTION_UNKNOWN;
	}

	@Override
	public String getContainer(String ref) {
		return "";
	}

	@Override
	public String getContent(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return "";
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return "";
		
		StringBuilder sb = new StringBuilder();
		
		// Add rubric title
		SearchUtils.appendCleanString(rubric.getTitle(), sb);
		
		// Add criteria titles and descriptions
		for (Criterion criterion : rubric.getCriteria()) {
			SearchUtils.appendCleanString(criterion.getTitle(), sb);
			SearchUtils.appendCleanString(criterion.getDescription(), sb);
		}
		
		log.debug("Rubric Content for reference: {} is: {}", ref.getReference(), sb.toString());
		return sb.toString();
	}

	@Override
	public Reader getContentReader(String reference) {
		return new StringReader(getContent(reference));
	}

	@Override
	public Map<String, ?> getCustomProperties(String ref) {
		return null;
	}

	@Override
	public String getCustomRDF(String ref) {
		return null;
	}

	@Override
	public String getId(String reference) {
		try {
			Reference ref = getReference(reference);
			if (ref != null) {
				return ref.getId();
			}
		} catch (Exception e) {
			log.debug("Error getting id for reference: {}", reference, e);
		}
		return "";
	}

	public List<String> getSiteContent(String context) {
		List<String> references = new ArrayList<>();
		
		try {
			// Get all rubrics for the site and filter out drafts
			List<Rubric> rubrics = rubricRepository.findByOwnerId(context);
			
			for (Rubric rubric : rubrics) {
				// Only include published (non-draft) rubrics
				if (!rubric.getDraft()) {
					String ref = getReference(context, rubric.getId());
					references.add(ref);
				}
			}
			
		} catch (Exception e) {
			log.warn("Error getting site content for context: {}", context, e);
		}
		
		return references;
	}

	@Override
	public Iterator<String> getSiteContentIterator(String context) {
		return getSiteContent(context).iterator();
	}

	@Override
	public String getSiteId(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return null;
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return null;
		
		// For rubrics, the ownerId is typically the site ID or user ID
		return rubric.getOwnerId();
	}

	@Override
	public String getSubType(String ref) {
		return "";
	}

	@Override
	public String getTitle(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return "";
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return "";
		
		String title = rubric.getTitle();
		return title != null ? title : "";
	}

	@Override
	public String getTool() {
		return "rubrics";
	}

	@Override
	public String getType(String ref) {
		return "rubric";
	}

	@Override
	public String getUrl(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return "";
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return "";
		
		String ownerId = rubric.getOwnerId();
		Long rubricId = rubric.getId();
		
		// Return URL to the specific rubric
		return "/portal/site/" + ownerId + "/tool/sakai.rubrics?rubricId=" + rubricId;
	}

	@Override
	public boolean isContentFromReader(String reference) {
		return false;
	}

	@Override
	public boolean isForIndex(String reference) {
		Reference ref = getReference(reference);
		if (ref == null) return false;
		
		Rubric rubric = getRubric(ref);
		if (rubric == null) return false;
		
		// Only index published rubrics
		return !rubric.getDraft();
	}

	@Override
	public boolean matches(String reference) {
		return reference.startsWith(RubricsService.REFERENCE_ROOT);
	}

	@Override
	public boolean matches(Event event) {
		return matches(event.getResource());
	}

	/**
	 * Helper method to get Rubric from a reference
	 */
	private Rubric getRubric(Reference ref) {
		try {
			String id = ref.getId();
			if (id != null) {
				Long rubricId = Long.parseLong(id);
				return rubricRepository.findById(rubricId).orElse(null);
			}
		} catch (Exception e) {
			log.debug("Error getting Rubric for reference: {}", ref.getReference(), e);
		}
		return null;
	}

	/**
	 * Helper method to construct a reference string
	 */
	private String getReference(String siteId, Long rubricId) {
		return RubricsService.REFERENCE_ROOT + "/" + siteId + "/" + rubricId.toString();
	}

	/**
	 * Helper method to parse a reference from a string
	 */
	private Reference getReference(String reference) {
		try {
			EntityProducer ep = getProducer(reference);
			if (ep != null) {
				return entityManager.newReference(reference);
			}
		} catch (Exception e) {
			log.debug("Error parsing reference: {}", reference, e);
		}
		return null;
	}

	/**
	 * Helper method to get the entity producer for a reference
	 */
	private EntityProducer getProducer(String ref) {
		return entityManager.getEntityProducer(ref, null);
	}
} 