var webpack = require('webpack');

module.exports = function (config) {
    config.set({
        browsers: [ 'Chrome' ], // run in Chrome
        singleRun: false,
        frameworks: [ 'mocha', 'chai', 'sinon', 'sinon-chai' ], // use the mocha test framework
        files: [
            '../../angular/angular.js',
            '../node_modules/angular-mocks/angular-mocks.js',
            '../dhis2.angular.*.js',
            'tests.webpack.js', // just load this file
        ],
        preprocessors: {
            'tests.webpack.js': [ 'webpack', 'sourcemap' ], // preprocess with webpack and our sourcemap loader
            '../dhis2.angular.*.js': ['coverage'],
        },
        reporters: [ 'dots', 'coverage' ], // report results in this format
        coverageReporter: {
            type: 'lcov',
            dir: '../coverage',
            subdir: function(browser) {
                // normalization process to keep a consistent browser name accross different OS
                return browser.toLowerCase().split(/[ /-]/)[0];
            }
        },
        webpack: { // kind of a copy of your webpack config
            devtool: 'inline-source-map', // just do inline source maps instead of the default
            module: {
                loaders: [
                    { test: /\.js$/, loader: 'babel-loader' }
                ]
            }
        },
        webpackServer: {
            noInfo: true // please don't spam the console when running in karma!
        }
    });
};
