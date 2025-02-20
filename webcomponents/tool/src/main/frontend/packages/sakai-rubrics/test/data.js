export const i18nUrl = /getI18nProperties.*rubrics$/;

export const i18n = `
grading_rubric=Grading Rubric
rubric=rubric
criterion=criterion
rating=rating
rubric_title=Rubric Title
manage_rubrics=Manage Rubrics
add_rubric=Add Rubric
toggle_details=Toggle details for
edit_rubric=Edit Rubric
edit_criterion=Edit Criterion
edit_rating=Edit Rating
copy=Copy
remove=Remove {}
remove_label=Remove
remove_rating_disabled=The last rating of a criterion can not be removed.
cancel=Cancel
save=Save
done=Done
total=Total
confirm_remove=Are you sure you want to remove
confirm_remove_shared=Are you sure you want to remove {} both from shared rubrics and from its source site?
is_locked=is locked: Only titles, descriptions and criterion order can be modified
locked_warning=Rubric in use. Editable content will be updated on all previously associated items.
points=Points
weight=Weight
save_weights=Save Weights
total_weight=Total Weight:
total_weight_wrong=Weighting for the criterions must equal 100%
total_grade=Total Grade:
min_max_points=From {} to {} points
saved_successfully=Saved successfully!
add_criterion=Add Criterion
add_criterion_group=Add Criterion Group
add_rating=Add rating to
rating_title=Rating Title
rating_description=Rating Description
drag_order=Drag to reorder
drag_to_reorder_info=To reorder, click and drag the <i class=\"si si-drag-handle\" aria-hidden=\"true\"></i> icon, \
or use your keyboard to select a drag icon and the 'e' or 'd' key to move it.
criterion_title=Criterion Title
criterion_description=Criterion Description
criterion_group_title=Criterion Group Title
criterion_group_description=Criterion Group Description
preview_rubric=Preview Rubric
point_override_details=Fine tune points by entering a number and overriding the selected rating points
criterion_comment=Leave a comment about this criterion for the student
criterion_comment_student=Instructor comment about this criterion
comment_for_criterion=Comment for {}
site_rubrics=Site Rubrics
shared_rubrics=Public Rubrics
toggle_site_rubrics=Toggle Site Rubrics
toggle_shared_rubrics=Toggle Public Rubrics
copy_to_site=Copy {} to site list
site_name=Name
site_title=Origin
creator_name=Author
modified=Modified
actions=Actions
share=Make Public {}
share_label=Make Public
weighted_label=Switch to Standard Grading
standard_label=Switch to Weighted Grading
weighted_status=Weighted Rubric
draft_label=Draft
draft_turn_on=Save as Draft
draft_turn_off=Publish Rubric
draft_info=While saved as draft, the rubric won't be available from other tools.
draft_invalid_weight_publish=In order to publish the rubric:\u0020
draft_save_invalid_weights=Invalid weights can be saved, until the rubric has to be published.
draft_save_weights=\u0020- Weights will be saved.
revoke=Revoke public sharing for {}
revoke_label=Revoke
no_rubrics=No rubrics have been created.
point_value=Point value
rubric_selector_label=Rubric selector
close_dialog=Close Dialog
search_rubrics=Search Rubrics by title, site or author:
percent_sign=%
draft_evaluation=Draft rubric evaluation. {}
draft_evaluation_sakai.gradebookng=Save the grade to publish the rubric to the student.
draft_evaluation_sakai.assignment.grades=Save and release to student to publish the rubric to the student.
draft_evaluation_sakai.samigo=Update the quiz score to publish the rubric to the student.
locked_message=This tool, when locked (the default), can NOT be seen by students. Students can \
still, however, see their rubric evaluations in tools like Assignments, Gradebook and Tests and \
Quizzes. You DO NOT need to unlock this tool for students to see their rubric evaluations.
adjust_scores_warning=Deselecting the 'Adjust individual student scores' option will reset \
existing student rubric scores to the default point value for selected ratings. Are you sure?
public_rubrics_title=Publicly Shared Rubrics
public_rubrics_info=Rubrics in this section were created by instructors and shared publicly with all users. You can share a rubric publicly by clicking on the "Globe" icon next to the rubric you would like to share. To use one of these rubrics in your course site, expand the "Public Rubrics" section and click on the "Copy" icon located in the "Actions" column.

# Rubrics in pdf
export_title=Export {}
export_rubric_title=Exported Rubric Title: {0}
export_rubric_student=Student Name: {0}
export_rubric_site=Rubric exported from the site: {0} 
export_rubric_date=Rubric exported on: {0}
export_total_points=Total points: {0}
export_rubrics_points={0} : {1} points\n
export_rubrics_weight={0} : {1} points ({2}%)\n
export_comments=Comments: {0} 
export_label=Export to pdf

default_rubric_title=New Rubric
default_criterion1_title=Criterion 1
default_c1_r1_title=Inadequate
default_c1_r2_title=Meets expectations
default_c1_r3_title=Exceeds expectations
default_criterion2_title=Criterion 2
default_c2_r1_title=Inadequate
default_c2_r2_title=Poor
default_c2_r3_title=Fair
default_c2_r4_title=Good
default_c2_r5_title=Exceptional
default_criterion_title=New Criterion
default_empty_criterion_title=New Criterion Group
default_rating_title=New Rating
featured_rubrics_title=Featured Rubrics
featured_rubrics_info=Rubrics in this section are templates featured by the Office of eLearning and can be used in your course site. To use one of these rubrics in your Isidore site, expand the "Feature Rubrics" section and click on the "Copy" icon located in the "Actions" column.
criteria_summary=Criteria Summary
no_evaluations_warning=No students have been evaluated yet.
criterion2=Criterion
average=Average
median=Median
stdev=Standard Deviation
student_summary=Student Summary
score=Score
student_name=Student Name
adjusted_score=Adjusted Scores*
adjusted_score_warning=* Scores in this section were entered manually and do not correspond to any rating. They are positioned in the tables based on where they fall among the existing ratings' point values.
expand_all=Expand All
collapse_all=Collapse All
rubric_points_warning=A rubric's point value should match the maximum point value of the activity or question to grade.
`;

