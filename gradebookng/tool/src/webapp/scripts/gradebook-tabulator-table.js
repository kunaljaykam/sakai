
GbGradeTable.tbrHeaderRenderer = function (colValue, headerKey, column) {
    // if (colValue < GbGradeTable.getTbrFixedColumns().length) {
      return function (col, header) {
        try {
          const template = GbGradeTable.templates[headerKey];
          const column = { col: colValue, ...col };
          return template.process({ col: column, settings: GbGradeTable.settings });
        } catch (error) {
          console.error("Error in tbrHeaderRenderer:", error);
          return `<div class="header-error">Error rendering header</div>`;
        }
      };
    // }
  
    // var hasAssociatedRubric = column.type === "assignment" ? column.hasAssociatedRubric : false;
  
    // var templateData = $.extend({
    //   col: col,
    //   settings: GbGradeTable.settings,
    //   hasAssociatedRubric: hasAssociatedRubric,
    // }, column);
  
    // const cleanedTitle = templateData.title.replace(/"/g, '&quot;');
  
    // if (column.type === "assignment") {
    //   templateData.tooltip = GbGradeTable.i18n["label.gradeitem.assignmentheadertooltip"].replace("{0}", cleanedTitle);
    //   return GbGradeTable.templates.assignmentHeader.process(templateData);
    // } else if (column.type === "category") {
    //   templateData.tooltip = GbGradeTable.i18n["label.gradeitem.categoryheadertooltip"].replace("{0}", cleanedTitle);
    //   $th.addClass("gb-item-category");
    //   return GbGradeTable.templates.categoryScoreHeader.process(templateData);
    // } else {
    //   return "Unknown column type for column: " + col + " (" + column.type+ ")";
    // }
  };
  

  GbGradeTable.tbrStudentCellRenderer = function(cell, formatterParams, onRendered) {
    var rowIndex = cell.getRow().getIndex(); // Get the row index
    var studentData = GbGradeTable.students[rowIndex]; // Fetch the student data based on the row index
  
    if (!studentData) {
      console.error("No student data found for row index:", rowIndex);
      return ''; // Return an empty string if no data found
    }
  
    var cellElement = cell.getElement();
    var cellKey = `${rowIndex}_${cell.getColumn().getIndex()}`;
  
    if (!(cellElement instanceof Element)) {
      console.error("Invalid cell element:", cellElement);
      return '';
    }
  
    var data = $.extend({
      settings: GbGradeTable.settings
    }, studentData);
  
    var html = GbGradeTable.templates.studentCell.setHTML(cellElement, data);
  
    $(cellElement).attr("scope", "row").attr("role", "rowHeader");
    $.data(cellElement, 'cell-initialised', cellKey);
    $.data(cellElement, "studentid", studentData.userId);
    $.data(cellElement, "metadata", {
      id: cellKey,
      student: studentData
    });
  
    $(cellElement).removeAttr('aria-describedby');
  
    return cellElement.innerHTML; // Return the generated HTML
  };
  


  

