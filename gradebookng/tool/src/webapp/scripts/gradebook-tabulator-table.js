$(document).ready(function() {
    // need TrimPath to load before parsing templates
    GbGradeTable.tbrTemplates = {
      cell: new TrimPathFragmentCache('cell', TrimPath.parseTemplate(
          $("#cellTemplate").html().trim().toString())),
      courseGradeCell: new TrimPathFragmentCache('courseGradeCell',TrimPath.parseTemplate(
          $("#courseGradeCellTemplate").html().trim().toString())),
      courseGradeHeader: TrimPath.parseTemplate(
          $("#courseGradeHeaderTemplate").html().trim().toString()),
      assignmentHeader: TrimPath.parseTemplate(
          $("#assignmentHeaderTemplate").html().trim().toString()),
      categoryScoreHeader: TrimPath.parseTemplate(
          $("#categoryScoreHeaderTemplate").html().trim().toString()),
      studentHeader: (function() {
        var templateContent = $("#studentHeaderTemplate").html().trim().toString();
        var parsedTemplate = TrimPath.parseTemplate(templateContent);
        return parsedTemplate;
      })(),
      studentNumberHeader: (function() {
          var templateContent = $("#studentNumberHeaderTemplate").html().trim().toString();
          return TrimPath.parseTemplate(templateContent);
      })(),
      studentCell: new TrimPathFragmentCache('studentCell', TrimPath.parseTemplate(
          $("#studentCellTemplate").html().trim().toString())),
      metadata: TrimPath.parseTemplate(
          $("#metadataTemplate").html().trim().toString()),
      studentSummary: TrimPath.parseTemplate(
          $("#studentSummaryTemplate").html().trim().toString()),
      gradeItemSummary: TrimPath.parseTemplate(
          $("#gradeItemSummaryTemplate").html().trim().toString()),
      gradeItemSummaryTooltip: TrimPath.parseTemplate(
          $("#gradeItemSummaryTooltipTemplate").html().trim().toString()),
      caption: TrimPath.parseTemplate(
          $("#captionTemplate").html().trim().toString()),
      tooltip: TrimPath.parseTemplate(
          $("#tooltipTemplate").html().trim().toString()),
      gradeMenuTooltip: TrimPath.parseTemplate(
         $("#gradeMenuTooltip").html().trim().toString()),
      gradeHeaderMenuTooltip: TrimPath.parseTemplate(
         $("#gradeHeaderMenuTooltip").html().trim().toString()),
      newGradeItemPopoverTitle: TrimPath.parseTemplate(
         $("#newGradeItemPopoverTitle").html().trim().toString()),
      newGradeItemPopoverMessage: TrimPath.parseTemplate(
         $("#newGradeItemPopoverMessage").html().trim().toString()),
    };
  
    // Log to verify
  });



GbGradeTable.renderTableTabulator = function (elementId, tableData) {




    GbGradeTable.tbrFixedColumns = [
      // Student name column
      {
        titleFormatter: function() {
          var col = 0; 
          // var column = {
          //   type: "student", // Example type, adjust as necessary
          //   title: "Studentss"
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
        field: "studentname",
        formatter: GbGradeTable.tbrStudentCellFormatter,
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
        field: "studentnumber",
        formatter: function(cell, formatterParams, onRendered) {
          return GbGradeTable.tbrstudentNumberCellRenderer(cell, formatterParams, onRendered);
      },
        editor: false,
        width: studentNumberColumnWidth,
      },
      // Course grade column
      {
        titleFormatter: function() {
          var col = 2; // Adjust the column index as necessary
          // var column = {
          //   type: "coursegrade", // Example type, adjust as necessary
          //   title: "Course Grade"
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
  
  
    // Initialize the tabulator
    GbGradeTable.newInstance = new Tabulator ("#tbr_gradeTableWrapper", {
      debugInitialization:true, // only for debugging
      data: GbGradeTable.getFilteredData(),
      layout: "fitColumns",
      columns: allColumns,
  
    });
  
    // console.log("data:", GbGradeTable.getFilteredData()); // Debugging log
    
  };