



GbGradeTable.tbrStudentCellFormatter = function (cell, formatterParams, onRendered) {
    // console.log("Student Cell is called"); // Debugging log
    var value = cell.getValue();
    if (value === null) {
      return "";
    }
  
    // Create a temporary div element to hold our HTML
    var div = document.createElement('div');
    
    // Set attributes
    div.setAttribute("scope", "row");
    div.setAttribute("role", "rowHeader");
  
    var row = cell.getRow().getPosition();
    var col = cell.getColumn().getField();
    var cellKey = row + '_' + col;
  
    var data = Object.assign({
      settings: GbGradeTable.settings       // this wouldn't work.
    }, value);
    // console.log("Student Cell Data:",value); // Debugging log
  
    // Assuming GbGradeTable.templates.studentCell is a function that returns HTML
    var html = GbGradeTable.tbrTemplates.studentCell.setHTML(data);
  
    div.innerHTML = html;
  
    // Store data on the cell element
    onRendered(function() {
      var element = cell.getElement();
      element.setAttribute('data-cell-initialised', cellKey);
      element.setAttribute('data-studentid', value.userId);
      element.setAttribute('data-metadata', JSON.stringify({
        id: cellKey,
        student: value
      }));
      element.removeAttribute('aria-describedby');
    });
  
    return div;
  }


  GbGradeTable.tbrstudentNumberCellFormatter = function(cell, formatterParams, onRendered) {

    console.log("Student Number Cell is called"); // Debugging log
    // Get the cell value and other properties
    var value = cell.getValue();
    var row = cell.getRow().getIndex();
    var col = cell.getColumn().getField();
  
    if (value === null) {
        return;
    }
  
    var cellElement = cell.getElement();
  
    $(cellElement).attr("scope", "row").attr("role", "rowHeader");
  
    var cellKey = (row + '_' + col);
  
    var data = {
        settings: GbGradeTable.settings,
        studentNumber: value
    };
  
    var html = GbGradeTable.tbrTemplates.studentNumberCell.setHTML(cellElement, data);
  
    $.data(cellElement, 'cell-initialised', cellKey);
    $.data(cellElement, "studentid", value.userId);
    $.data(cellElement, "metadata", {
        id: cellKey,
        student: value
    });
  
    $(cellElement).removeAttr('aria-describedby');
  
    // Return the HTML content
    // console.log("Student Number Cell HTML:", html); // Debugging log
    return html;
  };
  

  GbGradeTable.renderTableTabulator = function (elementId, tableData) {


    console.log("student header ", GbGradeTable.templates.studentHeader);
  
    GbGradeTable.tbrFixedColumns = [
      // Student name column
      {
        // titleFormatter: function() {
        //   var col = 0; 
        //   // var column = {
        //   //   type: "student", // Example type, adjust as necessary
        //   //   title: "Studentss"
        //   // };
        //   var $th = document.createElement('th');
    
        //   // Use the headerRenderer function to get the processed HTML
        //   var headerHtml = GbGradeTable.headerRenderer(col, $th);
    
        //   // Create a div and set its inner HTML
        //   var headerDiv = document.createElement("div");
        //   headerDiv.innerHTML = headerHtml;
    
        //   // Return the div's inner HTML
        //   return headerDiv.innerHTML;
        // },
        titleFormatter: GbGradeTable.headerRenderer(0, GbGradeTable.students),
        field: "studentName",
        formatter: GbGradeTable.studentCellRenderer,
        editor: false,
        width: 220,
      },
      // Student number column
      {
        // formatter: GbGradeTable.tbrstudentNumberCellFormatter,
    
        titleFormatter: function() {
          var col = 1;
          // var column = {
          //   type: "studentnumber", // Example type, adjust as necessary
          //   title: "Student Number"
          // };
          var $th = document.createElement('th');
    
          // Use the headerRenderer function to get the processed HTML
          var headerHtml = GbGradeTable.headerRenderer(col, $th);
    
          // Create a div and set its inner HTML
          var headerDiv = document.createElement("div");
          headerDiv.innerHTML = headerHtml;
    
          // Return the div's inner HTML
          return headerDiv.innerHTML;
        },
        field: "studentNumber",
        formatter: GbGradeTable.tbrstudentNumberCellFormatter,
        editor: false,
        width: studentNumberColumnWidth,
      },
      // Course grade column
      {
        // titleFormatter: function() {
        //   var col = 2; // Adjust the column index as necessary
        //   // var column = {
        //   //   type: "coursegrade", // Example type, adjust as necessary
        //   //   title: "Course Grade"
        //   // };
        //   var $th = document.createElement('th');
    
        //   // Use the headerRenderer function to get the processed HTML
        //   var headerHtml = GbGradeTable.headerRenderer(col, $th);
    
        //   // Create a div and set its inner HTML
        //   var headerDiv = document.createElement("div");
        //   headerDiv.innerHTML = headerHtml;
    
        //   // Return the div's inner HTML
        //   return headerDiv.innerHTML;
        // },
        titleFormatter: function () {
          var col = 2;
          var headerDiv = document.createElement("div");
          headerDiv.innerHTML = GbGradeTable.template.courseGradeHeader;
          return headerDiv.innerHTML;
        },
  
        field: "coursegrade",
        formatter: function(cell, formatterParams, onRendered) {
          return GbGradeTable.tbrcourseGradeRenderer(cell, formatterParams, onRendered);
      },
        editor: false,
        width: GbGradeTable.settings.showPoints ? 220 : 140,
      }
    ];
    
    
    
    // GbGradeTable.tbrFixedColumns = GbGradeTable.getFixedColumns().map((fixedColumn, index) => {
    //   return {
    //     titleFormatter: function() {
    //       const column = fixedColumn._data_;
    //       const $th = document.createElement('th');
    
    //       // Use the headerRenderer function to get the processed HTML
    //       const headerHtml = GbGradeTable.headerRenderer(index, column, $th);
    //       console.log(`tbrProcessed ${column.type} Header HTML:`, headerHtml); // Debugging log
    
    //       // Create a div and set its inner HTML
    //       const headerDiv = document.createElement("div");
    //       headerDiv.innerHTML = headerHtml;
    
    //       // Return the div's inner HTML
    //       return headerDiv.innerHTML;
    //     },
    //     field: column.type,
    //     formatter: function(cell, formatterParams, onRendered) {
    //       return fixedColumn.renderer(cell.getValue(), cell.getRow().getData());
    //     },
    //     editor: fixedColumn.editor,
    //     width: fixedColumn.width,
    //   };
    // });
    
    GbGradeTable.tbrColumns = GbGradeTable.columns.map((column, index) => {
      return {
        formatter: function(cell, formatterParams, onRendered) {
          return GbGradeTable.tbrcellRenderer(cell, formatterParams, onRendered);
      },
        titleFormatter: function() {
          const colIndex = GbGradeTable.FIXED_COLUMN_OFFSET + index;
          const $th = document.createElement('th');
    
          // Use the headerRenderer function to get the processed HTML
          const headerHtml = GbGradeTable.headerRenderer(colIndex, column, $th);
          // console.log(`Processed ${column.type} Header HTML:`, headerHtml); // Debugging log
    
          // Create a div and set its inner HTML
          const headerDiv = document.createElement("div");
          headerDiv.innerHTML = headerHtml;
    
          // Return the div's inner HTML
          return headerDiv.innerHTML;
        },
        field: column.type,
    
        editor: column.editor,
        width: column.width || 180,
      };
    });
  
  
    const allColumns = GbGradeTable.tbrFixedColumns.concat(GbGradeTable.tbrColumns);
  
  
  
  
    console.log("Table Data:", tableData); // Debugging log
  
    // console.log("data:", GbGradeTable.getFilteredData()); // Debugging log
    
  };
  