GbGradeTable.renderTabulatorTable = function(elementId, tableDataTabulator) {

  GbGradeTable.students = tableDataTabulator.students;
  GbGradeTable.columns = tableDataTabulator.columns;
  let hiddenItems = JSON.parse(sessionStorage.getItem(GB_HIDDEN_ITEMS_KEY)) || [];
  GbGradeTable.columns.filter(c => hiddenItems.includes(c.assignmentId)).forEach(c => c.hidden = true);
  GbGradeTable.settings = tableDataTabulator.settings;
  GbGradeTable.courseGradeId = tableDataTabulator.courseGradeId;
  GbGradeTable.gradebookId = tableDataTabulator.gradebookId;
  GbGradeTable.i18n = tableDataTabulator.i18n;  



  // Custom formatter function
function customFormatter(cell, formatterParams, onRendered) {
  var cellValue = cell.getValue();

  if (Array.isArray(cellValue)) {
      // Handle array data, assuming you want to join array elements with a comma
      return cellValue.join(", ");
  } else if (typeof cellValue === 'object' && cellValue !== null) {
      // Handle object data, assuming you want to display it as JSON string
      return JSON.stringify(cellValue);
  } else {
      // Handle primitive data
      return cellValue;
  }
}

      // tbr_fixedColumns
  GbGradeTable.tbr_fixedColumns.push(
    {
      // title: "Student",
     formatter:function(cell, formatterParams, onRendered){
      // Inject custom HTML based on cell data
      var rowData = cell.getRow().getData();
      return "<div class='custom-content'><b>" + rowData.name + "</b> - Value: " + rowData.value + "</div>";
  },
      titleFormatter: GbGradeTable.tbrHeaderRenderer(0, 'studentHeader'),

      // width: 220,
      // sorter: function(a, b, aRow, bRow, column, dir, sorterParams) {
      //   return GbGradeTable.studentSorter(a, b);
      // }
    },
    // {
    //   title: "Course Grade",
    //   field: "0.{eid}",
    //   formatter: GbGradeTable.courseGradeRenderer,
    //   titleFormatter: GbGradeTable.tbrHeaderRenderer(1, 'courseGradeHeader'),
    //   width: 220 ,
    //   sorter: function(a, b, aRow, bRow, column, dir, sorterParams) {
    //     const a_percent = parseFloat(a);
    //     const b_percent = parseFloat(b);
    //     const aIsNaN = isNaN(a_percent);
    //     const bIsNaN = isNaN(b_percent);
  

    //     if (a_percent > b_percent || (!aIsNaN && bIsNaN)) {
    //       return 1;
    //     }
    //     if (a_percent < b_percent || (aIsNaN && !bIsNaN)) {
    //       return -1;
    //     }
    //     return 0;
    //   }
    // }
  );




  var exampleData = [
    {id: 1, name: "Item 1", value: 100},
    {id: 2, name: "Item 2", value: 200},
    // More data items...
];

  // Initialize tabulator
  GbGradeTable.newInstance = new Tabulator ("#tbr_gradeTableWrapper", {
    // data: exampleData, // Assign data to table
    // columns: [
    //     {
    //         title: "ID",
    //         field: "id",
    //     },
    //     {
    //         title: "Name",
    //     },
    //     {
    //         title: "Custom HTML",
    //         formatter: function(cell, formatterParams, onRendered){
    //             // Inject custom HTML based on cell data
    //             var rowData = cell.getRow().getData();
    //             return "<div class='custom-content'><b>" + rowData.name + "</b> - Value: " + rowData.value + "</div>";
    //         }
    //     },
    //   ],
    // data: exampleData,
    // layout: "fitColumns",
    // columns: GbGradeTable.getTbrFixedColumns(),
    // // autoColumnsDefinitions: GbGradeTable.getTbrFilteredColumns(),
    // // autoColumns: true,
    // // height: GbGradeTable.calculateIdealHeight(),
    // // width: GbGradeTable.calculateIdealWidth(),
    // resizableColumns: false,
    // resizableRows: false,
    // movableColumns: false,
    // movableRows: false,


  });



}


// Tabulator Fixed Columns Definition
GbGradeTable.tbr_fixedColumns = [];



