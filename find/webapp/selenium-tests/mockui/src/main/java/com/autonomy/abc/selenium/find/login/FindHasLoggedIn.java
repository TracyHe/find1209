package com.autonomy.abc.selenium.find.login;

import com.autonomy.abc.selenium.login.SSOFailureException;
import com.hp.autonomy.frontend.selenium.login.HasLoggedIn;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FindHasLoggedIn implements HasLoggedIn {
    private final WebDriver driver;

    public FindHasLoggedIn(final WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public boolean hasLoggedIn() {
        try {
            new WebDriverWait(driver, 30)
                    .withMessage("logging in to Find")
                    .until(ExpectedConditions.visibilityOfElementLocated(By.className("find-pages-container")));
            return true;
        } catch (final Exception e) {
            if (signedInTextVisible()) {
                throw new SSOFailureException();
            }
        }
        return false;
    }

    private boolean signedInTextVisible() {
        return !driver.findElements(By.xpath("//*[text()='Signed in']")).isEmpty();
    }
}
