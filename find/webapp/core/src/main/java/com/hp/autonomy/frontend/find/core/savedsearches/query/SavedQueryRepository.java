/*
 * (c) Copyright 2016 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.frontend.find.core.savedsearches.query;


import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearchRepository;

/**
 * Repository containing actions which can be performed on the main searches table
 * Spring automatically implements basic operations
 */
public interface SavedQueryRepository extends SavedSearchRepository<SavedQuery, SavedQuery.Builder> {
}
