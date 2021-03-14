const api = "http://34.94.33.47:8000";

function get_token() {
    if (localStorage.getItem("token") != null) {
        return localStorage.getItem("token");
    }
    else {
        alert("You have logged out. Please sign in agagin!");
        location.replace("http://34.94.33.47/signin.html");
    }
}

function ajax_post_auth(url, data, handler) {
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = handler;
    xmlhttp.open("POST", url);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.setRequestHeader("Authorization", "Bearer " + get_token());
    xmlhttp.send(data);
}

function ajax_get_auth(url, handler) {
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = handler;
    xmlhttp.open("GET", url);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.setRequestHeader("Authorization", "Bearer " + get_token());
    xmlhttp.send();
}

function ajax_download_auth(url, handler) {
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = handler;
    xmlhttp.open("GET", url);
    xmlhttp.responseType = "blob";
    xmlhttp.setRequestHeader("Authorization", "Bearer " + get_token());
    xmlhttp.send();
}

function load_all_handler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            let data = JSON.parse(xmlhttp.responseText)
            let row = "";
            data.forEach((report, index) => {
                row = row.concat("<tr>")
                row = row.concat("<td class=\"text-left\">").concat(index + 1).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.description).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(formatTime(report.createdTime)).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(formatTime(report.updatedTime)).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.pdfReportStatus).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.excelReportStatus).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.pdfReportStatus === 'COMPLETED' ? "<a onclick='downloadPDF(\"" + report.id + "\")' style='margin-right: 1em' href='#'>Download PDF</a>" : "").concat(report.excelReportStatus === 'COMPLETED' ? "<a onclick='downloadExcel(\"" + report.id + "\")' style='margin-right: 1em' href='#'>Download Excel</a>" : "").concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat("<a onclick='update(\"" + report.id + "\")' href='#'>Update</a>";).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat("<a onclick='delete(\"" + report.id + "\")' href='#'>Delete</a>";).concat("</td>");
                row = row.concat("</tr>");
                
            });
            document.getElementById("report_list_body").innerHTML = row;
        }
        else {
            alert("Refresh data error. Please contact the admin!");
        }
    }
}

function load_all() {
    ajax_get_auth(api + "/report", load_all_handler);
}

function formatTime(time) {
    if (!time) return "N/A";
    const d = new Date(time);
    return singleDigit(d.getMonth() + 1) + '/' + singleDigit(d.getDate()) + ' ' + singleDigit(d.getHours()) + ':' + singleDigit(d.getMinutes());
}

function singleDigit(dig) {
    return ('0' + dig).slice(-2)
}

function download_file_handler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            var blob = xmlhttp.response;
            var fileName = xmlhttp.getResponseHeader("File-Name")
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileName;
            link.click();
        }
        else {
            alert('Download error. Please contact the admin!')
        }
    }
}

function downloadPDF(reqId) {
    ajax_download_auth(api + '/report/content/' + reqId + '/PDF', download_file_handler);
}

function downloadExcel(reqId) {
    ajax_download_auth(api + "/report/content/" + reqId + "/EXCEL", download_file_handler);
}


function showDelete(reqId) {
    if (confirm("Are you sure to delete report?")) {
        //alert('Not implemented');
    }
}

function downloadLinks(ps, es, id) {
    return (ps === 'COMPLETED' ? "<a onclick='downloadPDF(\"" + id + "\")' style='margin-right: 1em' href='#'>Download PDF</a>" : "") +
        (es === 'COMPLETED' ? "<a onclick='downloadExcel(\"" + id + "\")' style='margin-right: 1em' href='#'>Download Excel</a>" : "");
}

function validateInput() {
    try {
        return JSON.parse(document.getElementById("inputData").value);
    } catch (err) {
        alert("This is not a valid Json.");
        return "";
    }
}

function create_report_handler()
{
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            hide();
            load_all();            
        }
        else {
            alert(xmlhttp.responseText);
        }
    }
}

let my_modal = null;

function show() {
    my_modal = new bootstrap.Modal(document.getElementById("create_report_model"), {});
    my_modal.show();
}

function hide() {
    my_modal.hide();
}

function logout() {
    localStorage.removeItem("token");
    location.replace("http://34.94.33.47/signin.html");
}

function create_report(async)
{
    let data = validateInput();
    if (data) {
         ajax_post_auth((async) ? api + "/report/async" : api + "/report/sync", JSON.stringify(data), create_report_handler);
    }
}
