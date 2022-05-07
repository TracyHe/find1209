/*
 * (c) Copyright 2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

define([
    'underscore',
    'backbone',
    'find/app/page/search/document/document-detail-tabs',
    'find/app/util/events',
    'find/app/util/url-manipulator',
    'find/app/util/view-server-client',
    'find/app/page/search/document/document-preview-helper',
    'i18n!find/nls/bundle',
    'jquery',
    'rating',
    'text!find/templates/app/page/search/document/document-detail-content-view.html'
], function(_, Backbone, tabs, events, urlManipulator, viewClient, DocumentPreviewHelper, i18n
            ,jquery,rating,template) {
    'use strict';

    return Backbone.View.extend({
        template: _.template(template),

        events: {
            'click .document-detail-open-original-link': function() {
                // the link itself is responsible for opening the window
                events().original();
            },
            'click .document-detail-mmap-button': function() {
                this.mmapTab.open(this.documentModel.attributes);
            },
            'change.example-fontawesome-o' : function(value, text, event){
                //tracy work on this
                if (typeof(event) !== 'undefined') {
                      // rating was selected by a user
                      console.log('undefined event');
                      console.log(event.target);

                    } else {
                      // rating was selected programmatically
                      // by calling `set` method
                      console.log('by calling `set` method-1');
                      console.log(text);
                      console.log('by calling `set` method-2');
                    }
            },
            'shown.bs.tab a[data-toggle=tab]': function(event) {
                const tab = this.tabs[$(event.target).parent().index()];
                tab.view.render();
            }
        },

        initialize: function(options) {
            this.indexesCollection = options.indexesCollection;
            this.documentRenderer = options.documentRenderer;
            this.mmapTab = options.mmapTab;
            this.documentModel = options.documentModel;

            this.tabs = this.filterTabs(tabs);

        },

        render: function() {

            const url = this.documentModel.get('url');
            const documentHref = url
                ? urlManipulator.addSpecialUrlPrefix(
                    this.documentModel.get('contentType'),
                    this.documentModel.get('url'))
                : viewClient.getHref(this.documentModel, false, true);

            this.$el.html(this.template({
                i18n: i18n,
                title: this.documentModel.get('title'),
                href: documentHref,
                tabs: this.tabs,
                mmap: this.mmapTab.supported(this.documentModel.attributes)
            }));
            var currentRating = $('#example-fontawesome-o').data('current-rating');
            $('.stars-example-fontawesome-o .current-rating')
                        .find('span')
                        .html(currentRating);

                    $('.stars-example-fontawesome-o .clear-rating').on('click', function(event) {
                        event.preventDefault();

                        $('#example-fontawesome-o')
                            .barrating('clear');
                    });

                    $('#example-fontawesome-o').barrating({
                        theme: 'fontawesome-stars-o',
                        showSelectedRating: false,
                        initialRating: currentRating,
                        onSelect: function(value, text) {
                            if (!value) {
                                $('#example-fontawesome-o')
                                    .barrating('clear');
                            } else {
                                $('.stars-example-fontawesome-o .current-rating')
                                    .addClass('hidden');

                                $('.stars-example-fontawesome-o .your-rating')
                                    .removeClass('hidden')
                                    .find('span')
                                    .html(value);
                            }
                        },
                        onClear: function(value, text) {
                            $('.stars-example-fontawesome-o')
                                .find('.current-rating')
                                .removeClass('hidden')
                                .end()
                                .find('.your-rating')
                                .addClass('hidden');
                        }
                    });
            this.renderDocument();
            this.renderTabContent();
        },

        filterTabs: function(tabList) {
            return _.chain(tabList)
                .filter(function(tab) {
                    return tab.shown(this.documentModel);
                }, this)
                .map(function(tab, index) {
                    return _.extend({index: index}, tab);
                })
                .value()
        },

        renderDocument: function() {
            DocumentPreviewHelper.showPreview(
                this.$('.document-detail-view-container'), this.documentModel, null);
        },

        renderTabContent: function() {
            const $tabContentContainer = this.$('.document-detail-tabs-content');

            _.each(this.tabs, function(tab) {
                tab.view = new (tab.TabContentConstructor)({
                    tab: tab,
                    model: this.documentModel,
                    indexesCollection: this.indexesCollection,
                    documentRenderer: this.documentRenderer
                });

                $tabContentContainer.append(tab.view.$el);
            }, this);

            if(this.tabs.length !== 0) {
                this.tabs[0].view.render();
            }
        },

        remove: function() {
            _.each(this.tabs, function(tab) {
                tab.view && tab.view.remove();
            });

            Backbone.View.prototype.remove.call(this);
        }
    });
});
