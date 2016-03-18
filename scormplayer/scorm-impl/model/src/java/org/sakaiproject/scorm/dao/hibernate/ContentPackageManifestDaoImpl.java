/*
 * #%L
 * SCORM Model Impl
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
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
 * #L%
 */
package org.sakaiproject.scorm.dao.hibernate;

import java.io.Serializable;

import org.sakaiproject.scorm.dao.api.ContentPackageManifestDao;
import org.sakaiproject.scorm.model.api.ContentPackageManifest;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ContentPackageManifestDaoImpl extends HibernateDaoSupport implements ContentPackageManifestDao {

	public ContentPackageManifest load(Serializable id) {
		return (ContentPackageManifest) getHibernateTemplate().load(ContentPackageManifest.class, id);
	}

	public Serializable save(ContentPackageManifest manifest) {
		return getHibernateTemplate().save(manifest);
	}

}
