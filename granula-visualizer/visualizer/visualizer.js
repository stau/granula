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


$(document).ready(function () {
    loadVisualizer();
});

function loadVisualizer() {

    $('#cache-div').load('data/history.htm', function () {
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





