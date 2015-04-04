$(document).ready(function() {
    addNavigationHandlers("body");
});

window.fireHistoryChange = true;

function navigate(url, container, page) {
    $.ajax({
        url: url,
        type: "GET",
        data: {
            "m-container": container,
            "m-page": page,
            "m-app-version": window.mascherl.applicationVersion
        },
        dataType: "html",
        success: function (data, status, xhr) {
            container = xhr.getResponseHeader("X-Mascherl-Container");

            if (container !== "dialog-content") {
                $("div.modal-backdrop").remove();
            }

            var containerDiv = $("#" + container);
            containerDiv.html(data);
            containerDiv.attr("m-page", xhr.getResponseHeader("X-Mascherl-Page"));
            addNavigationHandlers("#" + container);
            document.title = xhr.getResponseHeader("X-Mascherl-Title");
        },
        error: function (xhr, status, errorThrown) {
            if (xhr.status === 409) {
                // navigate the whole browser page to the new url
                window.location.href = url;
            } else {
                var containerDiv = $("#main");
                containerDiv.html($("#error").html());
                containerDiv.attr("m-page", "Error");
                History.replaceState({error: true}, null, History.getState().url);
                document.title = "Error";
            }
        }
    });
}

function submitForm(url, form, container, page) {
    $.ajax({
        url: url,
        data: $("#" + form).serialize()
            + "&m-form=" + form
            + "&m-container=" + container
            + "&m-app-version=" + window.mascherl.applicationVersion
            + (typeof page === "undefined" ? "" : "&m-page=" + page),
        type: "POST",
        dataType: "html",
        //contentType: "application/json; charset=UTF-8",
        success: function (data, status, xhr) {
            container = xhr.getResponseHeader("X-Mascherl-Container");

            if (xhr.getResponseHeader("X-Mascherl-Url") !== "") {
                window.fireHistoryChange = false;
                History.pushState({"container": container}, null, xhr.getResponseHeader("X-Mascherl-Url"));
                window.fireHistoryChange = true;
            }

            var containerDiv = $("#" + container);
            containerDiv.html(data);
            containerDiv.attr("m-page", xhr.getResponseHeader("X-Mascherl-Page"));
            addNavigationHandlers("#" + container);
            document.title = xhr.getResponseHeader("X-Mascherl-Title");
        },
        error: function (xhr, status, errorThrown) {
            if (xhr.status === 409) {
                window.alert(xhr.responseText);
                window.location.reload(true);
            } else {
                var containerDiv = $("#main");
                containerDiv.html($("#error").html());
                containerDiv.attr("m-page", "Error");
                History.replaceState({error: true}, null, History.getState().url);
                document.title = "Error";
            }
        }
    });
}

function addNavigationHandlers(id) {
    $(id + " a:not([m-ignore])").click(function(event) {
        var container = $(this).attr("m-container");
        if (typeof container === "undefined") {
            container = "main";
        }
        var shouldTriggerManualStateChange = (event.target.href === History.getState().url); // not a new URL, thus no history change event
        History.pushState({"container": container}, null, event.target.href);
        if (shouldTriggerManualStateChange) {
            onStateChange();   // trigger state change manually
        }
        return false; // stop handling event
    });

    $(id + " form:not([m-ignore])").submit(function(event) {
        var container = $(this).attr("m-container");
        if (typeof container === "undefined") {
            container = "main";
        }
        var navMeta = calculateNavigationMeta(container);
        submitForm(event.target.action, $(this).attr("id"), navMeta.container, navMeta.page);
        return false;  // stop handling event
    });
}

History.Adapter.bind(window, 'statechange', function() {
    onStateChange();
});

function onStateChange() {
    if (!window.fireHistoryChange || History.getState().data.error === true) {
        return;
    }

    var navMeta = calculateNavigationMeta(History.getState().data.container);
    navigate(History.getState().url, navMeta.container, navMeta.page);
}

function calculateNavigationMeta(container) {
    var page;
    if (typeof container === "undefined") {
        container = "main";
    } else if (container !== "main") {
        var containerDiv = $("#" + container);
        if (containerDiv.length === 0) {
            container = "main";
        } else {
            // container found in DOM, but page may not necessarily fit, this must be decided by the server
            page = containerDiv.attr("m-page");
        }
    }
    return {page: page, container: container};
}