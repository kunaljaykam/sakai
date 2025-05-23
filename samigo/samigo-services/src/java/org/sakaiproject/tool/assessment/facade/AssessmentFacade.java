/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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


package org.sakaiproject.tool.assessment.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.osid.assessment.AssessmentException;

import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAttachmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionMetaDataIfc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

@Slf4j
@Getter
@Setter
public class AssessmentFacade extends AssessmentBaseFacade
    implements java.io.Serializable, AssessmentIfc
{
  private static final long serialVersionUID = 7526471155622776147L;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private AssessmentIfc data;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Long assessmentTemplateId;

  private Long assessmentId;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Set sectionSet;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Set assessmentAttachmentSet;

  private Integer questionSize;
  private Date startDate;
  private Date dueDate;
  private String lastModifiedDateForDisplay;
  private String releaseTo;
  private Map<String, String> releaseToGroups;
  private int groupCount;
  private boolean selected;
  private Integer multipleTimers;

  public AssessmentFacade() {
    //super();
    this.data = new AssessmentData();
    try {
      // assessment(org.osid.assessment.Assessment) is a protected properties
      // in AssessmentBaseFacade
      assessment.updateData(this.data);
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
  }

  /**
   * IMPORTANT: this constructor do not have "data", this constructor is
   * merely used for holding assessmentBaseId (which is the assessmentId), Title
   * & lastModifiedDate for displaying purpose.
   * This constructor does not persist data (which it has none) to DB
   * @param id
   * @param title
   * @param lastModifiedDate
   */
  public AssessmentFacade(Long id, String title, Date lastModifiedDate) {
    // in the case of template assessmentBaseId is the assessmentTemplateId
    super.setAssessmentBaseId(id);
    super.setTitle(title);
    super.setLastModifiedDate(lastModifiedDate);
  }
  
  /**
   * IMPORTANT: this constructor do not have "data", this constructor is
   * merely used for holding assessmentBaseId (which is the assessmentId), Title
   * & lastModifiedDate for displaying purpose.
   * This constructor does not persist data (which it has none) to DB
   * @param id
   * @param title
   * @param lastModifiedDate
   */
  public AssessmentFacade(Long id, String title, Date lastModifiedDate, String lastModifiedBy) {
    // in the case of template assessmentBaseId is the assessmentTemplateId
    super.setAssessmentBaseId(id);
    super.setTitle(title);
    super.setLastModifiedDate(lastModifiedDate);
    super.setLastModifiedBy(lastModifiedBy);
  }

  public AssessmentFacade(Long id, String title, Date lastModifiedDate, Date startDate, Date dueDate, String releaseTo, Map<String, String> releaseToGroups, String lastModifiedBy, int questionSize) {
	    // in the case of template assessmentBaseId is the assessmentTemplateId
	    super.setAssessmentBaseId(id);
	    super.setTitle(title);
	    super.setLastModifiedDate(lastModifiedDate);
	    super.setLastModifiedBy(lastModifiedBy);
	    this.questionSize = questionSize;
	    this.startDate = startDate;
	    this.dueDate = dueDate;
	    this.releaseTo = releaseTo;
	    this.releaseToGroups = releaseToGroups;
	    setGroupCount();
  }
  
  public AssessmentFacade(AssessmentIfc data, Boolean loadSection) {
    try {
      // assessment(org.osid.assessment.Assessment) is a protected properties
      // in AssessmentBaseFacade
      //assessment.updateData(this.data);
      super.setData(data);
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    // super class does not have assessmentTemplateId nor sectionSet and assessmentAttachmentSet,
    // so we need to set it here
    this.assessmentTemplateId = data.getAssessmentTemplateId();
    this.assessmentAttachmentSet = data.getAssessmentAttachmentSet();
    // sectionSet is a set of SectionFacade
    this.sectionSet = new HashSet();

    // check if we need to load section
    if (loadSection.equals(Boolean.TRUE)){
      Set dataSet = data.getSectionSet();
      Iterator iter = dataSet.iterator();
      while (iter.hasNext()) {
        SectionData s = (SectionData) iter.next();
        this.sectionSet.add(new SectionFacade(s));
      }
    }
  }

  public AssessmentFacade(AssessmentIfc data) {
    try {
      // assessment(org.osid.assessment.Assessment) is a protected properties
      // in AssessmentBaseFacade
      //assessment.updateData(this.data);
      super.setData(data);
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    // super class does not have assessmentTemplateId nor sectionSet and AssessmentAttachemntSet,
    // so we need to set it here
    this.assessmentTemplateId = data.getAssessmentTemplateId();
    this.assessmentAttachmentSet = data.getAssessmentAttachmentSet();
    // sectionSet is a set of SectionFacade
    this.sectionSet = new HashSet();
    Set dataSet = data.getSectionSet();
    Iterator iter = dataSet.iterator();
    while (iter.hasNext()){
      SectionData s = (SectionData)iter.next();
      this.sectionSet.add(new SectionFacade(s));
    }
  }

  public Long getAssessmentId(){
    try {
      this.data = (AssessmentIfc) assessment.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getAssessmentId();
  }

  public Long getAssessmentTemplateId() {
    try {
      this.data = (AssessmentIfc) assessment.getData();
    }
    catch (AssessmentException ex) {
      log.error(ex.getMessage(), ex);
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getAssessmentTemplateId();
 }

  public void setAssessmentTemplateId(Long assessmentTemplateId) {
    this.assessmentTemplateId = assessmentTemplateId;
    this.data.setAssessmentTemplateId(assessmentTemplateId);
  }

  public Set getSectionSet() {
    try {
      this.data = (AssessmentIfc) assessment.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    this.sectionSet = new HashSet();
    Set dataSet = this.data.getSectionSet();
    Iterator iter = dataSet.iterator();
    while (iter.hasNext()){
      SectionData s = (SectionData)iter.next();
      this.sectionSet.add(new SectionFacade(s));
    }
    return this.sectionSet;
  }

  // sectionSet must be a set of SectionFacade
  public void setSectionSet(Set sectionSet) {
    this.sectionSet = sectionSet;
    HashSet set = new HashSet();
    Iterator iter = sectionSet.iterator();
    while (iter.hasNext()){
      SectionFacade sf = (SectionFacade)iter.next();
      set.add(sf.getData());
    }
    this.data.setSectionSet(set);
  }

  public ArrayList getSectionArray() {
    ArrayList list = new ArrayList();
    if (this.sectionSet != null){
      Iterator iter = this.sectionSet.iterator();
      while (iter.hasNext()) {
        SectionFacade s = (SectionFacade)iter.next();
        list.add(s);
      }
    }
    return list;
  }

  public ArrayList getSectionArraySorted() {
    ArrayList list = getSectionArray();
    Collections.sort(list);
    return list;
  }

  public SectionDataIfc getSection(Long sequence){
    ArrayList list = getSectionArraySorted();
    if (list == null)
      return null;
    else
      return (SectionDataIfc) list.get(sequence.intValue()-1);
  }

  public SectionDataIfc getDefaultSection(){
    ArrayList list = getSectionArraySorted();
    if (list == null)
      return null;
    else
      return (SectionDataIfc) list.get(0);
  }

  public Set getAssessmentAttachmentSet() throws DataFacadeException {
    try {
      this.data = (AssessmentIfc) assessment.getData();
      this.assessmentAttachmentSet = data.getAssessmentAttachmentSet();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return assessmentAttachmentSet;
  }

  public void setAssessmentAttachmentSet(Set assessmentAttachmentSet) {
    this.assessmentAttachmentSet = assessmentAttachmentSet;
    this.data.setAssessmentAttachmentSet(assessmentAttachmentSet);
  }

  public List getAssessmentAttachmentList() {
    ArrayList list = new ArrayList();
    if (assessmentAttachmentSet != null){
      Iterator iter = assessmentAttachmentSet.iterator();
      while (iter.hasNext()){
        AssessmentAttachmentIfc a = (AssessmentAttachmentIfc)iter.next();
        list.add(a);
      }
    }
    return list;
  }

  public void setGroupCount() {
    if (releaseToGroups != null) {
      groupCount = releaseToGroups.size();
    } else {
      groupCount = 0;
    }
  }

  public int hasMultipleTimers() {
    if(multipleTimers != null) {
      return multipleTimers;
    }
    int retA = 0;
    int retP = 0;
    int retQ = 0;
    if(data != null) {
      retA = Boolean.TRUE.toString().equalsIgnoreCase(data.getAssessmentMetaDataByLabel("hasTimeAssessment")) ? 1 : 0;
    }
    if(sectionSet != null) {
      for(Object s_o : sectionSet) {
        SectionDataIfc s = (SectionDataIfc)s_o;
        
        String value = s.getSectionMetaDataByLabel(SectionMetaDataIfc.TIMED);
        if(StringUtils.isNotBlank(value) && !StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), value)) {
          retP = 1;
        }
        if(s.getItemSet() != null && retQ == 0) {
          for(Object i_o : s.getItemSet()) {
            ItemDataIfc i = (ItemDataIfc)i_o;
            
            value = i.getItemMetaDataByLabel(ItemMetaDataIfc.TIMED);
            if(StringUtils.isNotBlank(value) && !StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), value)) {
              retQ = 1;
              break;
            }
          }
        }
        if(retP > 0 && retQ > 0) {
          break;
        }
      }
    }
    multipleTimers = retA + retP + retQ;
    return multipleTimers;
  }
}
