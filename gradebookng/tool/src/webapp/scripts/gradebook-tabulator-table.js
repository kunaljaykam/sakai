
// // GbGradeTable.tbrStudentCellFormatter = function (cell, formatterParams, onRendered) {
// //     // console.log("Student Cell is called"); // Debugging log
// //     var value = cell.getValue();
// //     if (value === null) {
// //       return "";
// //     }
  
// //     // Create a temporary div element to hold our HTML
// //     var div = document.createElement('div');
    
// //     // Set attributes
// //     div.setAttribute("scope", "row");
// //     div.setAttribute("role", "rowHeader");
  
// //     var row = cell.getRow().getPosition();
// //     var col = cell.getColumn().getField();
// //     var cellKey = row + '_' + col;
  
// //     var data = Object.assign({
// //       settings: GbGradeTable.settings       // this wouldn't work.
// //     }, value);
// //     // console.log("Student Cell Data:",value); // Debugging log
  
// //     // Assuming GbGradeTable.templates.studentCell is a function that returns HTML
// //     var html = GbGradeTable.tbrTemplates.studentCell.setHTML(data);
  
// //     div.innerHTML = html;
  
// //     // Store data on the cell element
// //     onRendered(function() {
// //       var element = cell.getElement();
// //       element.setAttribute('data-cell-initialised', cellKey);
// //       element.setAttribute('data-studentid', value.userId);
// //       element.setAttribute('data-metadata', JSON.stringify({
// //         id: cellKey,
// //         student: value
// //       }));
// //       element.removeAttribute('aria-describedby');
// //     });
  
// //     return div;
// //   }


// //   GbGradeTable.tbrstudentNumberCellFormatter = function(cell, formatterParams, onRendered) {

// //     console.log("Student Number Cell is called"); // Debugging log
// //     // Get the cell value and other properties
// //     var value = cell.getValue();
// //     var row = cell.getRow().getIndex();
// //     var col = cell.getColumn().getField();
  
// //     if (value === null) {
// //         return;
// //     }
  
// //     var cellElement = cell.getElement();
  
// //     $(cellElement).attr("scope", "row").attr("role", "rowHeader");
  
// //     var cellKey = (row + '_' + col);
  
// //     var data = {
// //         settings: GbGradeTable.settings,
// //         studentNumber: value
// //     };
  
// //     var html = GbGradeTable.tbrTemplates.studentNumberCell.setHTML(cellElement, data);
  
// //     $.data(cellElement, 'cell-initialised', cellKey);
// //     $.data(cellElement, "studentid", value.userId);
// //     $.data(cellElement, "metadata", {
// //         id: cellKey,
// //         student: value
// //     });
  
// //     $(cellElement).removeAttr('aria-describedby');
  
// //     // Return the HTML content
// //     // console.log("Student Number Cell HTML:", html); // Debugging log
// //     return html;
// //   };

// // GbGradeTable.tbrHeaderRenderer = function (col, column, $th) {
// //     // console.log("Header Renderer is called"); // Debugging log

// //     // Get the column type

// // GbGradeTable.titleHeaderFormatter = function (column) {
// //     // console.log("Title Header Formatter is called"); // Debugging log
    
// //     var $th = document.createElement('th');
// //     var headerHtml = GbGradeTable.headerRenderer(column, $th);
// //     var headerDiv = document.createElement("div");
// //     headerDiv.innerHTML = headerHtml;
// //     return headerDiv.innerHTML;
// //   }

 
// // GbGradeTable.fixedColumnHeader

  

//   GbGradeTable.renderTableTabulator = function (elementId, tableData) {


//     // console.log("student header ", GbGradeTable.templates.studentHeader);
  
//     GbGradeTable.tbrFixedColumns = [
//       // Student name column
//       {
//         titleFormatter: headerRenderer(0, 'studentHeader'),
//         field: "studentName",
//         formatter: GbGradeTable.studentCellRenderer,
//         editor: false,
//         width: 220,
//       },
//       // Student number column
//       {
//         // formatter: GbGradeTable.tbrstudentNumberCellFormatter,
    
//         titleFormatter: headerRenderer(1, 'studentNumberHeader'),
//         field: "studentNumber",
//         formatter: GbGradeTable.tbrstudentNumberCellFormatter,
//         editor: false,
//         width: studentNumberColumnWidth,
//       },
//       // Course grade column
//       {
//         // titleFormatter: function() {
//         //   var col = 2; // Adjust the column index as necessary
//         //   // var column = {
//         //   //   type: "coursegrade", // Example type, adjust as necessary
//         //   //   title: "Course Grade"
//         //   // };
//         //   var $th = document.createElement('th');
    
//         //   // Use the headerRenderer function to get the processed HTML
//         //   var headerHtml = GbGradeTable.headerRenderer(col, $th);
    
//         //   // Create a div and set its inner HTML
//         //   var headerDiv = document.createElement("div");
//         //   headerDiv.innerHTML = headerHtml;
    
//         //   // Return the div's inner HTML
//         //   return headerDiv.innerHTML;
//         // },
//         // titleFormatter: function (col, column, $th) {
//         //     return GbGradeTable.headerRenderer(col, column, $th);
//         //   },
//         titleFormatter: headerRenderer(2, 'courseGradeHeader'),
//         field: "coursegrade",
//         formatter: function(cell, formatterParams, onRendered) {
//           return GbGradeTable.tbrcourseGradeRenderer(cell, formatterParams, onRendered);
//       },
//         editor: false,
//         width: GbGradeTable.settings.showPoints ? 220 : 140,
//       }
//     ];
    
    
    
