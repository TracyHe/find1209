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

        this.on("invalid", function (model, error) {
            console.log("Houston, we have a problem: " + error)
        });
    },
    constructor: function (attributes, options) {

        Backbone.Model.apply(this, arguments);
    },
    validate: function (attr) {
        if (!attr.username) {
            return "Invalid username supplied."
        }
    },
    urlRoot: '../api/public/rating'
});
});