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
({
    paths: {
        webmail: '${project.build.sourceDirectory}/../webapp/resources/1.0.0/webmail',
        mailCompose: '${project.build.sourceDirectory}/../webapp/resources/1.0.0/mailCompose',
        mailDetail: '${project.build.sourceDirectory}/../webapp/resources/1.0.0/mailDetail',
        mailInbox: '${project.build.sourceDirectory}/../webapp/resources/1.0.0/mailInbox',
        mascherl: '${project.build.directory}/mascherl/META-INF/resources/mascherl/1.0.0/js/mascherl',
        jquery: '${project.build.directory}/webjars/META-INF/resources/webjars/jquery/2.1.3/jquery.min',
        history: '${project.build.directory}/webjars/META-INF/resources/webjars/historyjs/1.8.0/scripts/bundled/html5/jquery.history',
        bootstrap: '${project.build.directory}/webjars/META-INF/resources/webjars/bootstrap/3.3.4/js/bootstrap.min'
    },
    shim: {
        history: {
            deps: ['jquery'],
            exports: 'History'
        },
        bootstrap: {
            deps: ['jquery']
        }
    },
    baseUrl: '${project.build.sourceDirectory}/../webapp/resources/1.0.0/',
    deps: ['webmail', 'mailCompose', 'mailDetail', 'mailInbox'],
    wrapShim: false,
    optimize: 'uglify2'
})