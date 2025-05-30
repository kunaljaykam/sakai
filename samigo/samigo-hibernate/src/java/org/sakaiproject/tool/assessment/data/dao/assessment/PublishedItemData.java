/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.tool.assessment.data.dao.assessment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemAttachmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemHistoricalIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTagIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

@Slf4j
public class PublishedItemData
    implements java.io.Serializable, ItemDataIfc, Comparable<ItemDataIfc> {
  private static final ResourceBundle rb = ResourceBundle.getBundle("org.sakaiproject.tool.assessment.bundle.Messages");

  private static final long serialVersionUID = 7526471155622776147L;

  private Long itemId;
  private Long originalItemId;
  private String itemIdString;
  private SectionDataIfc section;
  private Integer sequence;
  private Integer duration;
  private Integer triesAllowed;
  private String instruction;
  private String description;
  private Long typeId;
  private String grade;
  private Double score;
  private Double discount;
  private Boolean scoreDisplayFlag = Boolean.TRUE;
  private String hint;
  private Boolean hasRationale;
  private Integer status;
  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;
  @Getter private Boolean isExtraCredit = Boolean.FALSE;
  @Getter @Setter private Boolean isFixed = Boolean.FALSE;
  private Set itemTextSet;
  private Set itemMetaDataSet;
  private Set itemFeedbackSet;
  private ItemGradingData lastItemGradingDataByAgent;
  private Set<ItemAttachmentIfc> itemAttachmentSet;
  private Set<ItemTagIfc> itemTagSet;
  private Boolean partialCreditFlag;
  private Double minScore;
  private String itemHash;
  private String hash;

  private String themeText;
  private String leadInText;
  private String emiAnswerOptionLabels=null;
  
  private ArrayList emiAnswerOptions;
  private ArrayList emiQuestionAnswerCombinations;
  
  private Integer answerOptionsRichCount;
  private Integer answerOptionsSimpleOrRich;

  private String tagListToJsonString;

  @Getter @Setter private Integer cancellation = ItemDataIfc.ITEM_NOT_CANCELED;

  public PublishedItemData() {}

  // this constructor should be deprecated, it is missing triesAllowed
  public PublishedItemData(SectionDataIfc section, Integer sequence,
                  Integer duration, String instruction, String description,
                  Long typeId, String grade, Double score, Boolean scoreDisplayFlag, Double discount, Double minScore, String hint,
                  Boolean hasRationale, Integer status, String createdBy,
                  Date createdDate, String lastModifiedBy,
                  Date lastModifiedDate,
                  Set itemTextSet, Set itemMetaDataSet, Set itemFeedbackSet, Boolean partialCreditFlag, String hash, String itemHash) {
    this.section = section;
    this.sequence = sequence;
    this.duration = duration;
    this.instruction = instruction;
    this.description = description;
    this.typeId = typeId;
    this.grade = grade;
    this.score = score;
    this.scoreDisplayFlag = scoreDisplayFlag;
    this.discount = discount;
    this.hint = hint;
    this.hasRationale = hasRationale;
    this.status = status;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.itemTextSet = itemTextSet;
    this.itemMetaDataSet = itemMetaDataSet;
    this.itemFeedbackSet = itemFeedbackSet;
    this.partialCreditFlag=partialCreditFlag;
    this.minScore = minScore;
    this.hash = hash;
    this.itemHash = itemHash;
  }

  public PublishedItemData(SectionDataIfc section, Integer sequence,
                  Integer duration, String instruction, String description,
                  Long typeId, String grade, Double score, Boolean scoreDisplayFlag, Double discount, Double minScore, String hint,
                  Boolean hasRationale, Integer status, String createdBy,
                  Date createdDate, String lastModifiedBy,
                  Date lastModifiedDate,
                  Set itemTextSet, Set itemMetaDataSet, Set itemFeedbackSet,
                  Integer triesAllowed, Boolean partialCreditFlag, String hash, String itemHash) {
    this.section = section;
    this.sequence = sequence;
    this.duration = duration;
    this.instruction = instruction;
    this.description = description;
    this.typeId = typeId;
    this.grade = grade;
    this.score = score;
    this.scoreDisplayFlag = scoreDisplayFlag;
    this.discount = discount;
    this.hint = hint;
    this.hasRationale = hasRationale;
    this.status = status;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.itemTextSet = itemTextSet;
    this.itemMetaDataSet = itemMetaDataSet;
    this.itemFeedbackSet = itemFeedbackSet;
    this.triesAllowed = triesAllowed;
    this.partialCreditFlag=partialCreditFlag;
    this.minScore = minScore;
    this.hash = hash;
    this.itemHash = itemHash;
  }

  public PublishedItemData(SectionDataIfc section, Integer sequence,
                  Integer duration, String instruction, String description,
                  Long typeId, String grade, Double score, Boolean scoreDisplayFlag, Double discount, Double minScore, String hint,
                  Boolean hasRationale, Integer status, String createdBy,
                  Date createdDate, String lastModifiedBy,
                  Date lastModifiedDate,
                  Set itemTextSet, Set itemMetaDataSet, Set itemFeedbackSet,
                  Integer triesAllowed, Boolean partialCreditFlag, String hash, String itemHash,
                  Long originalItemId) {
    this.section = section;
    this.sequence = sequence;
    this.duration = duration;
    this.instruction = instruction;
    this.description = description;
    this.typeId = typeId;
    this.grade = grade;
    this.score = score;
    this.scoreDisplayFlag = scoreDisplayFlag;
    this.discount = discount;
    this.hint = hint;
    this.hasRationale = hasRationale;
    this.status = status;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
    this.itemTextSet = itemTextSet;
    this.itemMetaDataSet = itemMetaDataSet;
    this.itemFeedbackSet = itemFeedbackSet;
    this.triesAllowed = triesAllowed;
    this.partialCreditFlag=partialCreditFlag;
    this.minScore = minScore;
    this.hash = hash;
    this.itemHash = itemHash;
    this.originalItemId = originalItemId;
  }

  public Long getItemId() {
    return this.itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
    setItemIdString(itemId.toString());
  }

  public void setIsExtraCredit(Boolean extraCredit) {
    this.isExtraCredit = BooleanUtils.toBoolean(extraCredit);
  }

  public String getItemIdString() {
    return this.itemIdString;
  }

  public void setItemIdString(String itemIdString) {
    this.itemIdString = itemIdString;
  }

  public Long getOriginalItemId() {
    return this.originalItemId;
  }

  public void setOriginalItemId(Long originalItemId) {
    this.originalItemId = originalItemId;
  }

  public SectionDataIfc getSection() {
    return this.section;
  }

  public void setSection(SectionDataIfc section) {
    this.section = section;
  }

  public Integer getDuration() {
    return this.duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getSequence() {
    return this.sequence;
  }

  public void setSequence(Integer sequence) {
    this.sequence = sequence;
  }

  public String getInstruction() {
    return this.instruction;
  }

  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getTypeId() {
    return this.typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  public String getGrade() {
    return this.grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public Double getScore() {
    return this.score;
  }

  public void setScore(Double score) {
    this.score = score;
  }
  
  public Boolean getScoreDisplayFlag(){
	  if(this.scoreDisplayFlag == null){
		  return Boolean.TRUE;
	  }
	  return this.scoreDisplayFlag;
  }
  
  public void setScoreDisplayFlag(Boolean scoreDisplayFlag){
	  this.scoreDisplayFlag = scoreDisplayFlag;
  }

  public Double getDiscount() {
	  if (this.discount==null){
		  this.discount= Double.valueOf(0);
	  }
	  return this.discount;
  }

  public void setDiscount(Double discount) {
	  if (discount==null){
		  discount =Double.valueOf(0);
	  }
	  this.discount = discount;
  }
	  
  public String getHint() {
    return this.hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public Boolean getHasRationale() {
    return this.hasRationale;
  }

  public void setHasRationale(Boolean hasRationale) {
    this.hasRationale = hasRationale;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getLastModifiedDate() {
    return this.lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Set getItemTextSet() {
    return itemTextSet;
  }

  public void setItemTextSet(Set itemTextSet) {
    this.itemTextSet = itemTextSet;
  }

  public String getHash() {
    return this.hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getItemHash() {
    return this.itemHash;
  }

  public void setItemHash(String itemHash) {
    this.itemHash = itemHash;
  }

  public Set getItemMetaDataSet() {
    return itemMetaDataSet;
  }

  public void setItemMetaDataSet(Set itemMetaDataSet) {
    this.itemMetaDataSet = itemMetaDataSet;
  }

  public Set<ItemTagIfc> getItemTagSet() { return itemTagSet; }

  public void setItemTagSet(Set<ItemTagIfc> itemTagSet) { this.itemTagSet = itemTagSet; }

  public Set getItemFeedbackSet() {
    return itemFeedbackSet;
  }

  public void setItemFeedbackSet(Set itemFeedbackSet) {
    this.itemFeedbackSet = itemFeedbackSet;
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
  }

  public void addItemText(String text, Set answerSet) {
    if (this.itemTextSet == null) {
      this.itemTextSet = new HashSet();
    }
    Long sequence =  Long.valueOf(this.itemTextSet.size()+1);
    PublishedItemText itemText = new PublishedItemText(this, sequence, text, answerSet);
    this.itemTextSet.add(itemText);
  }

  public String getItemMetaDataByLabel(String label) {
    for (Iterator<PublishedItemMetaData> it = this.itemMetaDataSet.iterator(); it.hasNext();) {
      PublishedItemMetaData imd = (PublishedItemMetaData) it.next();
      if (imd.getLabel().equals(label)) {
        return (String) imd.getEntry();
      }
    }
    return null;
  }

  public void addItemMetaData(String label, String entry) {
    if (this.itemMetaDataSet == null) {
      setItemMetaDataSet(new HashSet());
    }
    this.itemMetaDataSet.add(new PublishedItemMetaData(this, label, entry));
  }

  public String getCorrectItemFeedback() {
    return getItemFeedback(PublishedItemFeedback.CORRECT_FEEDBACK);
  }

  public String getCorrectItemFeedbackValue() {
    return getItemFeedbackValue(PublishedItemFeedback.CORRECT_FEEDBACK);
  }

  public void setCorrectItemFeedback(String text, String value) {
    removeFeedbackByType(PublishedItemFeedback.CORRECT_FEEDBACK);
    addItemFeedback(PublishedItemFeedback.CORRECT_FEEDBACK, text, value);
  }

  public String getInCorrectItemFeedback() {
    return getItemFeedback(PublishedItemFeedback.INCORRECT_FEEDBACK);
  }

  public String getInCorrectItemFeedbackValue() {
    return getItemFeedbackValue(PublishedItemFeedback.INCORRECT_FEEDBACK);
  }

  public void setInCorrectItemFeedback(String text, String value) {
    removeFeedbackByType(PublishedItemFeedback.INCORRECT_FEEDBACK);
    addItemFeedback(PublishedItemFeedback.INCORRECT_FEEDBACK, text, value);
  }

 /**
  * Get General Feedback
  * @return
  */
  public String getGeneralItemFeedback() {
    return getItemFeedback(ItemFeedback.GENERAL_FEEDBACK);
  }

  /**
   * Set General Feedback
   * @param text
   */
  public void setGeneralItemFeedback(String text, String value) {
    removeFeedbackByType(ItemFeedback.GENERAL_FEEDBACK);
    addItemFeedback(ItemFeedback.GENERAL_FEEDBACK, text, value);
  }

  public String getItemFeedback(String typeId) {
    for (Iterator i = this.itemFeedbackSet.iterator(); i.hasNext(); ) {
      PublishedItemFeedback itemFeedback = (PublishedItemFeedback) i.next();
      if (itemFeedback.getTypeId().equals(typeId)) {
        return itemFeedback.getText();
      }
    }

    return null;
  }

  public String getItemFeedbackValue(String typeId) {
    for (Iterator i = this.itemFeedbackSet.iterator(); i.hasNext(); ) {
      PublishedItemFeedback itemFeedback = (PublishedItemFeedback) i.next();
      if (itemFeedback.getTypeId().equals(typeId)) {
        return itemFeedback.getTextValue();
      }
    }

    return null;
  }

  public void addItemFeedback(String typeId, String text, String textValue) {
    if (this.itemFeedbackSet == null) {
      setItemFeedbackSet(new HashSet());
    }
    this.itemFeedbackSet.add(new PublishedItemFeedback(this, typeId, text, textValue));
  }

  public void removeFeedbackByType(String typeId) {
    if (itemFeedbackSet != null) {
      for (Iterator i = this.itemFeedbackSet.iterator(); i.hasNext(); ) {
        PublishedItemFeedback itemFeedback = (PublishedItemFeedback) i.next();
        if (itemFeedback.getTypeId().equals(typeId)) {
          i.remove();
        }
      }
    }
  }

  public void updateFeedbackByType(String typeId, String text, String value) {
    if (itemFeedbackSet != null) {
      for (Iterator i = this.itemFeedbackSet.iterator(); i.hasNext(); ) {
        PublishedItemFeedback itemFeedback = (PublishedItemFeedback) i.next();
        if (itemFeedback.getTypeId().equals(typeId)) {
            itemFeedback.setText(text);
            itemFeedback.setTextValue(value);
        }
      }
    }
  }

  public void removeMetaDataByType(String label) {
   try {
    if (itemMetaDataSet!= null) {
      for (Iterator i = this.itemMetaDataSet.iterator(); i.hasNext(); ) {
        PublishedItemMetaData itemMetaData= (PublishedItemMetaData) i.next();
        if (itemMetaData.getLabel().equals(label)) {
          //this.itemMetaDataSet.remove(itemMetaData);
          i.remove();
        } 
      } 
    } 
  }
  catch (Exception e) {
      log.error(e.getMessage(), e);
  }
}

  /**
  * If this is a true-false question return true if it is true, else false.
  * If it is not a true-false question return false.
  * @return true if this is a true true-false question
  */
   public Boolean getIsTrue() {
     // if not true false, done.
     if (!this.getTypeId().equals(TypeD.TRUE_FALSE))
     {
       return Boolean.FALSE;
     }

     Set answerSet = null;

     Set set = this.getItemTextSet();
     Iterator iter = set.iterator();
     if (iter.hasNext())
     {
       answerSet = ( (ItemTextIfc) iter.next()).getAnswerSet();
     }

     // if the FIRST answer is CORRECT, the true false question is TRUE
     // Note that this is implementation dependent
     if (answerSet != null)
     {
       Iterator aiter = answerSet.iterator();
       if (aiter.hasNext())
       {
         AnswerIfc answer = (AnswerIfc) aiter.next();
         return answer.getIsCorrect();
       }
     }

     return Boolean.FALSE;

   }

   /**
  * In the case of an ordinary question, this will obtain the a set of text with
  * one element and return it; in FIB return multiple elements separated by underscores.
  * @return text of question
  */
   public String getText() {
     String text = "";
     if (getTypeId().equals(TypeIfc.MATCHING) 
             || getTypeId().equals(TypeIfc.CALCULATED_QUESTION)
             || getTypeId().equals(TypeIfc.IMAGEMAP_QUESTION)
             || getTypeId().equals(TypeIfc.MATRIX_CHOICES_SURVEY))
         return instruction;
     Set set = this.getItemTextSet();
     Iterator iter = set.iterator();

     while (iter.hasNext())
     {
       ItemTextIfc itemText = (ItemTextIfc) iter.next();
       
       //if EMI use only the first textItem's text for display (seqence = 0)
       if (this.getTypeId().equals(TypeIfc.EXTENDED_MATCHING_ITEMS)) {
    	   if (!itemText.getSequence().equals(Long.valueOf(0))) {
    		   continue;
    	   }
    	   else {
    		   text = itemText.getText();
    		   break;
    	   }
       }

       text += "" + itemText.getText(); //each text add it in

       if (this.getTypeId().equals(TypeIfc.FILL_IN_BLANK))
       { //e.g. Roses are {}. Violets are {}. replace as
    	   // Roses are ____. Violets are ____.
    	   String markers_pair = StringEscapeUtils.unescapeHtml4(this.getItemMetaDataByLabel("MARKERS_PAIR"));
    	   if ((StringUtils.isEmpty(markers_pair)) || markers_pair.length() != 2) {
    		   markers_pair = "{}";
    	   }
        text = text.replaceAll(Pattern.quote("" + markers_pair.charAt(0)), "__");
        text = text.replaceAll(Pattern.quote("" + markers_pair.charAt(1)), "__");
      }
       if (this.getTypeId().equals(TypeIfc.FILL_IN_NUMERIC))
       { //e.g. Roses are {}. Violets are {}. replace as
         // Roses are ____. Violets are ____.
         text = text.replaceAll("\\{","__");
         text = text.replaceAll("\\}","__");
      }
    }
    return text;
   }

   public TypeIfc getType() {
       /*
     TypeFacadeQueriesAPI typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
     TypeIfc type = typeFacadeQueries.getTypeFacadeById(this.typeId);
     TypeD typeD = new TypeD(type.getAuthority(), type.getDomain(),
                     type.getKeyword(), type.getDescription());
     typeD.setTypeId(this.typeId);
     return typeD;
       */
       return null;
   }

   public ArrayList getItemTextArray() {
     ArrayList list = new ArrayList();
     Iterator iter = itemTextSet.iterator();
     while (iter.hasNext()){
       list.add(iter.next());
     }
     return list;
   }

   public ArrayList getItemTextArraySorted() {
     ArrayList list = getItemTextArray();
     Collections.sort(list);
     return list;
   }

   public void setTriesAllowed(Integer triesAllowed) {
     this.triesAllowed = triesAllowed;
   }

   public Integer getTriesAllowed() {
     return this.triesAllowed;
   }

   /**
    * This method return the answerKey for a matching question
    * e.g. A:2, B:3, C:1, D:4 (where A, B & C is the answer label and 1,2 &3
    * are the itemText sequence
    */
   public String getAnswerKey() {
		String answerKey = "";
		ArrayList<ItemTextIfc> itemTextArray = getItemTextArraySorted();
		if (itemTextArray.size() == 0)
			return answerKey;

		if (this.getTypeId().equals(TypeD.EXTENDED_MATCHING_ITEMS)) {
			Iterator itemTextIter = itemTextArray.iterator();
			while (itemTextIter.hasNext()) {
				ItemTextIfc itemText = (ItemTextIfc) itemTextIter.next();
				if (itemText.isEmiQuestionItemText()) {
				   answerKey += itemText.getSequence() + ":";
				   List emiItems = itemText.getAnswerArraySorted();
				   Iterator emiItemsIter = emiItems.iterator();
				   while (emiItemsIter.hasNext()) {
					   AnswerIfc answer = (AnswerIfc)emiItemsIter.next();
					   if (answer.getIsCorrect()) {
						   answerKey += answer.getLabel();
					   }
				   }
				   answerKey += " ";
				}
			}
			return answerKey;
		} else if (typeId.equals(TypeD.MATCHING)) {

			List<String> answerKeys = new ArrayList<>(itemTextArray.size());
			for (ItemTextIfc question : itemTextArray) {
				boolean isDistractor = true;

				List<AnswerIfc> answersSorted = question.getAnswerArraySorted();
				for (AnswerIfc answer : answersSorted) {
					if (!getPartialCreditFlag() && answer.getIsCorrect()) {
						answerKeys.add(question.getSequence() + ":" + answer.getLabel());
						isDistractor = false;
						break;
					}
				}

				if (isDistractor) {
					answerKeys.add(
						question.getSequence()
							+ ":"
							+ rb.getString("choice_labels").split(":")[answersSorted.size()]);
				}
			}

			answerKey = StringUtils.join(answerKeys, ", ");
			return answerKey;
		}

		for (int i = 0; i < itemTextArray.size(); i++) {
			ItemTextIfc text = (ItemTextIfc) itemTextArray.get(i);
			List answers = text.getAnswerArraySorted();
			for (int j = 0; j < answers.size(); j++) {
				AnswerIfc a = (AnswerIfc) answers.get(j);
				if (!this.getPartialCreditFlag() && (Boolean.TRUE).equals(a.getIsCorrect())) {
					if (!this.getTypeId().equals(TypeD.MATCHING)) {
						if (this.getTypeId().equals(TypeD.TRUE_FALSE)) {
							answerKey = a.getText();
						} else if (TypeD.CALCULATED_QUESTION.equals(this.getTypeId())) {
							if (StringUtils.isEmpty(answerKey)) {
								answerKey = a.getLabel() +" = "+ a.getText().substring(0, a.getText().lastIndexOf("|")) ;
							} else {
								answerKey += ":split:" + a.getLabel() +" = "+ a.getText().substring(0, a.getText().lastIndexOf("|")) ;
							}
						} else {
							if (("").equals(answerKey)) {
								answerKey = a.getLabel();
							} else {
								answerKey += "," + a.getLabel();
							}
						}
					}
				}
				//multiple choice partial credit:
				if (this.getTypeId().equals(TypeD.MULTIPLE_CHOICE) && this.getPartialCreditFlag()){
					Double pc =  Double.valueOf(a.getPartialCredit()); //--mustansar
					if (pc == null) {
						pc = Double.valueOf(0d);
					}
					if(pc > 0){
						String correct = rb.getString("correct");
						if(("").equals(answerKey)){
							answerKey = a.getLabel() + "&nbsp;<span style='color: green'>(" + pc + "%&nbsp;" + correct + ")</span>";
						}else{
							answerKey += ",&nbsp;" + a.getLabel() + "&nbsp;<span style='color: green'>(" + pc + "%&nbsp;" + correct + ")</span>";
						}
					}
				}
			}
		}

		return answerKey;
	}

  public int compareTo(ItemDataIfc o) {
      return sequence.compareTo(o.getSequence());
  }

    /*
  public ItemGradingData getLastItemGradingDataByAgent(){
    GradingService service = new GradingService();
    ItemGradingData i= service.getLastItemGradingDataByAgent(
        this.itemId.toString(), AgentFacade.getAgentString());
    return i;
  }

  public ItemGradingData getLastItemGradingDataByGivenAgent(String agentId){
    GradingService service = new GradingService();
    ItemGradingData i= service.getLastItemGradingDataByAgent(
        this.itemId.toString(), "jon");
    return i;
  }
    */

    public boolean getGeneralItemFbIsNotEmpty() {
		return isNotEmpty(getGeneralItemFeedback());
	}

	public boolean getCorrectItemFbIsNotEmpty() {
		return isNotEmpty(getCorrectItemFeedback());
	}

	public boolean getIncorrectItemFbIsNotEmpty() {
		return isNotEmpty(getInCorrectItemFeedback());
	}

	public boolean isNotEmpty(String wyzText) {

		if (wyzText != null) {
			int index = 0;
			String t = (wyzText.replaceAll("<.*?>", " ")).trim();
			while (index < t.length()) {
				char c = t.charAt(index);
				if (Character.isLetterOrDigit(c)) {
					return true;
				}
				index++;
			}
		}
		return false;
	}

  public Set getItemAttachmentSet() {
    return itemAttachmentSet;
  }

  public void setItemAttachmentSet(Set itemAttachmentSet) {
    this.itemAttachmentSet = itemAttachmentSet;
  }

  public List<ItemAttachmentIfc> getItemAttachmentList() {
    if ( this.itemAttachmentSet == null || this.itemAttachmentSet.isEmpty() ) {
      return new ArrayList<>();
    }
    return new ArrayList<>(this.itemAttachmentSet);
  }

  public Map<Long, ItemAttachmentIfc> getItemAttachmentMap() {
    final Map<Long, ItemAttachmentIfc> map = new HashMap<>();
    if ( this.itemAttachmentSet == null || this.itemAttachmentSet.isEmpty() ) {
      return map;
    }
    for (ItemAttachmentIfc a : this.itemAttachmentSet) {
      map.put(a.getAttachmentId(), a);
    }
    return map;
  }

  public void addItemAttachment(ItemAttachmentIfc attachment) {
    if ( attachment == null ) {
      return;
    }
    if ( this.itemAttachmentSet == null ) {
      this.itemAttachmentSet = new HashSet<>();
    }
    attachment.setItem(this);
    this.itemAttachmentSet.add(attachment);
  }

  public void removeItemAttachmentById(Long attachmentId) {
    if ( attachmentId == null ) {
      return;
    }
    if ( this.itemAttachmentSet == null || this.itemAttachmentSet.isEmpty() ) {
      return;
    }
    Iterator i = this.itemAttachmentSet.iterator();
    while ( i.hasNext() ) {
      final ItemAttachmentIfc a = (ItemAttachmentIfc)i.next();
      if ( attachmentId.equals(a.getAttachmentId()) ) {
        i.remove();
        a.setItem(null);
      }
    }
  }

  public void removeItemAttachment(ItemAttachmentIfc attachment) {
    if ( attachment == null ) {
      return;
    }
    attachment.setItem(null);
    if ( this.itemAttachmentSet == null || this.itemAttachmentSet.isEmpty() ) {
      return;
    }
    this.itemAttachmentSet.remove(attachment);
  }
  
  
  public Boolean getPartialCreditFlag() {
	  if (partialCreditFlag == null) {
		  return Boolean.FALSE;
	  }
	  return partialCreditFlag;
  }

  public void setPartialCreditFlag(Boolean partialCreditFlag) {
	  this.partialCreditFlag = partialCreditFlag;
  }

  public String getLeadInText() {
	if (leadInText == null) {
		setThemeAndLeadInText();
	}
	return leadInText;
  }

  public String getThemeText() {
	if (themeText == null) {
		setThemeAndLeadInText();
	}
	return themeText;
  }

  private void setThemeAndLeadInText() {
		if (TypeD.EXTENDED_MATCHING_ITEMS.equals(getTypeId())) {
			boolean themeTextIsSet = false, leadInTextIsSet = false;
			Iterator iter = itemTextSet.iterator();
			while (iter.hasNext()) {
				ItemTextIfc itemText= (ItemTextIfc) iter.next();
				if (itemText.getSequence().equals(ItemTextIfc.EMI_THEME_TEXT_SEQUENCE)) {
					themeText = itemText.getText();
					themeTextIsSet = true;
				}
				if (itemText.getSequence().equals(ItemTextIfc.EMI_LEAD_IN_TEXT_SEQUENCE)) {
					leadInText = itemText.getText();
					leadInTextIsSet = true;
				}
				
				if (themeTextIsSet && leadInTextIsSet) {
					return;
				}
			}
		}
	  }

	public int getNumberOfCorrectEmiOptions() {
		int count=0;
		Iterator itemTextIter = itemTextSet.iterator();
		while (itemTextIter.hasNext()) {
			ItemTextIfc itemText = (ItemTextIfc)itemTextIter.next();
			if (!itemText.isEmiQuestionItemText()) continue;
			Iterator answerIter = itemText.getAnswerSet().iterator();
			while (answerIter.hasNext()) {
				AnswerIfc answer = (AnswerIfc) answerIter.next();
				if (answer.getIsCorrect()) count++;
			}
		}
		return count;
	}
  
	//available option labels for EMI answers
	public String getEmiAnswerOptionLabels() {
		String emiAnswerOptionLabels = null;
		if (TypeD.EXTENDED_MATCHING_ITEMS.equals(getTypeId())) {
			if (getIsAnswerOptionsSimple()) {
				emiAnswerOptionLabels = "";
				Iterator iter = getEmiAnswerOptions().iterator();
				while (iter.hasNext()) {
					AnswerIfc answer = (AnswerIfc) iter.next();
					emiAnswerOptionLabels += answer.getLabel();
				}
			}
			else { // Rich
				emiAnswerOptionLabels = ItemDataIfc.ANSWER_OPTION_LABELS.substring(0, getAnswerOptionsRichCount().intValue());
			}
		}
		return emiAnswerOptionLabels;
	}
	
	public boolean isValidEmiAnswerOptionLabel(String label) {
		if (label == null) return false;
		String validOptionLabels = getEmiAnswerOptionLabels();
		if (label.length()==1 && validOptionLabels.contains(label)) {
			return true;
		}
		return false;
	}
	
	  public List getEmiAnswerOptions() {
		  if (!typeId.equals(TypeD.EXTENDED_MATCHING_ITEMS)) return null;
		  ItemTextIfc itemText = getItemTextBySequence(ItemTextIfc.EMI_ANSWER_OPTIONS_SEQUENCE);  
		  if (itemText != null) {
			    return itemText.getAnswerArraySorted();
		  }
		  return null;
	  }
	  
	  public ArrayList getEmiQuestionAnswerCombinations() {
		  if (!typeId.equals(TypeD.EXTENDED_MATCHING_ITEMS)) return null;
		  Iterator iter = getItemTextArraySorted().iterator();
		  ArrayList emiQuestionAnswerCombinations = new ArrayList();
		  while (iter.hasNext()) {
			  ItemTextIfc itemText = (ItemTextIfc)iter.next();
			  if (itemText.isEmiQuestionItemText()) {
				  emiQuestionAnswerCombinations.add(itemText);
			  }
		  }
		  return emiQuestionAnswerCombinations;
	  }
	  
	  public ItemTextIfc getItemTextBySequence(Long itemTextSequence) {
		  ItemTextIfc itemText = null;  
		  Iterator itemTextIter = itemTextSet.iterator();
		  while (itemTextIter.hasNext()) {
			  itemText = (ItemTextIfc) itemTextIter.next();
			  if (itemText.getSequence().equals(itemTextSequence)) {
				  return itemText;
			  }
		  }
		  return null;
	  }
	  
		public Integer getAnswerOptionsRichCount() {
			return answerOptionsRichCount;
		}

		public void setAnswerOptionsRichCount(Integer answerOptionsRichCount) {
			this.answerOptionsRichCount = answerOptionsRichCount;
		}	  
		  
		public Integer getAnswerOptionsSimpleOrRich() {
			return answerOptionsSimpleOrRich;
		}

		public void setAnswerOptionsSimpleOrRich(Integer answerOptionsSimpleOrRich) {
			this.answerOptionsSimpleOrRich = answerOptionsSimpleOrRich;
		}
		
		  public String getEmiAnswerOptionsRichText() {
			  if (!typeId.equals(TypeD.EXTENDED_MATCHING_ITEMS)) return null;
			  ItemTextIfc itemText = getItemTextBySequence(ItemTextIfc.EMI_ANSWER_OPTIONS_SEQUENCE);  
			  if (itemText != null) {
				    return itemText.getText();
			  }
			  return null;
		  }

		  public boolean getIsAnswerOptionsSimple() {
			  return this.getAnswerOptionsSimpleOrRich().equals(ItemDataIfc.ANSWER_OPTIONS_SIMPLE);
		  }

		  public boolean getIsAnswerOptionsRich() {
			  return this.getAnswerOptionsSimpleOrRich().equals(ItemDataIfc.ANSWER_OPTIONS_RICH);
		  }

  public String[] getRowChoices(){

	  ArrayList itemTextArray = getItemTextArraySorted();

	  List<String> stringList = new ArrayList<String>();

	  for(int i=0; i<itemTextArray.size();i++) {
		  String str = ((ItemTextIfc) itemTextArray.get(i)).getText();
		  if(str!= null && str.trim().length() > 0) {
			  stringList.add(str);
		  }
	  }

	  String [] rowChoices = stringList.toArray(new String[stringList.size()]);

	  return rowChoices;	 
  }
  
  public List<Integer> getColumnIndexList() {

	  List<Integer> columnIndexList = new ArrayList<Integer>();
	  List itemTextArray = getItemTextArraySorted();
	  List answerArray = ((ItemTextIfc)itemTextArray.get(0)).getAnswerArraySorted();  
	  List<String> stringList = new ArrayList<String>();

	  for(int i=0; i<answerArray.size();i++) {
		  String str = ((AnswerIfc) answerArray.get(i)).getText();
		  if(str!= null && str.trim().length() > 0) {
			  stringList.add(str);
		  }
	  }
	  for (int k=0; k< stringList.size(); k++){
		  columnIndexList.add(Integer.valueOf(k));
	  }
	  return columnIndexList;
  }

  public String[] getColumnChoices() {
	  List itemTextArray = getItemTextArraySorted();
	  List answerArray = ((ItemTextIfc)itemTextArray.get(0)).getAnswerArraySorted();   
	  List<String> stringList = new ArrayList<String>();

	  for(int i=0; i<answerArray.size();i++) {
		  String str = ((AnswerIfc) answerArray.get(i)).getText();
		  if(str!= null && str.trim().length() > 0) {
			  stringList.add(str);
		  }
	  }
	  String [] columnChoices = stringList.toArray(new String[stringList.size()]);

	  return columnChoices;

  }

  public boolean getAddCommentFlag(){
	  if (getItemMetaDataByLabel(ItemMetaDataIfc.ADD_COMMENT_MATRIX) != null)
		  return Boolean.parseBoolean(getItemMetaDataByLabel(ItemMetaDataIfc.ADD_COMMENT_MATRIX));
	  return false;
  }

  public String getCommentField(){
	  if (getItemMetaDataByLabel(ItemMetaDataIfc.ADD_COMMENT_MATRIX) != null && getItemMetaDataByLabel(ItemMetaDataIfc.ADD_COMMENT_MATRIX).equalsIgnoreCase("true"))
		  return (String)(getItemMetaDataByLabel(ItemMetaDataIfc.MX_SURVEY_QUESTION_COMMENTFIELD));
	  return null;
  }

  public String getRelativeWidthStyle() {
	  String width = (String)(getItemMetaDataByLabel(ItemMetaDataIfc.MX_SURVEY_RELATIVE_WIDTH));
	  if (width != null && Integer.valueOf(width) != 0)
		  return "width:" + width + "%";
	  else
		  return "";
  }
  public String getImageMapSrc() {
	  return getItemMetaDataByLabel(ItemMetaDataIfc.IMAGE_MAP_SRC);
  }

  public String getImageMapAltText() {
      return getItemMetaDataByLabel(ItemMetaDataIfc.IMAGE_MAP_ALT_TEXT);
  }

 public Double getMinScore() {
         return minScore;
 }
	  	 
 public void setMinScore(Double minScore) {
         this.minScore = minScore;
 }

  private String convertTagListToJsonString(Set<ItemTagIfc> itemTagSet) {

    String tagsListToJson = "[";
    if (itemTagSet != null) {
      Iterator<ItemTagIfc> i = itemTagSet.iterator();
      Boolean more = false;
      while (i.hasNext()) {
        if (more) {
          tagsListToJson += ",";
        }
        ItemTagIfc tagToShow = (ItemTagIfc) i.next();
        String tagId = tagToShow.getTagId();
        String tagLabel = tagToShow.getTagLabel();
        String tagCollectionName = tagToShow.getTagCollectionName();
        tagsListToJson += "{\"tagId\":\"" + tagId + "\",\"tagLabel\":\"" + tagLabel + "\",\"tagCollectionName\":\"" + tagCollectionName + "\"}";
        more = true;
      }
    }
    tagsListToJson += "]";
    return tagsListToJson;
  }

  public String getTagListToJsonString() {
    return convertTagListToJsonString(itemTagSet);
  }

  public Set<ItemHistoricalIfc> getItemHistoricalSet() {
    // Do nothing
    return new HashSet<>();
  }

  public void setItemHistoricalSet(Set<ItemHistoricalIfc> itemMetaDataSet) {
    // Do nothing
  }

  public void addItemHistorical(String modifiedBy, Date modifiedDate) {
    // Do nothing
  }

}
