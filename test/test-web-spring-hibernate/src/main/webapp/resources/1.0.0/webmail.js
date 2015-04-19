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

define(['jquery', 'history', 'mascherl', 'bootstrap'], function($, History, mascherl) {

    $(document).ready(function () {
        $(window).bind("mascherlresponse", function(event, data) {
            if (data.container !== "dialogContent" && data.container !== "stateContainer" && data.container !== "dialogMessages") {
                $("div.modal-backdrop").remove();
            }

            if (data.pageGroup === "MailComposePage") {
                composePageLoaded();
            } else if (data.pageGroup === "MailDetailPage") {
                detailPageLoaded();
            } else if (data.pageGroup === "MailInboxPage") {
                inboxPageLoaded();
            } else if (data.pageGroup === "SignUpPage") {
                signUpPageLoaded(data.container);
            }
        });

        mascherl.boot();
    });

    function composePageLoaded() {
        $("#backBtn").click(function() {
            var formData = $("#composeForm").serialize();

            var lastState = History.getStateByIndex(-2);
            if (typeof lastState !== "undefined") {
                formData += "&returnTo=" + encodeURIComponent(History.getShortUrl(lastState.url));
            }

            mascherl.submitData("/mail/save/" + $(this).attr("data-uuid"), formData, "content");
        });
    }

    function detailPageLoaded() {
        $("#deleteBtn").click(function() {
            var lastState = History.getStateByIndex(-2);
            var data = $.makeArray([]);
            if (typeof lastState !== "undefined") {
                data.push("returnTo=" + History.getShortUrl(lastState.url));
            }
            mascherl.submitData("/mail/" + $(this).attr("data-uuid") + "/delete", data.join("&"), "content");
        });
    }

    function inboxPageLoaded() {
        $("#refreshBtn").click(function() {
            mascherl.navigate(window.location.href, "content");
        });
        $("#composeBtn").click(function() {
            mascherl.submitData("/mail/compose", "", "content");
        });
        $("#deleteBtn").click(function() {
            var data = $.map($("input.messageSelect:checked"), function (checkbox, i) {
                return "mailUuid=" + $(checkbox).attr("data-uuid");
            });
            var pageParameter = getParameterByName("page");
            if (pageParameter !== "") {
                data.push("page=" + pageParameter);
            }
            data.push("mailType=" + $(this).attr("data-mail-type"));
            mascherl.submitData("/mail/delete", data.join("&"), "content");
        });

        $("#messageSelectAll").click(function() {
            $(".messageSelect").prop("checked", $(this).prop("checked"));
        });
        $(".messageSelect").click(function() {
            $("#messageSelectAll").prop("checked", false);
        });
    }

    function signUpPageLoaded(container) {
        if (container === "dialog" || container === "main") {
            $("div.modal-backdrop").remove();
            var dialog = $('#signUpDialog');
            dialog.modal('show');
            dialog.on('hide.bs.modal', function (e) {
                $("div.modal-backdrop").remove();
                History.pushState({"container": "dialog"}, null, "/");
            })
        }

        $('#inputCountry').change(function() {
            mascherl.submitData('/signup/selectCountry', $(this).serialize(), 'stateContainer')
        });
    }

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

});