//     // GbGradeTable.tbrFixedColumns = GbGradeTable.getFixedColumns().map((fixedColumn, index) => {
//     //   return {
//     //     titleFormatter: function() {
//     //       const column = fixedColumn._data_;
//     //       const $th = document.createElement('th');
    
//     //       // Use the headerRenderer function to get the processed HTML
//     //       const headerHtml = GbGradeTable.headerRenderer(index, column, $th);
//     //       console.log(`tbrProcessed ${column.type} Header HTML:`, headerHtml); // Debugging log
    
//     //       // Create a div and set its inner HTML
//     //       const headerDiv = document.createElement("div");
//     //       headerDiv.innerHTML = headerHtml;
    
//     //       // Return the div's inner HTML
//     //       return headerDiv.innerHTML;
//     //     },
//     //     field: column.type,
//     //     formatter: function(cell, formatterParams, onRendered) {
//     //       return fixedColumn.renderer(cell.getValue(), cell.getRow().getData());
//     //     },
//     //     editor: fixedColumn.editor,
//     //     width: fixedColumn.width,
//     //   };
//     // });



    
//     GbGradeTable.tbrColumns = GbGradeTable.columns.map((column, index) => {
//       return {
//         formatter: function(cell, formatterParams, onRendered) {
//           return GbGradeTable.tbrcellRenderer(cell, formatterParams, onRendered);
//       },
//         titleFormatter: function() {
//           const colIndex = GbGradeTable.FIXED_COLUMN_OFFSET + index;
//           const $th = document.createElement('th');
    
//           // Use the headerRenderer function to get the processed HTML
//           const headerHtml = GbGradeTable.headerRenderer(colIndex, column, $th);
//           // console.log(`Processed ${column.type} Header HTML:`, headerHtml); // Debugging log
    
//           // Create a div and set its inner HTML
//           const headerDiv = document.createElement("div");
//           headerDiv.innerHTML = headerHtml;
    
//           // Return the div's inner HTML
//           return headerDiv.innerHTML;
//         },
//         // field: column.type,
    
//         editor: column.editor,
//         width: column.width || 180,
//       };
//     });
  
  
//     const allColumns = GbGradeTable.tbrFixedColumns.concat(GbGradeTable.tbrColumns);
  
//   // Header renderer function // headerRender is suppose to be function parent for all sorts of header renderers, fixed and asisgnments
//   function headerRenderer(col, templateName) {
//     return function(cell) {
//       return GbGradeTable.templates[templateName].process({ col: col, settings: GbGradeTable.settings });
//     };
//   }

//     GbGradeTable.newInstance = new Tabulator ("#tbr_gradeTableWrapper", {
//         // debugInitialization:true, // only for debugging
//         data: GbGradeTable.getFilteredData(),
//         layout: "fitColumns",
//         columns: allColumns,
//       });
    
//   };
  


GbGradeTable.renderTableTabulator = function (elementId, tableData) {
  GbGradeTable.tbrFixedColumns = [
      {
          titleFormatter: customTitleFormatter(0, 'studentHeader'),
          field: "studentName",
          formatter: GbGradeTable.studentCellRenderer,
          editor: false,
          width: 220,
      },
      {
          titleFormatter: customTitleFormatter(1, 'studentNumberHeader'),
          field: "studentNumber",
          formatter: GbGradeTable.tbrstudentNumberCellFormatter,
          editor: false,
          width: studentNumberColumnWidth,
      },
      {
          titleFormatter: customTitleFormatter(2, 'courseGradeHeader'),
          field: "coursegrade",
          formatter: function(cell, formatterParams, onRendered) {
              return GbGradeTable.tbrcourseGradeRenderer(cell, formatterParams, onRendered);
          },
          editor: false,
          width: GbGradeTable.settings.showPoints ? 220 : 140,
      }
  ];

  GbGradeTable.tbrColumns = GbGradeTable.columns.map((column, index) => {
      return {
          formatter: function(cell, formatterParams, onRendered) {
              return GbGradeTable.tbrcellRenderer(cell, formatterParams, onRendered);
          },
          titleFormatter: 
          editor: column.editor,
          width: column.width || 180,
      };
  });


  var $th = $(th);

  // Calculate the HTML that we need to show
  var html = '';
  if (col < GbGradeTable.FIXED_COLUMN_OFFSET) {
    html = GbGradeTable.headerRenderer(col, $th);
    // console.log('col', col, 'html', html);
  } else {
    //If col is not rendered, skip header renderer
    if (!GbGradeTable.isColumnRendered(this, col)) return false;
    html = GbGradeTable.headerRenderer(col, this.view.settings.columns[col]._data_, $th);

  }
  const allColumns = GbGradeTable.tbrFixedColumns.concat(GbGradeTable.tbrColumns);

  GbGradeTable.newInstance = new Tabulator("#tbr_gradeTableWrapper", {
      data: GbGradeTable.getFilteredData(),
      layout: "fitColumns",
      columns: allColumns,
  });
};

function customTitleFormatter(col, templateName) {
  return function(cell) {
      return GbGradeTable.headerRenderer(col, cell.getColumn().getDefinition(), cell.getElement());
  };
}

