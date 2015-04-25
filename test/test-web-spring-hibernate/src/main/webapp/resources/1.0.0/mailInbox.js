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
define('mailInbox', ['jquery', 'mascherl', 'webmail'], function($, mascherl, webmail) {

    return {
        pageContentLoaded : function() {
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
                var pageParameter = webmail.getParameterByName("page");
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
    };

});