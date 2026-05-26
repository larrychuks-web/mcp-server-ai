package com.saucedemo.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;

public class CheckoutTests {
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeMethod
    public void setUp() {
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
    }

    @Test(description = "TC-006: Successful checkout information entry and navigation to order overview")
    public void testSuccessfulCheckoutInfoEntry() {
        com.saucedemo.pages.LoginPage loginPage = new com.saucedemo.pages.LoginPage(page);
        com.saucedemo.pages.CartPage cartPage = new com.saucedemo.pages.CartPage(page);
        com.saucedemo.pages.CheckoutPage checkoutPage = new com.saucedemo.pages.CheckoutPage(page);

        // Step 1: Login
        loginPage.login("standard_user", "secret_sauce");
        assertThat(loginPage.isLoginSuccessful()).isTrue();

        // Step 2: Add item to cart
        cartPage.addItemToCart();
        cartPage.navigateToCart();
        assertThat(cartPage.getTotalCartItems()).isGreaterThanOrEqualTo(1);

        // Step 3: Proceed to checkout
        cartPage.clickCheckout();
        assertThat(checkoutPage.isOnCheckoutPage()).isTrue();

        // Step 4: Fill checkout form with valid data
        checkoutPage.fillCheckoutForm("John", "Doe", "12345");
        checkoutPage.clickContinue();

        // Step 5: Verify navigation to overview page
        com.saucedemo.pages.CheckoutOverviewPage overviewPage = new com.saucedemo.pages.CheckoutOverviewPage(page);
        assertThat(overviewPage.isOnOverviewPage()).isTrue();
    }

    @Test(description = "TC-015: Successful order completion with confirmation page")
    public void testSuccessfulOrderCompletion() {
        com.saucedemo.pages.LoginPage loginPage = new com.saucedemo.pages.LoginPage(page);
        com.saucedemo.pages.CartPage cartPage = new com.saucedemo.pages.CartPage(page);
        com.saucedemo.pages.CheckoutPage checkoutPage = new com.saucedemo.pages.CheckoutPage(page);
        com.saucedemo.pages.CheckoutOverviewPage overviewPage = new com.saucedemo.pages.CheckoutOverviewPage(page);
        com.saucedemo.pages.OrderConfirmationPage confirmationPage = new com.saucedemo.pages.OrderConfirmationPage(page);

        // Complete checkout flow
        loginPage.login("standard_user", "secret_sauce");
        cartPage.addItemToCart();
        cartPage.navigateToCart();
        cartPage.clickCheckout();
        checkoutPage.fillCheckoutForm("Jane", "Smith", "54321");
        checkoutPage.clickContinue();
        
        assertThat(overviewPage.isOnOverviewPage()).isTrue();
        overviewPage.clickFinish();
        
        assertThat(confirmationPage.isOnConfirmationPage()).isTrue();
        assertThat(confirmationPage.isConfirmationMessageVisible()).isTrue();
    }
}
