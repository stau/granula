/*
 * Copyright 2015 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var jobLists = [];
var selectedJobList = null;
var selectedJobArchive = null;
var selectedOperationUuid = null;

var columns = new Columns();

var fv_open_modals = 0;

$(document).ready(function () {
    loadVisualizer();
});

function loadVisualizer() {

    $('#cache-div').load('history.htm', function () {
        $('#cache-div #recent-arc a').each(function() {
            addJobList($(this).text());
        });

        hideAll();
        drawNavPanel();
        drawFooter();
        processParameters();
    });




}

function displayIntro() {
    hideAll();
    drawIntro();
    showIntro();
}

function displayDoc() {
    hideAll();
    drawDoc();
    showDoc();
}

function displayAbout() {
    hideAll();
    drawAbout();
    showAbout();
}

function displayDashboard() {
    hideAll();
    drawDashboard();
    showDashboard();
}

function displayJobPerfermance() {
    hideAll();
    showJobArchiveBoard();
    drawJobPerformance();
}

function drawJobPerformance() {

    if(selectedOperationUuid) {
        drawOperation(selectedOperationUuid);
    }
    else if (selectedJobArchive) {

        var jobNode = $(selectedJobArchive.file).children("Job");
        var topOperation = new Operation(jobNode.children("Operations").children("Operation"));
        drawOperation(topOperation.uuid);
    }
}

function hideAll() {
    $("#intro-container").hide();
    $("#doc-container").hide();
    $("#about-container").hide();
    $("#dashboard").hide();
    $("#perfboard").hide();
    $("#loading-div").hide();
}

function showDashboard() {
    $("#dashboard").show();
}

function showIntro() {
    $('#intro-container').show();
}

function showDoc() {
    $('#doc-container').show();
}

function showAbout() {
    $('#about-container').show();
}

function showLoadingDiv() {
    $("#loading-div").show();
}

function showJobArchiveBoard() {
    $("#perfboard").show();
}

function showDefaultModal() {
    $("#default-modal").modal('show');
}

function showShareModal() {
    $("#share-modal").modal('show');
}

function showNotificationModal() {
    $("#notification-modal").modal('show');
}

function showXmlModal() {
    $("#xml-modal").modal('show');
}

function drawIntro() {
    $('#intro-container').load('doc/intro/intro.htm');
}

function drawDoc() {
    $('#doc-container').load('doc/doc.htm');
}

function drawAbout() {
    $('#about-container').load('doc/about/about.htm');
}

function selectTarget(archiveId, jobUuid, operationUuid) {
    selectedJobList = getJobListById(archiveId);
    selectedJobArchive = getJobArchiveByUuid(archiveId, jobUuid);
    selectedOperationUuid = operationUuid;
}

function processParameters() {

    var page = getHttpParameters("page");
    var archivePath = getHttpParameters("arc");
    var jobUuid = getHttpParameters("job")
    var operationId = getHttpParameters("operation");

    if(archivePath) {

        if(!jobListAdded(archivePath)) {
            addJobList(archivePath);
        }
        var archive = getJobListByURL(archivePath);
        loadArchiveAndDisplay(archive, jobUuid, operationId);

    } else {
        if(page) {
            if(page == 'intro') {
                displayIntro();
            } else if(page == 'dashboard') {
                displayDashboard();
            } else if (page == 'doc') {
                displayDoc();
            } else if(page == 'about') {
                displayAbout();
            } else {
                displayDashboard();
            }
        } else {
            displayIntro();
        }
    }


}

function jobListAdded(archiveUrl) {
    if(_.filter(jobLists, function(archive) { return archive.url === archiveUrl}).length > 0) {
        return true;
    }
    else {
        return false;
    }
}

function addJobList(jobListUrl) {

    if(jobListAdded(jobListUrl)) {
        alert("Warning: Archive with url " + jobListUrl + " exists");
    } else{
        var archiveId = null;
            archiveId = (jobLists.length > 0 ) ? (_.last(jobLists)).id + 1 : 1;
        jobLists.push(new JobList(archiveId, jobListUrl));
    }

}



function loadArchiveAndDisplay(archive, jobUuid, operationId) {

    showLoadingDiv();

    //var archiveTableRow = $("#dashboard .table").find('tr[archiveId=' + archiveId + ']');

    if(!isSameOrigin(archive.url)) {
        alert('Error: file ' + archive.url +' does not follow the same origin policy');
        console.log('Error: file ' + archive.url +' does not follow the same origin policy');
        archive.status = "Failed";
        //archiveTableRow.find(".archive-status").html("Failed");
        return;
    }

    $.ajax({
        type: "GET",
        url: archive.url,
        dataType: 'xml',
        success: function(data){
            archive.file = data;
            archive.status = "Loaded";
            selectTarget(archive.id, jobUuid, operationId);

            if(jobUuid || operationId) {

                if($(selectedJobList.file).find('Job[uuid=' + jobUuid +']').length == 0) {
                    var opText = 'Tried to find Job [' + jobUuid + '] in Archive [' + archive.url +'].';
                    var errText = 'But Job [' + jobUuid + '] could not be found.';
                    var resText = "Redirecting to Dashboard."
                    drawNotificationTraceModal("Job Retrieval Failed", opText, errText, resText);
                    showNotificationModal();
                    displayDashboard();
                    selectedOperationUuid = null;
                    return;
                }

                if($(selectedJobList.file).find('Job[uuid=' + jobUuid +']').find('Operation[uuid=' + operationId +']').length == 0) {
                    var opText = 'Tried to find Operation [' + operationId + '] in Job [' + jobUuid +'].';
                    var errText = 'However Operation [' + operationId + '] could not be found.';
                    var resText = 'Redirecting to the top operation of Job[' + jobUuid + '].';
                    drawNotificationTraceModal("Job Retrieval Failed", opText, errText, resText);
                    showNotificationModal();
                    selectedOperationUuid = null;
                }

                displayJobPerfermance();
            } else {
                displayDashboard();
            }
        },
        xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    $('#loading-bar').html('Loading Granula Archive [' + archive.url + '] - ' + (percentComplete * 100).toFixed(0) + '%')
                }
            }, false);
            return xhr;
        },
        error: function(xhr, textStatus, errorThrown){
            archive.status = "Failed";
            //displayDashboard();
            console.log('Error: url ' + archive.url +' cannot be loaded');

            var opText = 'Tried to load granula archive at path ' + archive.url +'.';
            var errText = 'But the loading of the archive was not successful.';
            var resText = "Redirecting to Dashboard."
            drawNotificationTraceModal("Archive loading Failed", opText, errText, resText);
            showNotificationModal();
            displayDashboard();
        }
    });


}

function loadJobList(jobListId) {

    var jobList = getJobListById(jobListId);

    jobList.status = "Loading";
    displayDashboard();

    if(!isSameOrigin(jobList.url)) {
        alert('Error: file ' + jobList.url +' does not follow the same origin policy');
        console.log('Error: file ' + jobList.url +' does not follow the same origin policy');
        jobList.status = "Failed";
        //archiveTableRow.find(".archive-status").html("Failed");
        return;
    }

    $.ajax({
        type: "GET",
        url: jobList.url,
        dataType: 'xml',
        success: function(data){
            jobList.file = data;
            jobList.status = "Loaded";
            jobList.jobArcs = $(jobList.file).children("Jobs").children("Job").map(function(){ return new JobArchive($(this))}).get();
            drawJobLists();
            //displayDashboard();
        },
        error: function(xhr, textStatus, errorThrown){
                jobList.status = "Failed";
            //displayDashboard();
                console.log('Error: url ' + jobList.url +' cannot be loaded');
        }, xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    jobList.status  = 'Loading ' + (percentComplete * 100).toFixed(0) + '%';
                    displayDashboard();
                }
            }, false);
            return xhr;
        }
    });


}


function loadJobArchive(archiveId, jobArcUuid) {

    var jobArc = getJobArchiveByUuid(archiveId, jobArcUuid);

    jobArc.status = "Loading";
    displayDashboard();

    if(!isSameOrigin(jobArc.url)) {
        alert('Error: file ' + jobArc.url +' does not follow the same origin policy');
        console.log('Error: file ' + jobArc.url +' does not follow the same origin policy');
        jobArc.status = "Failed";
        //archiveTableRow.find(".archive-status").html("Failed");
        return;
    }



    $.ajax({
        type: "GET",
        url: jobArc.url,
        dataType: 'xml',
        success: function(data){
            jobArc.file = data;
            jobArc.status = "Loaded";
            drawJobLists();
            //displayDashboard();
        },
        error: function(xhr, textStatus, errorThrown){
            jobArc.status = "Failed";
            //displayDashboard();
            console.log('Error: url ' + jobArc.url +' cannot be loaded');
        }, xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    jobArc.status  = 'Loading ' + (percentComplete * 100).toFixed(0) + '%';
                    displayDashboard();
                }
            }, false);
            return xhr;
        }
    });


}



function unloadJobArchive(archiveId, jobArcUuid) {

    var jobArc = getJobArchiveByUuid(archiveId, jobArcUuid);

    jobArc.file = null;
    jobArc.status = "Unloaded";
    displayDashboard();

}


function unloadJobList(jobListId) {

    var jobList = getJobListById(jobListId);

    jobList.file = null;
    jobList.status = "Unloaded";
    jobList.jobArcs = [];
    displayDashboard();

}

function getJobListById(jogListId) {

    var matchedJobLists = _.filter(jobLists, function(archiveX) { return archiveX.id == jogListId});
    if(matchedJobLists.length != 1) {console.log('Error: Find ' + matchedJobLists.length  +' job list with id ' + jogListId)};

    return matchedJobLists[0];
}


function getJobArchiveByUuid(archiveId, jobArcUuid) {

    var archive = getJobListById(archiveId);
    var matchedJobArchives = _.filter(archive.jobArcs, function(jobArc) { return jobArc.uuid == jobArcUuid});
    if(matchedJobArchives.length != 1) {console.log('Error: Find ' + matchedJobArchives.length  +' job archives with uuid ' + jobArcUuid)};

    return matchedJobArchives[0];
}

function getJobListByURL(jobListUrl) {

    var matchedJobLists = _.filter(jobLists, function(jobList) { return jobList.url == jobListUrl});
    if(matchedJobLists.length != 1) {console.log('Error: Find ' + matchedJobLists.length  +' job list with url ' +  jobListUrl)};

    return matchedJobLists[0];
}

function drawDashboard() {

    drawJobLists();

    var dashboard = $("#dashboard");
    dashboard.find('h2').html("Dashboard");
    $(".description").html("Here is an overview of the Granula archives that you are reviewing.");
    $("#archiveURL").val("");

    $("#add-arc-form").on("submit", function (e) {
        e.preventDefault();
        var arcUrlInput = $("#archiveURL");
        if(arcUrlInput.val() !== "") {
            addJobList(arcUrlInput.val());
            arcUrlInput.val("");
            drawJobLists();
            //drawArchiveTable();
        }
    });
}

function getJobItm(jobArc, arcId) {

    var jobItm = $('<li class="list-group-item job-item">' + jobArc.type + " [ " + jobArc.name  + " ]" +'</li>');
    if(selectedJobArchive == jobArc) {
        jobItm.addClass("selected-job-item");
    }
    jobItm.append(' (' + jobArc.status + ')');

    var jobBtnGroup = $('<span class="pull-right"></span>');

    var jobLoadBtn = $('<button class="btn btn-xs btn-info"></button>');
    jobLoadBtn.attr('arc-id', arcId);
    jobLoadBtn.attr('job-arc-uuid', jobArc.uuid);
    jobLoadBtn.append($('<span class="glyphicon glyphicon glyphicon-play"></span>') );
    jobLoadBtn.append(' Load');
    jobBtnGroup.append(jobLoadBtn);

    jobBtnGroup.append(" ");

    var jobViewBtn = $('<button class="btn btn-xs btn-info"></button>');
    jobViewBtn.attr('arc-id', arcId);
    jobViewBtn.attr('job-arc-uuid', jobArc.uuid);
    //jobViewBtn.attr('op-uuid', job.topOperation.uuid);
    jobViewBtn.append($('<span class="glyphicon glyphicon glyphicon-fullscreen"></span>') );
    jobViewBtn.append(' View');
    jobViewBtn.prop("disabled", true);
    jobBtnGroup.append(jobViewBtn);




    jobItm.append(jobBtnGroup);

    var drptTxt = $('<p class="list-group-item-text"></p>');
    drptTxt.html('<a href="' + jobArc.url + '" target="_blank">' + jobArc.url + '</a>');

    jobItm.append(drptTxt);

    jobViewBtn.on("click", function(){
        selectTarget($(this).attr('arc-id'), $(this).attr('job-arc-uuid'));
        displayJobPerfermance();
    });

    jobLoadBtn.on("click", function(){
        selectTarget($(this).attr('arc-id'), $(this).attr('job-arc-uuid'));
        loadJobArchive($(this).attr('arc-id'), $(this).attr('job-arc-uuid'));
    });


    if(jobArc.file) {

        jobViewBtn.prop("disabled", false);

        jobLoadBtn.empty();
        jobLoadBtn.append($('<span class="glyphicon glyphicon glyphicon-pause"></span>') );
        jobLoadBtn.append(" Unload");
        jobLoadBtn.off();
        jobLoadBtn.on("click", function(){
            unloadJobArchive($(this).attr('arc-id'), $(this).attr('job-arc-uuid'));
        });
    }


    return jobItm;
}

function getJobListItm(archive) {



    var arcItm = $('<li class="list-group-item"></li>');

    arcItm.append($('<span class="glyphicon glyphicon-pushpin"></span>'));
    arcItm.append($('<strong>' + ' ' + get2Digit(archive.id) + " "+ getFilename(archive.url) + '</strong>'));
    arcItm.append(' (' + archive.status + ')');

    var btnGroup = $('<span class="pull-right"></span>');

    var loadBtn = $('<button class="btn btn-xs btn-info"></button>');
    loadBtn.attr('arc-id', archive.id);
    loadBtn.append($('<span class="glyphicon glyphicon glyphicon-play"></span>') );
    loadBtn.append(' Load');

    //var saveBtn = $('<button class="btn btn-xs btn-info"></button>');
    //saveBtn.attr('arc-id', archive.id);
    //saveBtn.append($('<span class="glyphicon glyphicon glyphicon-floppy-save"></span>') );
    //saveBtn.append(' Save');

    var viewBtn = $('<button class="btn btn-xs btn-info" data-toggle="collapse" class="clickable"></button>');
    viewBtn.attr('arc-id', archive.id);
    viewBtn.attr('data-target', '#arc-' + archive.id +'-job-pnl');
    viewBtn.append($('<span class="glyphicon glyphicon glyphicon-fullscreen"></span>'));
    viewBtn.append(' View');

    btnGroup.append(loadBtn);
    btnGroup.append(" ");
    //btnGroup.append(saveBtn);
    //btnGroup.append(" ");
    btnGroup.append(viewBtn);
    arcItm.append(btnGroup);

    loadBtn.on("click", function(){
        loadJobList($(this).attr('arc-id'));
    });

    //saveBtn.on("click", function(){
    //    window.open(
    //        getArchiveById($(this).attr('arc-id')).url,
    //        '_blank'
    //    );
    //});

    viewBtn.on("click", function(){
        //selectTarget($(this).attr('arc-id'), null);
        //displayJobPerfermance();
    });

    //saveBtn.prop("disabled", true);
    viewBtn.prop("disabled", true);


    var drptTxt = $('<p class="list-group-item-text"></p>');
    drptTxt.html('<a href="' + archive.url + '" target="_blank">' + archive.url + '</a>');

    arcItm.append(drptTxt);

    //loadJobList(archive.id);

    if(archive.file) {

        loadBtn.empty();
        loadBtn.append($('<span class="glyphicon glyphicon glyphicon-pause"></span>') );
        loadBtn.append(" Unload");
        loadBtn.off();
        loadBtn.on("click", function(){
            unloadJobList($(this).attr('arc-id'));
        });

        //saveBtn.prop("disabled", false);
        viewBtn.prop("disabled", false);

        var jobListPnl = $('<div id="" class="panel panel-default collapse"></div>');
        jobListPnl.attr('id', 'arc-' + archive.id +'-job-pnl');
        if(selectedJobList && archive.id == selectedJobList.id) {
            jobListPnl.addClass("in")
        }
        var jobList = $('<ul class="list-group"></ul>');
        jobListPnl.append(jobList);

        var jobArcs = archive.jobArcs;
        for(var i = 0; i < jobArcs.length; i++) {
            jobList.append(getJobItm(jobArcs[i], archive.id));
        }

        arcItm.append(jobListPnl);
    }

    return arcItm;
}


function drawJobLists() {
    var dashboard = $("#dashboard");

    var jobListsPnl = dashboard.find("#arc-list-pnl");

    jobListsPnl.find(".panel-heading").text("Recently viewed Granula Archives");

    var jobListsContainer = jobListsPnl.find(".arc-list");
    jobListsContainer.empty();

    for(var i = 0; i < jobLists.length; i++) {
        var archive = jobLists[i];
        jobListsContainer.append(getJobListItm(archive));
    }
}

function drawTopOperation() {
    $(selectedJobArchive.file).children("Job").children("Operations").children("Operation").each(function () {
        drawOperation(new Operation($(this)).uuid);
    });

}


function getAncestorLink(operation) {

    var currentOperation = operation;

    var ancestorLink = $('<p></p>');

    ancestorLink.prepend('&nbsp;&nbsp;>&nbsp;&nbsp;' + currentOperation.getTitle());
    currentOperation = currentOperation.getSuperoperation();

    while(currentOperation !== null) {
        var parentLink = $('<a>' + currentOperation.getTitle() +'</a>');
        parentLink.attr('uuid', currentOperation.uuid);

        parentLink.on('click', function (e) {
            e.preventDefault();
            selectedOperationUuid = $(this).attr('uuid');
            drawJobPerformance();
        });
        ancestorLink.prepend(parentLink);
        ancestorLink.prepend('&nbsp;&nbsp;>&nbsp;&nbsp;')
        currentOperation = currentOperation.getSuperoperation();
    }

    var jobLink = $('<a>' + selectedJobArchive.type + " [ " + selectedJobArchive.name  + " ]" + '</a>');
    jobLink.on('click', function (e) {
        e.preventDefault();
        displayDashboard();
    });

    //ancestorLink.prepend('&nbsp;&nbsp;');
    ancestorLink.prepend( jobLink);

    return ancestorLink;
}

function drawOperation(operationId) {

    var perfBoard = $("#perfboard");
    //perfBoard.find('h2').html("Job Performance");

    selectedOperationUuid = operationId;

    var operationNode = $(selectedJobArchive.file).children("Job").children("Operations").find('Operation[uuid="' + operationId + '"]');
    plotOperation(operationNode);

    var perfBoard = $("#perfboard");

    perfBoard.find('h2').empty().html('Job Performance Visualizer');
    perfBoard.find('.description').html('<h3><small><p class="ancestors-link"></p></small></h3>');
    perfBoard.find('.description').find('.ancestors-link').append(getAncestorLink(new Operation(operationNode)));


    var btnGroup = perfBoard.find('.header-btn-group');
    btnGroup.empty();

    var shareBtn = $('<button class="btn btn-default share-btn">Share</button>');
    var url = getDomainURL() + '?' + 'arc=' + selectedJobArchive.url + '&' + 'operation=' + selectedOperationUuid;
    shareBtn.attr('url', url);

    var viewBtn = $('<button class="btn btn-default view-btn">View</button>');

    var dwnBtn = $('<button class="btn btn-default share-btn">Download</button>');
    var arcUrl = selectedJobArchive.url;
    dwnBtn.attr('arc-url', arcUrl);

    //var dashboardBtn = $('<button class="btn btn-default share-btn">Dashboard</button>');


    btnGroup.append(shareBtn);
    btnGroup.append(" ");
    btnGroup.append(viewBtn);
    btnGroup.append(" ");
    btnGroup.append(dwnBtn);
    //btnGroup.append(" ");
    //btnGroup.append(dashboardBtn);

    //dashboardBtn.on('click', function() {
    //    displayDashboard();
    //})

    shareBtn.on("click", function(){

        var modal = $("#share-modal");
        var mdlBody = modal.find(".modal-body");
        var mdlTitle = modal.find(".modal-title");
        mdlTitle.html('Sharing/Bookmarking the Current View');
        var drptText = modal.find(".drpt-text");
        var urlPnl = modal.find(".url-pnl");

        var job = new Job($(selectedJobArchive.file).find("Job"));
        var operation = new Operation($(selectedJobArchive.file).children("Job").children("Operations").find('Operation[uuid="' + operationId + '"]'));
        var contextText = 'You are currently viewing Operation [' + operation.name + '] of Job [' + job.name  + '], found in Granula Archive [' + selectedJobArchive.url + ']. ';
        var actionText = 'Please use the following URL to share/bookmark this view: ';
        drptText.empty().append($('<p>' + contextText + ' ' + actionText + '</p>'));

        var urlText = $(this).attr('url');
        urlPnl.empty().append($('<p>' + '<a href=' + urlText + ' target="_blank">' + urlText + '</a>' + '</p>'));

        showShareModal();
        //window.prompt("Use the following URL to share the current view:", $(this).attr('url'));
    });

    viewBtn.on("click", function(){
        var operationNode = $(selectedJobArchive.file).children("Job").children("Operations").find('Operation[uuid="' + operationId + '"]');
        var xmlFull = (new XMLSerializer()).serializeToString(operationNode[0]);
        var xmlIm = $(xmlFull);
        xmlIm.children('Children').children('Operation').empty();
        xmlIm.children('Visuals').children('Visual').empty();
        xmlIm.children('Infos').children('Info').empty();
        var xml = getPrintableXml(xmlIm[0]);
        drawXmlModal((new Operation(operationNode)).getTitle(), xml);
        showXmlModal();

    });

    dwnBtn.on("click", function(){
        window.open(
            $(this).attr('arc-url'),
            '_blank'
        );
    });
}


function plotOperation(operation) {

    var visualNodes = operation.children("Visuals").children("Visual");

    var op = new Operation(operation);
    var board = new Board();

    var titleFrame = new Frame();
    board.addFrame(titleFrame);
    var titleVisual = new TitleVisual('Operation ' + op.getTitle());
    titleFrame.setVisual(titleVisual);

    var drptFrame = new Frame();
    board.addFrame(drptFrame);
    var drptVisual = new DescriptionVisual(operation.children("Visuals").children("Visual[name=SummaryVisual]"));
    drptFrame.setVisual(drptVisual);

    var operationFrame = new Frame();
    board.addFrame(operationFrame);
    var operationVisual = new OperationVisual(op);
    operationFrame.setVisual(operationVisual);

    visualNodes.each(function () {
        var visualNode = $(this);
        var d =  (new Date());
        if(visualNode.attr("name") !== "AllInfoVisual" && visualNode.attr("name") !== "SummaryVisual") {

            if(visualNode.attr("type") == "TableVisual") {
                var visualFrame = new Frame();
                board.addFrame(visualFrame);
                var visual = new TableVisual(visualNode);
                visualFrame.setVisual(visual);
            }

            if(visualNode.attr("type") == "TimeSeriesVisual") {
                var visualFrame = new Frame();
                board.addFrame(visualFrame);
                var visual = new TimeSeriesVisual(visualNode);
                visualFrame.setVisual(visual);
            }
        }
    });

    //var linechartFrame = new Frame();
    //board.addFrame(linechartFrame);
    //var visualStats = new LSC_VisualStats();
    //visualStats.load(op);
    //var linechartVisual = new LineChartVisual(visualStats);
    //linechartFrame.setVisual(linechartVisual);

    var allInfoFrame = new Frame();
    board.addFrame(allInfoFrame);
    var allInfoVisual = new TableVisual(operation.children("Visuals").children("Visual[name=AllInfoVisual]"));
    allInfoFrame.setVisual(allInfoVisual);

    board.construct();
    board.position();
    board.draw();
}


function drawNotificationTraceModal(title, operationText, errorText, resultText) {

    var modal = $("#notification-modal");
    var mdlBody = modal.find(".modal-body");
    var mdlTitle = modal.find(".modal-title");
    mdlTitle.html(title);
    mdlBody.empty();
    mdlBody.append($('<p>' + operationText + ' ' + errorText + ' ' + resultText + '</p>'));

}



function drawXmlModal(title, xml) {

    var modal = $("#xml-modal");
    var mdlBody = modal.find(".modal-body");
    var mdlTitle = modal.find(".modal-title");
    var preField = mdlBody.find('.prettyprint');
    mdlTitle.html(title);
    preField.html(xml);
    prettyPrint();
}

function drawInfoTraceModal(infoUuidsText) {

    var infoUuids = infoUuidsText.split("-");
    var infoUuid = infoUuids[infoUuids.length -1];

    var infoNode = $(selectedJobArchive.file).find('Info[uuid=' + infoUuid +']');
    var ownerNode = infoNode.parent("Infos").parent();

    var info = new Info(infoNode);

    if(ownerNode[0].tagName == "Operation") {
        var owner = new Operation(ownerNode);
    } else {
        console.error("Unknown information owner type");
    }

    var modal = $("#default-modal");
    var mdlBody = modal.find(".modal-body");
    var mdlTitle = modal.find(".modal-title");
    mdlTitle.html("Information Tracing");
    mdlBody.empty();



    var traceStack = $('<p />');
    for(var i = 0; i < infoUuids.length; i++) {
        var historyInfo = new Info($(selectedJobArchive.file).find('Info[uuid=' + infoUuids[i] +']'));
        var infoLink = $('<a>' + historyInfo.name + '@' + (new Operation(historyInfo.owner)).getTitle() + '</a>');
        traceStack.append($('<small> &#8680 </small>'));
        traceStack.append(infoLink);


        var partUuidText = "";
        for(var j = 0; j <= i; j++) {
            partUuidText += (partUuidText == "") ? infoUuids[j] : "-" + infoUuids[j];
        }

        infoLink.attr('uuid', partUuidText);

        infoLink.on('click', function (e) {
            e.preventDefault();
            drawInfoTraceModal($(this).attr('uuid'));
        });
    }
    mdlBody.append(traceStack);

    var infoContainer = $('<div class="panel-body tab-container"><div class="tab">Target Information</div><br></div>');
    mdlBody.append($('<div class="panel panel-default"></div>').append(infoContainer));

    infoContainer.append($('<p>' + info.description + '</p>'));


    var infoTblContainer = $('<div class="panel panel-default">');
    var infoTblLabel = $('<div class="panel-heading">Information [' + info.name + ']</div>');

    var infoTable = $('<table class="table table-bordered">');
    infoTable.append($('<tr><th>Name</th><th>Type</th><th>Value</th><th>Information Owner</th></tr>'));
    var row = $('<tr></tr>');
    row.append('<td>' + info.name + '</td>');
    row.append('<td>' + info.type + '</td>');
    row.append('<td>' + info.value + '</td>');
    row.append('<td>' + 'Operation ' + owner.getTitle() + '</td>');

    var xmlField = $('<div></div>');

    xmlField.hide();

    var printBtn = $('<button class="btn btn-xs trace-btn pull-right">');
    printBtn.append('<span class="glyphicon glyphicon glyphicon-menu-down"></span>' + '</button>');
    printBtn.attr('uuid', info.uuid);
    printBtn.attr('toggle', "off");
    printBtn.on("click", function(){
        if($(this).attr('toggle') == "off") {
            $(this).attr('toggle', "on");
            $(this).find('span').removeClass('glyphicon-menu-down');
            $(this).find('span').addClass('glyphicon-menu-up');
            var btnInfo = new Info($(selectedJobArchive.file).find('Info[uuid=' + $(this).attr('uuid') +']'));

            var printableXml = getPrintableXml((btnInfo.node)[0]);
            var preField = $('<pre class="prettyprint"></pre>');
            preField.html(printableXml);
            xmlField.append(preField);
            xmlField.show();
            prettyPrint();
        } else {
            $(this).attr('toggle', "off");
            $(this).find('span').addClass('glyphicon-menu-down');
            $(this).find('span').removeClass('glyphicon-menu-up');
            xmlField.empty();
            xmlField.hide();
        }
    });

    infoTblLabel.append(printBtn);
    infoTable.append(row);

    infoTblContainer.append(infoTblLabel);
    infoTblContainer.append(infoTable);
    infoContainer.append(infoTblContainer);
    infoContainer.append(xmlField);


    var sourcesContainer = $('<div class="panel-body tab-container"><div class="tab">Source List</div><br></div>');
    mdlBody.append($('<div class="panel panel-default"></div>').append(sourcesContainer));

    var sources = infoNode.children("Sources").children("Source").map( function () { return new Source($(this)); }).get();

    if(sources.length == 0) {
        sourcesContainer.append($('<p>Strangely, there are not any sources for this information.</p>'));
    }


    for(var i = 0; i < sources.length; i++) {

        var source = sources[i];

        var sourceTblContainer = $('<div class="panel panel-default">');
        var sourceTblLabel = $('<div class="panel-heading">Source [' + source.name + ']</div>');
        var sourceTable = $('<table class="table table-bordered">');

        if (source.type == "InfoSource") {
            sourceTable.append($('<tr><th>Name</th><th>Value</th><th>Information Owner</th></tr>'));
            for(var j = 0; j < source.infoUuids.length; j++) {
                var infoUUid = source.infoUuids[j];
                var sourceInfoNode = $(selectedJobArchive.file).find('Info[uuid=' + infoUUid +']');

                var sourceInfo = new Info(sourceInfoNode);
                var ownerNode = sourceInfoNode.parent("Infos").parent();
                var owner = new Operation(ownerNode);
                var row = $('<tr></tr>');

                var traceSign = $('<button class="btn btn-xs trace-btn" uuid="' + infoUuidsText + '-' + sourceInfo.uuid +'">');
                traceSign.append('<span class="glyphicon glyphicon glyphicon-zoom-in"></span>' + '</button>');

                var nameCell = $('<td></td>');
                row.append(nameCell);

                nameCell.append('<b>' + sourceInfo.name + '</b>');
                nameCell.append(traceSign);

                row.append('<td>' + sourceInfo.value + '</td><td>' + 'Operation ' + owner.getTitle() + '</td>');

                traceSign.on("click", function(){
                        drawInfoTraceModal($(this).attr("uuid"));
                });

                sourceTable.append(row);
            }
        } else {
            sourceTable.append($('<tr><th>Record Location</th><td>' + source.recordLocation  + '</td><</tr>'));
        }


        sourceTblContainer.append(sourceTblLabel);
        sourceTblContainer.append(sourceTable);
        sourcesContainer.append(sourceTblContainer);


    }
}

function drawNavPanel() {


    var dashboardBtn = $("#dashboard-btn");
    dashboardBtn.on("click", function(e) {
        e.preventDefault();
        $('#nav-container li').removeClass('active');
        $(this).parent().addClass('active');
        displayDashboard();
    });

    var homeBtn = $("#intro-btn");
    homeBtn.on("click", function(e) {
        e.preventDefault();
        $('#nav-container li').removeClass('active');
        $(this).parent().addClass('active');
        displayIntro();
    });

    var docBtn = $("#doc-btn");
    docBtn.on("click", function(e) {
        e.preventDefault();
        $('#nav-container li').removeClass('active');
        $(this).parent().addClass('active');
        displayDoc();
    });

    var aboutBtn = $("#about-btn");
    aboutBtn.on("click", function(e) {
        e.preventDefault();
        $('#nav-container li').removeClass('active');
        $(this).parent().addClass('active');
        displayAbout();
    });

}


function drawFooter() {
    $(".footer").append("<p>&copy; 2015 Parallel and Distributed Systems Group, Delft University of Technology </p>");
    $(".footer").append("<p>Access time: " + new Date() + "</p>");
}




function Columns() {

    this.minorColumn = null;
    this.majorColumn = null;

    this.setMinorColumn = function (minorColumn) {
        this.minorColumn = minorColumn;
    }

    this.setMajorColumn = function (majorColumn) {
        this.majorColumn = majorColumn;
    }

}

function Column(x, w) {
    this.x = x;
    this.w= w;
    this.x2 = x + w;
}

