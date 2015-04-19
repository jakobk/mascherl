/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
define('mascherl', ['jquery', 'history'], function($, History) {

    var mascherl = {};

    mascherl.handleHistoryChange = true;

    mascherl.boot = function() {
        History.Adapter.bind(window, 'statechange', function () {
            mascherl.onStateChange();
        });

        mascherl.addNavigationHandlers("body");

        if (typeof window.mascherlConfig.replaceUrl !== "undefined" && window.mascherlConfig.replaceUrl !== "") {
            mascherl.handleHistoryChange = false;
            History.replaceState({"container": "main"}, document.title, window.mascherlConfig.replaceUrl);
            mascherl.handleHistoryChange = true;
        }

        $(window).triggerHandler("mascherlresponse", {
            container: "main",
            pageUrl: History.getShortUrl(History.getState().url),
            pageGroup: window.mascherlConfig.pageGroup
        });
    };

    mascherl.onStateChange = function() {
        if (!mascherl.handleHistoryChange || History.getState().data.error === true) {
            return;
        }

        var navMeta = mascherl.calculateNavigationMeta(History.getState().data.container);
        mascherl.navigate(History.getState().url, navMeta.container, navMeta.page);
    };

    mascherl.navigate = function (url, container, page) {
        $.ajax({
            url: url,
            type: "GET",
            data: {
                "m-container": container,
                "m-page": page,
                "m-app-version": window.mascherlConfig.applicationVersion
            },
            dataType: "html",
            cache: false,
            success: function (data, status, xhr) {
                container = xhr.getResponseHeader("X-Mascherl-Container");

                if (xhr.getResponseHeader("X-Mascherl-Url") != null) {
                    mascherl.handleHistoryChange = false;
                    History.replaceState({"container": container}, null, xhr.getResponseHeader("X-Mascherl-Url"));
                    mascherl.handleHistoryChange = true;
                }

                var containerDiv = $("#" + container);
                containerDiv.html(data);
                containerDiv.attr("m-page", xhr.getResponseHeader("X-Mascherl-Page"));
                mascherl.addNavigationHandlers("#" + container);
                document.title = xhr.getResponseHeader("X-Mascherl-Title");

                $(window).triggerHandler("mascherlresponse", {
                    container: container,
                    pageUrl: History.getShortUrl(History.getState().url),
                    pageGroup: xhr.getResponseHeader("X-Mascherl-Page")
                });
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
    };

    mascherl.submitData = function (url, data, container) {
        if (typeof container === "undefined" || container === "") {
            container = "main";
        }
        $.ajax({
            url: url,
            headers: {
                "X-Mascherl-Container": container,
                "X-Mascherl-App-Version": window.mascherlConfig.applicationVersion
            },
            data: data,
            type: "POST",
            dataType: "html",
            cache: false,
            success: function (data, status, xhr) {
                container = xhr.getResponseHeader("X-Mascherl-Container");

                if (xhr.getResponseHeader("X-Mascherl-Url") != null) {
                    mascherl.handleHistoryChange = false;
                    History.pushState({"container": container}, null, xhr.getResponseHeader("X-Mascherl-Url"));
                    mascherl.handleHistoryChange = true;
                }

                var containerDiv = $("#" + container);
                containerDiv.html(data);
                containerDiv.attr("m-page", xhr.getResponseHeader("X-Mascherl-Page"));
                mascherl.addNavigationHandlers("#" + container);
                document.title = xhr.getResponseHeader("X-Mascherl-Title");

                $(window).triggerHandler("mascherlresponse", {
                    container: container,
                    pageUrl: History.getShortUrl(History.getState().url),
                    pageGroup: xhr.getResponseHeader("X-Mascherl-Page")
                });
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
    };

    mascherl.addNavigationHandlers = function (id) {
        $(id + " a:not([m-ignore])").click(function (event) {
            var container = $(this).attr("m-container");
            if (typeof container === "undefined") {
                container = "main";
            }
            var shouldTriggerManualStateChange = (event.target.href === History.getState().url); // not a new URL, thus no history change event
            History.pushState({"container": container}, null, event.target.href);
            if (shouldTriggerManualStateChange) {
                mascherl.onStateChange();   // trigger state change manually
            }
            return false; // stop handling event
        });

        $(id + " form:not([m-ignore])").submit(function (event) {
            var container = $(this).attr("m-container");
            if (typeof container === "undefined") {
                container = "main";
            }
            var navMeta = mascherl.calculateNavigationMeta(container);
            mascherl.submitData(event.target.action, $(this).serialize(), navMeta.container);
            return false;  // stop handling event
        });
    };

    mascherl.calculateNavigationMeta = function(container) {
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
    };

    return mascherl;
});