GbGradeTable.getTbrFixedColumns = function() {
    return GbGradeTable.tbr_fixedColumns;
  };  
  
  GbGradeTable.getTbrFilteredColumns = function() {
    return GbGradeTable.getTbrFixedColumns().concat(GbGradeTable.columns.filter(function(col) {
      return !col.hidden;
    }).map(function (column) {
      var readonly = column.externallyMaintained;
      var hasAssociatedRubric = column.type === "assignment" ? column.hasAssociatedRubric : false;
      var templateData = $.extend({
        col: column, // Ensure col refers to the current column
        settings: GbGradeTable.settings,
        hasAssociatedRubric: hasAssociatedRubric,
      }, column);
  
      const cleanedTitle = templateData.title.replace(/"/g, '&quot;');
  
      // Custom title formatter logic
      var titleFormatter;
      if (column.type === "assignment") {
        templateData.tooltip = GbGradeTable.i18n["label.gradeitem.assignmentheadertooltip"].replace("{0}", cleanedTitle);
        titleFormatter = function() {
          return GbGradeTable.templates.assignmentHeader.process(templateData);
        };
      } else if (column.type === "category") {
        templateData.tooltip = GbGradeTable.i18n["label.gradeitem.categoryheadertooltip"].replace("{0}", cleanedTitle);    // todo  - add class to the header
        titleFormatter = function() {
          return GbGradeTable.templates.categoryScoreHeader.process(templateData);
        };
      } else {
        titleFormatter = function() {
          return "Unknown column type for column: " + column + " (" + column.type + ")";
        };
      }
  
      return {
        formatter: GbGradeTable.cellRenderer,
        titleFormatter: titleFormatter,
        width: 180,
      };
    }));
  };
  
  // returns the gradebook items included in this category, as AssignmentColumn objects
  GbGradeTable.itemsInCategory = function(categoryId) {
    return GbGradeTable.columns.filter(function(col) {
        return col.categoryId === categoryId && col.type === "assignment";
    });
};

// const studentNumbers = GbGradeTable.students.map(student => student.studentNumber || "");

// Retrieve the template content and ensure it's parsed correctly
// const studentNumberTemplate = $("#studentNumberCellTemplate").html().trim();

// Define the custom formatter for the student number column
function studentNumberFormatter(cell, formatterParams, onRendered) {
  // Get the row data
  const rowData = cell.getData();

  // Access the student number from the row data. Assuming the student number is at the second position.
  const studentNumber = rowData[1];

  // Replace the template placeholder with the actual student number
  const renderedTemplate = studentNumberTemplate.replace('${studentNumber}', studentNumber);

  // Set the cell content to the rendered template
  const td = cell.getElement();
  td.innerHTML = renderedTemplate;

  // Set additional attributes and data on the cell
  td.setAttribute("scope", "row");
  td.setAttribute("role", "rowHeader");

  const rowIndex = cell.getRow().getIndex();
  const colIndex = cell.getColumn().getField();

  const cellKey = rowIndex + '_' + colIndex;

  $.data(td, 'cell-initialised', cellKey);
  $.data(td, "studentid", rowData.userId);
  $.data(td, "metadata", {
    id: cellKey,
    student: rowData
  });

  // Perform any post-render actions
  if (onRendered) {
    onRendered();
  }
  return renderedTemplate;
}

// Custom formatter function
function customFormatter(cell, formatterParams, onRendered) {
  var cellValue = cell.getValue();

  if (Array.isArray(cellValue)) {
      // Handle array data, assuming you want to join array elements with a comma
      return cellValue.join(", ");
  } else if (typeof cellValue === 'object' && cellValue !== null) {
      // Handle object data, assuming you want to display it as JSON string
      return JSON.stringify(cellValue);
  } else {
      // Handle primitive data
      return cellValue;
  }
}

// Define a dummy formatter function
function dummyCellRenderer(cell, formatterParams, onRendered) {
  // Get the cell element
  const td = cell.getElement();

  // Set static content for testing
  td.innerHTML = "<div style='color: red;'>Test Content</div>";

  // Set additional attributes and data on the cell
  td.setAttribute("scope", "row");
  td.setAttribute("role", "rowHeader");

  const rowIndex = cell.getRow().getIndex();
  const colIndex = cell.getColumn().getField();

  const cellKey = rowIndex + '_' + colIndex;

  $.data(td, 'cell-initialised', cellKey);

  // Perform any post-render actions
  if (onRendered) {
    onRendered();
  }
  return td.innerHTML;
}



    // GbGradeTable.tbr_fixedColumns.splice(1, 0, {
    //     field: "0[0].eid",
    //     formatter: customFormatter,
    //     titleFormatter: GbGradeTable.tbrHeaderRenderer(1, 'studentNumberHeader'),

    //     editor: false,
    //     width: 220,
    // });