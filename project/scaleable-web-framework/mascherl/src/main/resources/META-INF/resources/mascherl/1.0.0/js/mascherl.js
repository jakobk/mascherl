$(document).ready(function() {
    addNavigationHandlers("body");
});

window.mascherl = window.mascherl || {};
window.mascherl.handleHistoryChange = true;

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
        cache: false,
        success: function (data, status, xhr) {
            container = xhr.getResponseHeader("X-Mascherl-Container");

            $(window).triggerHandler("mascherlresponse", container);

            if (xhr.getResponseHeader("X-Mascherl-Url") != null) {
                window.mascherl.handleHistoryChange = false;
                History.replaceState({"container": container}, null, xhr.getResponseHeader("X-Mascherl-Url"));
                window.mascherl.handleHistoryChange = true;
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

function submitData(url, data, container) {
    if (typeof container === "undefined" || container === "") {
        container = "main";
    }
    $.ajax({
        url: url,
        headers: {
            "X-Mascherl-Container": container,
            "X-Mascherl-App-Version": window.mascherl.applicationVersion
        },
        data: data,
        type: "POST",
        dataType: "html",
        cache: false,
        success: function (data, status, xhr) {
            container = xhr.getResponseHeader("X-Mascherl-Container");

            $(window).triggerHandler("mascherlresponse", container);

            if (xhr.getResponseHeader("X-Mascherl-Url") != null) {
                window.mascherl.handleHistoryChange = false;
                History.pushState({"container": container}, null, xhr.getResponseHeader("X-Mascherl-Url"));
                window.mascherl.handleHistoryChange = true;
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
        submitData(event.target.action, $(this).serialize(), navMeta.container);
        return false;  // stop handling event
    });
}

History.Adapter.bind(window, 'statechange', function() {
    onStateChange();
});

function onStateChange() {
    if (!window.mascherl.handleHistoryChange || History.getState().data.error === true) {
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