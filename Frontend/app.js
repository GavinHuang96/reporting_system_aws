const fontend = ""
const backend = "http://34.94.33.47:8000";

/*** Util ***/
let newModal = null;
let updateModal = null;

function showNewModal() {
    newModal = new bootstrap.Modal(document.getElementById("create_report_model"), {});
    newModal.show();
}

function hideNewModal() {
    newModal.hide();
}

function showUpdateModal() {
    updateModal = new bootstrap.Modal(document.getElementById("update_report_model"), {});
    updateModal.show();
}

function hideUpdateModal() {
    updateModal.hide();
}

function validateInput(data) {
    try {
        return JSON.parse(data);
    } catch (err) {
        alert("This is not a valid Json.");
        return "";
    }
}

function singleDigit(dig) {
    return ("0" + dig).slice(-2)
}

function formatTime(time) {
    if (!time) return "N/A";
    const d = new Date(time + "Z");
    return singleDigit(d.getMonth() + 1) + "/" + singleDigit(d.getDate()) + " " + singleDigit(d.getHours()) + ":" + singleDigit(d.getMinutes());
}

function getToken() {
    if (localStorage.getItem("token") != null) {
        return localStorage.getItem("token");
    }
    else {
        alert("You have logged out. Please sign in agagin!");
        location.replace(fontend + "/signin.html");
    }
}

function ajax(url, method, data, responseType, doAuth, handler) {
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = handler;
    xmlhttp.open(method, url);
    xmlhttp.responseType = responseType;
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    if (doAuth) xmlhttp.setRequestHeader("Authorization", "Bearer " + getToken());
    (data == null) ? xmlhttp.send() : xmlhttp.send(data);
}

/*** Token Part ***/
function checkTokenInUserHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            if (JSON.parse(xmlhttp.responseText).success) {
                location.replace(fontend + "/index.html");
            }
        }
    }
}

function checkTokenInUser() {
    if (localStorage.getItem("token") != null) {
        ajax(backend + "/auth/validate", "POST", JSON.stringify({ "token": localStorage.getItem("token") }), "text", false, checkTokenInUserHandler);
    }
}

function checkTokenInAppHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            if (JSON.parse(xmlhttp.responseText).success == false) {
                location.replace(fontend + "/signin.html");
            } else {
                loadAll();
            }
        }
    }
}

function checkTokenInApp() {
    if (localStorage.getItem("token") != null) {
        ajax(backend + "/auth/validate", "POST", JSON.stringify({ "token": localStorage.getItem("token") }), "text", false, checkTokenInAppHandler);
    } else {
        location.replace(fontend + "/signin.html");
    }
}

/*** Authentication Part ***/
function signupHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            alert(JSON.parse(xmlhttp.responseText).message);
            location.replace(fontend + "/signin.html");
        } else {
            alert(JSON.parse(xmlhttp.responseText).message);
        }
    }
}

function signup(form) {
    let email = form.elements.email.value
    let password = form.elements.password.value
    ajax(backend + "/auth/signup", "POST", JSON.stringify({ "email": email, "password": password }), "text", false, signupHandler);
}

function signinHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            localStorage.setItem("token", JSON.parse(xmlhttp.responseText).accessToken);
            location.replace(fontend + "/index.html");
        } else {
            alert("Email or password is not correct!");
        }
    }
}

function signin(form) {
    let email = form.elements.email.value
    let password = form.elements.password.value
    ajax(backend + "/auth/signin", "POST", JSON.stringify({ "email": email, "password": password }), "text", false, signinHandler);
}

/*** APP Part ***/
function logout() {
    localStorage.removeItem("token");
    location.replace(fontend + "/signin.html");
}

function loadAllHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            let data = JSON.parse(xmlhttp.responseText).data
            let row = "";
            data.forEach((report, index) => {
                row = row.concat("<tr>")
                row = row.concat("<td class=\"text-left\">").concat(index + 1).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.description).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(formatTime(report.createdTime)).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(formatTime(report.updatedTime)).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.pdfReportStatus).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat(report.excelReportStatus).concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat((report.pdfReportStatus === "GENERATED" || report.pdfReportStatus === "UPDATED") ? "<a onclick='downloadPDF(\"" + report.reqId + "\")' style='margin-right: 1em' href='#'>PDF</a>" : "").concat((report.excelReportStatus === "GENERATED" || report.excelReportStatus === "UPDATED") ? "<a onclick='downloadExcel(\"" + report.reqId + "\")' style='margin-right: 1em' href='#'>Excel</a>" : "").concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat("<a onclick='updateRecord(\"" + report.reqId + "\")' href='#'>Update</a>").concat("</td>");
                row = row.concat("<td class=\"text-left\">").concat("<a onclick='deleteRecord(\"" + report.reqId + "\")' href='#'>Delete</a>").concat("</td>");
                row = row.concat("</tr>");
            });
            document.getElementById("report_list_body").innerHTML = row;
        }
        else {
            alert("Refresh data error. Please contact the admin!");
        }
    }
}

function loadAll() {
    ajax(backend + "/report", "GET", null, "text", true, loadAllHandler);
}

function downloadFileHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            let blob = xmlhttp.response;
            let fileName = xmlhttp.getResponseHeader("File-Name")
            let link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = fileName;
            link.click();
        }
        else {
            alert("Download error. Please contact the admin!")
        }
    }
}

function downloadPDF(reqId) {
    ajax(backend + "/report/content/" + reqId + "/PDF", "GET", null, "blob", true, downloadFileHandler);
}

function downloadExcel(reqId) {
    ajax(backend + "/report/content/" + reqId + "/EXCEL", "GET", null, "blob", true, downloadFileHandler);
}

function updateRecordHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            document.getElementById("input_update_data").value = JSON.stringify(JSON.parse(xmlhttp.responseText).data, null, 4);
            showUpdateModal();
        }
        else {
            alert("Fetch old report error. Please contact the admin!")
        }
    }
}

function updateRecord(reqId) {
    document.getElementById("update_report").setAttribute("onClick", "updateReport('" + reqId + "')");
    ajax(backend + "/report/" + reqId, "GET", null, "text", true, updateRecordHandler)
}

function deleteRecordHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            loadAll();
        }
        else {
            alert("Delete error. Please contact the admin!")
        }
    }
}

function deleteRecord(reqId) {
    if (confirm("Are you sure to delete report?")) {
        ajax(backend + "/report/" + reqId, "DELETE", null, "text", true, deleteRecordHandler)
    }
}

function updateReportHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            hideUpdateModal();
            loadAll();
        }
        else {
            alert(xmlhttp.responseText);
        }
    }
}

function updateReport(id) {
    let data = validateInput(document.getElementById("input_update_data").value);
    if (data) {
        ajax(backend + "/report/" + id, "PUT", JSON.stringify(data), "text", true, updateReportHandler);
    }
}

function createReportHandler() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            hideNewModal();
            loadAll();
        }
        else {
            alert(xmlhttp.responseText);
        }
    }
}

function createReport(async) {
    let data = validateInput(document.getElementById("input_create_data").value);
    if (data) {
        ajax((async) ? backend + "/report/async" : backend + "/report/sync", "POST", JSON.stringify(data), "text", true, createReportHandler);
    }
}