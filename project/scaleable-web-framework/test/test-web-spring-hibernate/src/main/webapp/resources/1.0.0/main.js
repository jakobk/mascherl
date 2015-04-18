'use strict';

require.config({
    paths: {
        mascherl: '/mascherl/1.0.0/js/mascherl',
        jquery: '/webjars/jquery/2.1.3/jquery.min',
        history: '/webjars/historyjs/1.8.0/scripts/bundled/html4+html5/jquery.history',
        bootstrap: '/webjars/bootstrap/3.3.4/js/bootstrap.min'
    },
    shim: {
        history: {
            deps: ['jquery'],
            exports: 'History'
        }
    },
    deps: ['./webmail']
});
