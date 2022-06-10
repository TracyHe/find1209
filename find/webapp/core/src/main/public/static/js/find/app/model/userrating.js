define([
    'backbone',
    'underscore'
], function(Backbone, _) {

    'use strict';
return Backbone.Model.extend({
    defaults: {
        username: "",
        docreferenceid: "",
        rating: ""
    },

    initialize: function () {
        console.log('Rating has been initialized');
        this.on("invalid", function (model, error) {
            console.log("Houston, we have a problem: " + error)
        });
    },
    constructor: function (attributes, options) {
        console.log('Rating\'s constructor had been called');
        Backbone.Model.apply(this, arguments);
    },
    validate: function (attr) {
        if (!attr.username) {
            return "Invalid username supplied."
        }
    },
    urlRoot: 'http://localhost:8080/api/public/rating'
});
});