export const siteId = "xyz";
export const toolId = "sakai.samigo";
export const entityId = "entity1";
export const evaluatedItemId = "evaluatedItem1";

export const sharedRubricsUrl = "/api/rubrics/shared";
export const sharedRubrics = [
];

export const siteTitle = "XYZ Site";
export const userId = "adrian";

export const ownerId = userId;
export const creatorDisplayName = "User 1";
export const formattedModifiedDate = "7 Feb 1971";

export const criterion1 = {
  id: 1,
  title: "Space",
  description: "Is the place",
  ratings: [
    {
      id: 1,
      title: "Poor",
      description: "A poor performance",
      points: 1,
    },
    {
      id: 2,
      title: "Not bad",
      description: "A barely adequate performance",
      points: 2,
    },
  ],
  pointoverride: "1.2",
};

export const criteria1 = [
  criterion1,
  {
    id: 2,
    title: "Group 1",
    description: "Groups 1 group",
    ratings: [
    ],
  },
];

export const criteria2 = [
  {
    id: 3,
    title: "Ocean",
    description: "Is the graveyard of hubris",
    ratings: [
      {
        id: 3,
        title: "Poor",
        description: "A poor performance",
        points: 1,
      },
      {
        id: 4,
        title: "Crap",
        description: "Crapish",
        points: 2,
      },
    ],
    pointoverride: "1.4",
  },
  {
    id: 4,
    title: "Group 2",
    description: "Groups 2 group",
    ratings: [
    ],
  },
];

export const criteria3 = [
  {
    id: 5,
    title: "C1",
    description: "First criterion",
    ratings: [
      {
        id: 5,
        title: "Rating1",
        description: "First rating",
        points: 1,
      },
      {
        id: 6,
        title: "Rating2",
        description: "Second rating",
        points: 2,
      },
    ]
  },
  {
    id: 6,
    title: "C2",
    description: "Second criterion",
    ratings: [
      {
        id: 7,
        title: "Rating1",
        description: "First rating",
        points: 1,
      },
      {
        id: 8,
        title: "Rating2",
        description: "Second rating",
        points: 2,
      },
    ],
  },
];

export const rubric1 = {
  id: "1",
  title: "Rubric 1",
  ownerId,
  siteTitle,
  locked: true,
  creatorDisplayName,
  formattedModifiedDate,
  criteria: criteria1,
};

export const rubric2 = {
  id: "2",
  title: "Rubric 2",
  ownerId,
  siteTitle,
  creatorDisplayName,
  formattedModifiedDate,
  criteria: criteria2,
};

export const rubric3 = {
  id: "3",
  title: "Rubric 3",
  ownerId,
  siteTitle,
  creatorDisplayName,
  formattedModifiedDate,
  criteria: criteria1,
  locked: true
};

export const rubric4 = {
  id: "4",
  title: "Rubric 4",
  ownerId,
  siteTitle,
  creatorDisplayName,
  formattedModifiedDate,
  criteria: criteria3
};

export const evaluatedItemOwnerId = "fisha";

export const rubricsUrl = /api\/sites\/xyz\/rubrics[\?\w=]*$/;
export const rubrics = [ rubric1, rubric2 ];

export const rubric1Url = `/api/sites/${siteId}/rubrics/${rubric1.id}`;
export const rubric1OwnerUrl = `/api/sites/${ownerId}/rubrics/${rubric1.id}`;
export const rubric3OwnerUrl = `/api/sites/${ownerId}/rubrics/${rubric3.id}`;

export const associationUrl = `/api/sites/${siteId}/rubric-associations/tools/${toolId}/items/${entityId}`;

export const association = {
  rubricId: rubric1.id,
  siteId: siteId,
  parameters: {
    fineTunePoints: true,
  },
};

export const evaluationUrl = `/api/sites/${siteId}/rubric-evaluations/tools/${toolId}/items/${entityId}/evaluations/${evaluatedItemId}/owners/${evaluatedItemOwnerId}`;

export const evaluation = {
  criterionOutcomes: [
    { criterionId: 1, selectedRatingId: 2, comments: "Rubbish", points: 2 }
  ],
};

export const rubric4OwnerUrl = `/api/sites/${ownerId}/rubrics/${rubric4.id}`;
export const rubric4CriteriaSortUrl = `/api/sites/${ownerId}/rubrics/${rubric4.id}/criteria/sort`;
export const rubric4Criteria5Url = `/api/sites/${ownerId}/rubrics/${rubric4.id}/criteria/5`;
export const rubric4Criteria6Url = `/api/sites/${ownerId}/rubrics/${rubric4.id}/criteria/6`;
