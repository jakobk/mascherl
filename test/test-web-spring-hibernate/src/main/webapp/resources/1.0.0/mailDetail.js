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
define('mailDetail', ['jquery', 'mascherl', 'history'], function($, mascherl, History) {

    return {
        pageContentLoaded : function() {
            $("#deleteBtn").click(function() {
                var lastState = History.getStateByIndex(-2);
                var data = $.makeArray([]);
                if (typeof lastState !== "undefined") {
                    data.push("returnTo=" + History.getShortUrl(lastState.url));
                }
                mascherl.submitData("/mail/" + $(this).attr("data-uuid") + "/delete", data.join("&"), "content");
            });
        }
    };

});