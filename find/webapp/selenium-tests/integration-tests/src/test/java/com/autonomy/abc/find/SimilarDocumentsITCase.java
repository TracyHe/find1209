package com.autonomy.abc.find;

import com.autonomy.abc.base.FindTestBase;
import com.autonomy.abc.selenium.find.FindService;
import com.autonomy.abc.selenium.find.application.FindElementFactory;
import com.autonomy.abc.selenium.find.preview.DetailedPreviewPage;
import com.autonomy.abc.selenium.find.preview.InlinePreview;
import com.autonomy.abc.selenium.find.results.DocumentViewer;
import com.autonomy.abc.selenium.find.results.FindResult;
import com.autonomy.abc.selenium.find.results.ListView;
import com.autonomy.abc.selenium.find.results.SimilarDocumentsView;
import com.autonomy.abc.selenium.query.IndexFilter;
import com.autonomy.abc.selenium.query.Query;
import com.hp.autonomy.frontend.selenium.application.ApplicationType;
import com.hp.autonomy.frontend.selenium.config.TestConfig;
import com.hp.autonomy.frontend.selenium.control.Frame;
import com.hp.autonomy.frontend.selenium.framework.logging.ActiveBug;
import com.hp.autonomy.frontend.selenium.framework.logging.RelatedTo;
import com.hp.autonomy.frontend.selenium.framework.logging.ResolvedBug;
import com.hp.autonomy.frontend.selenium.util.Waits;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.List;

import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.assertThat;
import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.verifyThat;
import static com.hp.autonomy.frontend.selenium.matchers.ControlMatchers.urlContains;
import static com.hp.autonomy.frontend.selenium.matchers.ElementMatchers.containsText;
import static com.hp.autonomy.frontend.selenium.matchers.StringMatchers.containsIgnoringCase;
import static com.hp.autonomy.frontend.selenium.matchers.StringMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assume.assumeThat;
import static org.openqa.selenium.lift.Matchers.displayed;

@RelatedTo("CSA-2090")
public class SimilarDocumentsITCase extends FindTestBase {
    private FindService findService;
    private SimilarDocumentsView similarDocuments;

    public SimilarDocumentsITCase(final TestConfig config) {
        super(config);
    }

    @Before
    public void setUp() {
        findService = getApplication().findService();
        getElementFactory().getFindPage().goToListView();
    }

    @Test
    public void testSimilarDocumentsShowUp() throws InterruptedException {
        final ListView results = findService.search(new Query("Doe"));

        for (int i = 1; i <= 5; i++) {
            final String title = results.getResult(i).getTitleString();
            similarDocuments = findService.goToSimilarDocuments(i);

            verifyThat(getDriver().getCurrentUrl(), containsString("suggest"));

            verifyThat(similarDocuments.getTitle(), allOf(containsIgnoringCase("Similar results"), containsIgnoringCase('"' + title + '"')));
            verifyThat(similarDocuments.getTotalResults(), greaterThan(0));
            verifyThat(similarDocuments.getResults(1), not(empty()));

            similarDocuments.backButton().click();
        }
    }

    @Test
    @ResolvedBug("CCUK-3678")
    public void testTitle() {
        findService.search(new Query("Bill Murray"));

        for (int i = 1; i <= 5; i++) {
            similarDocuments = findService.goToSimilarDocuments(i);
            final WebElement seedLink = similarDocuments.seedLink();
            verifyThat(seedLink, displayed());
            verifyThat(seedLink.getText(), not(isEmptyOrNullString()));
            similarDocuments.backButton().click();
            getElementFactory().getConceptsPanel().removeAllConcepts();
        }
    }

    @Test
    public void testPreviewSeed() throws InterruptedException {
        findService.search(new Query("moon"));

        for (int i = 1; i <= 5; i++) {
            Waits.loadOrFadeWait();
            similarDocuments = findService.goToSimilarDocuments(i);
            final WebElement seedLink = similarDocuments.seedLink();

            previewSeed(seedLink);
            similarDocuments.backButton().click();
        }
    }

    private void previewSeed(final WebElement seedLink) {
        seedLink.click();
        verifyThat("SeedLink goes to detailed document preview", getDriver().getCurrentUrl(), containsString("document"));
        getElementFactory().getDetailedPreview().goBackToSearch();

    }

    @Test
    @ResolvedBug("CCUK-3676")
    public void testPublicIndexesSimilarDocs() {
        assumeThat(getConfig().getType(), Matchers.is(ApplicationType.HOSTED));

        findService.search(new Query("Hammertime"));
        final FindElementFactory elementFactory = getElementFactory();
        elementFactory.getFilterPanel().indexesTreeContainer().expand();
        elementFactory.getFindPage().filterBy(IndexFilter.PUBLIC);

        for (int i = 1; i <= 5; i++) {
            verifySimilarDocsNotEmpty(i);
        }
    }

    private void verifySimilarDocsNotEmpty(final int i) {
        similarDocuments = findService.goToSimilarDocuments(i);
        verifyThat(similarDocuments.mainResultsContent().getText(), not(isEmptyOrNullString()));
        similarDocuments.backButton().click();
    }

    @Test
    public void testSimilarDocumentsFromSimilarDocuments() {
        findService.search("Self Defence Family");

        similarDocuments = findService.goToSimilarDocuments(1);
        assumeThat(similarDocuments.getResults().size(), not(0));

        String previousTitle = similarDocuments.seedLink().getText();
        for (int i = 0; i < 5; i++) {
            //Generate a random number between 1 and 5
            final int number = (int) (Math.random() * 5 + 1);

            final FindResult doc = similarDocuments.getResult(number);
            final String docTitle = doc.getTitleString();

            doc.similarDocuments().click();
            Waits.loadOrFadeWait();
            similarDocuments = getElementFactory().getSimilarDocumentsView();

            verifyThat("Going from " + previousTitle + " to " + docTitle + " worked successfully", similarDocuments.seedLink(), containsText(docTitle));

            previousTitle = docTitle;
        }
    }

    @Test
    @ResolvedBug("FIND-496")
    @ActiveBug(value = "FIND-626", type = ApplicationType.HOSTED)
    public void testInfiniteScroll() {
        findService.search(new Query("blast"));

        similarDocuments = findService.goToSimilarDocuments(1);

        final int totalNumberDocs = similarDocuments.getTotalResults();

        final int limit = 240;
        int i = 30;
        while (i <= limit && i <= totalNumberDocs) {
            verifyThat(similarDocuments.getVisibleResultsCount(), is(i));
            final DocumentViewer documentViewer = similarDocuments.getResult(i).openDocumentPreview();
            assertThat("Have opened preview container", documentViewer.previewPresent());
            documentViewer.close();
            verifyThat(similarDocuments.getVisibleResultsCount(), anyOf(is(i + 30), is(totalNumberDocs)));
            similarDocuments.waitForLoad();

            i += 30;
        }
    }

    @Test
    public void testSortByDate() throws ParseException {
        assumeThat(getConfig().getType(), Matchers.is(ApplicationType.ON_PREM));

        findService.search(new Query("Fade"));
        similarDocuments = findService.goToSimilarDocuments(1);
        Waits.loadOrFadeWait();
        similarDocuments.sortByDate();
        final List<FindResult> searchResults = similarDocuments.getResults();

        final ZonedDateTime timeNow = ZonedDateTime.now();

        ZonedDateTime previousDate = null;
        for (int i = 0; i <= 10; i++) {
            final ZonedDateTime currentDate = searchResults.get(i).convertRelativeDate(timeNow);

            if (previousDate != null) {
                verifyThat(currentDate, lessThanOrEqualTo(previousDate));
            }

            previousDate = currentDate;
        }
    }

    @Test
    @ResolvedBug("FIND-496")
    @ActiveBug(value = "FIND-626", type = ApplicationType.HOSTED)
    public void testDocumentPreview() {
        findService.search(new Query("stars"));
        similarDocuments = findService.goToSimilarDocuments(1);
        testDocPreview(similarDocuments.getResults(5));
    }

    private void testDocPreview(final Iterable<FindResult> results) {
        for (final FindResult result : results) {
            final DocumentViewer docPreview = result.openDocumentPreview();

            assertThat("Have opened preview container", docPreview.previewPresent());
            verifyThat("Preview not stuck loading", !similarDocuments.loadingIndicator().isDisplayed());
            verifyThat("Index displayed", docPreview.getIndexName(), not(nullValue()));
            verifyThat("Reference displayed", docPreview.getReference(), not(nullValue()));

            final Frame previewFrame = new Frame(getDriver(), docPreview.frame());
            final String frameText = previewFrame.getText();

            verifyThat("Preview document has content", previewFrame.operateOnContent(WebElement::getTagName), Matchers.is("body"));
            assertThat("Preview document has no error", frameText, not(containsString("encountered an error")));

            docPreview.close();
        }
    }

    @Test
    @ResolvedBug("FIND-496")
    @ActiveBug(value = "FIND-626", type = ApplicationType.HOSTED)
    public void testDetailedDocumentPreviewFromSimilar() {
        findService.search(new Query("stars"));
        similarDocuments = findService.goToSimilarDocuments(1);

        final FindResult firstSimilar = similarDocuments.getResult(1);
        final String title = firstSimilar.getTitleString();

        final InlinePreview inlinePreview = firstSimilar.openDocumentPreview();
        final DetailedPreviewPage detailedPreviewPage = inlinePreview.openPreview();

        verifyThat("Have opened right detailed preview", detailedPreviewPage.getTitle(), equalToIgnoringCase(title));
        detailedPreviewPage.goBackToSearch();

        verifyThat("'Similar documents' results' url", getDriver().getCurrentUrl(), containsString("suggest"));
        similarDocuments = getElementFactory().getSimilarDocumentsView();
        verifyThat("Back button still exists because on similar documents", similarDocuments.backButton().isDisplayed());

    }

    @Test
    public void testNavigateBackFromSimilarDocuments() {
        findService.search("stars");
        similarDocuments = findService.goToSimilarDocuments(1);

        getDriver().navigate().back();

        verifyThat("Returned to the search page", getDriver().getCurrentUrl(), allOf(containsString("search"), not(containsString("suggest"))));
    }
}
