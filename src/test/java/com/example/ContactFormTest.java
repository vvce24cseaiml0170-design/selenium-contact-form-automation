package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium WebDriver automation test suite for HTML Contact Form.
 * Executed using JUnit 5 (Jupiter) with explicit waits and headless Chrome support.
 */
public class ContactFormTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String pageUrl;

    // Element Locators
    private final By nameInputLocator = By.id("name");
    private final By emailInputLocator = By.id("email");
    private final By messageInputLocator = By.id("message");
    private final By submitButtonLocator = By.id("submitBtn");
    private final By successMessageLocator = By.id("successMessage");
    private final By nameErrorLocator = By.id("nameError");
    private final By emailErrorLocator = By.id("emailError");
    private final By messageErrorLocator = By.id("messageError");

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Default to headless execution for CI/CD and automated test stability
        String headlessProp = System.getProperty("headless", "true");
        boolean isCI = System.getenv("CI") != null;
        if ("true".equalsIgnoreCase(headlessProp) || isCI) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Resolve absolute URL for the local contact form webpage
        File htmlFile = Paths.get("webpage", "index.html").toFile();
        if (!htmlFile.exists()) {
            htmlFile = new File(System.getProperty("user.dir"), "webpage/index.html");
        }
        pageUrl = htmlFile.toURI().toString();
        driver.get(pageUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("TEST CASE 1: Verify Contact Form Display")
    public void testContactFormDisplay() {
        // 1. Verify Page Title
        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Page title should not be null.");
        assertTrue(pageTitle.contains("Contact Form"), "Page title should contain 'Contact Form'. Actual: " + pageTitle);

        // 2. Verify Name field is displayed
        WebElement nameField = driver.findElement(nameInputLocator);
        assertTrue(nameField.isDisplayed(), "Name input field must be displayed.");

        // 3. Verify Email field is displayed
        WebElement emailField = driver.findElement(emailInputLocator);
        assertTrue(emailField.isDisplayed(), "Email input field must be displayed.");

        // 4. Verify Message field is displayed
        WebElement messageField = driver.findElement(messageInputLocator);
        assertTrue(messageField.isDisplayed(), "Message textarea field must be displayed.");

        // 5. Verify Submit button is displayed
        WebElement submitButton = driver.findElement(submitButtonLocator);
        assertTrue(submitButton.isDisplayed(), "Submit button must be displayed.");
        assertEquals("Submit", submitButton.getText().trim(), "Submit button text should be 'Submit'.");
    }

    @Test
    @DisplayName("TEST CASE 2: Successful Form Submission")
    public void testSuccessfulFormSubmission() {
        // 1. Fill out all required fields with valid input
        WebElement nameField = driver.findElement(nameInputLocator);
        WebElement emailField = driver.findElement(emailInputLocator);
        WebElement messageField = driver.findElement(messageInputLocator);
        WebElement submitButton = driver.findElement(submitButtonLocator);

        nameField.sendKeys("Jane Doe");
        emailField.sendKeys("jane.doe@example.com");
        messageField.sendKeys("Hello! This is a valid automated contact inquiry message.");

        // 2. Submit the form
        submitButton.click();

        // 3. Wait for success message to become visible and assert text
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessageLocator));
        assertTrue(successAlert.isDisplayed(), "Success message banner should be visible.");
        assertEquals("Your message has been submitted successfully!", successAlert.getText().trim());

        // 4. Ensure no error messages are displayed
        WebElement nameError = driver.findElement(nameErrorLocator);
        WebElement emailError = driver.findElement(emailErrorLocator);
        WebElement messageError = driver.findElement(messageErrorLocator);

        assertFalse(nameError.isDisplayed(), "Name error should not be displayed after successful submission.");
        assertFalse(emailError.isDisplayed(), "Email error should not be displayed after successful submission.");
        assertFalse(messageError.isDisplayed(), "Message error should not be displayed after successful submission.");
    }

    @Test
    @DisplayName("TEST CASE 3: Empty Form Validation")
    public void testEmptyFormValidation() {
        // 1. Leave all fields empty and click submit
        WebElement submitButton = driver.findElement(submitButtonLocator);
        submitButton.click();

        // 2. Verify success message is NOT displayed
        WebElement successAlert = driver.findElement(successMessageLocator);
        assertFalse(successAlert.isDisplayed(), "Success message should NOT be displayed when fields are empty.");

        // 3. Verify all three field validation messages
        WebElement nameError = wait.until(ExpectedConditions.visibilityOfElementLocated(nameErrorLocator));
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(emailErrorLocator));
        WebElement messageError = wait.until(ExpectedConditions.visibilityOfElementLocated(messageErrorLocator));

        assertTrue(nameError.isDisplayed(), "Name validation message must be displayed.");
        assertEquals("Name is required.", nameError.getText().trim());

        assertTrue(emailError.isDisplayed(), "Email validation message must be displayed.");
        assertEquals("Email is required.", emailError.getText().trim());

        assertTrue(messageError.isDisplayed(), "Message validation message must be displayed.");
        assertEquals("Message is required.", messageError.getText().trim());
    }

    @Test
    @DisplayName("TEST CASE 4: Empty Name Validation")
    public void testEmptyNameValidation() {
        // 1. Fill valid Email and Message, leaving Name empty
        driver.findElement(emailInputLocator).sendKeys("valid.user@example.com");
        driver.findElement(messageInputLocator).sendKeys("Test message with missing name.");

        // 2. Submit the form
        driver.findElement(submitButtonLocator).click();

        // 3. Verify form not submitted successfully
        WebElement successAlert = driver.findElement(successMessageLocator);
        assertFalse(successAlert.isDisplayed(), "Success message must not be shown when Name is missing.");

        // 4. Verify Name validation message is displayed
        WebElement nameError = wait.until(ExpectedConditions.visibilityOfElementLocated(nameErrorLocator));
        assertTrue(nameError.isDisplayed(), "Name error should be visible.");
        assertEquals("Name is required.", nameError.getText().trim());

        // 5. Verify other errors are not displayed
        assertFalse(driver.findElement(emailErrorLocator).isDisplayed(), "Email error should not be shown.");
        assertFalse(driver.findElement(messageErrorLocator).isDisplayed(), "Message error should not be shown.");
    }

    @Test
    @DisplayName("TEST CASE 5: Empty Email Validation")
    public void testEmptyEmailValidation() {
        // 1. Fill valid Name and Message, leaving Email empty
        driver.findElement(nameInputLocator).sendKeys("John Smith");
        driver.findElement(messageInputLocator).sendKeys("Test message with missing email.");

        // 2. Submit the form
        driver.findElement(submitButtonLocator).click();

        // 3. Verify form not submitted successfully
        WebElement successAlert = driver.findElement(successMessageLocator);
        assertFalse(successAlert.isDisplayed(), "Success message must not be shown when Email is missing.");

        // 4. Verify Email validation message is displayed
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(emailErrorLocator));
        assertTrue(emailError.isDisplayed(), "Email error should be visible.");
        assertEquals("Email is required.", emailError.getText().trim());

        // 5. Verify other errors are not displayed
        assertFalse(driver.findElement(nameErrorLocator).isDisplayed(), "Name error should not be shown.");
        assertFalse(driver.findElement(messageErrorLocator).isDisplayed(), "Message error should not be shown.");
    }

    @Test
    @DisplayName("TEST CASE 6: Empty Message Validation")
    public void testEmptyMessageValidation() {
        // 1. Fill valid Name and Email, leaving Message empty
        driver.findElement(nameInputLocator).sendKeys("Alice Johnson");
        driver.findElement(emailInputLocator).sendKeys("alice@example.com");

        // 2. Submit the form
        driver.findElement(submitButtonLocator).click();

        // 3. Verify form not submitted successfully
        WebElement successAlert = driver.findElement(successMessageLocator);
        assertFalse(successAlert.isDisplayed(), "Success message must not be shown when Message is missing.");

        // 4. Verify Message validation message is displayed
        WebElement messageError = wait.until(ExpectedConditions.visibilityOfElementLocated(messageErrorLocator));
        assertTrue(messageError.isDisplayed(), "Message error should be visible.");
        assertEquals("Message is required.", messageError.getText().trim());

        // 5. Verify other errors are not displayed
        assertFalse(driver.findElement(nameErrorLocator).isDisplayed(), "Name error should not be shown.");
        assertFalse(driver.findElement(emailErrorLocator).isDisplayed(), "Email error should not be shown.");
    }

    @Test
    @DisplayName("TEST CASE 7: Invalid Email Validation")
    public void testInvalidEmailValidation() {
        // 1. Enter valid Name, invalid Email format, and valid Message
        driver.findElement(nameInputLocator).sendKeys("Bob Martin");
        driver.findElement(emailInputLocator).sendKeys("not-a-valid-email-format");
        driver.findElement(messageInputLocator).sendKeys("Testing invalid email address validation handling.");

        // 2. Submit the form
        driver.findElement(submitButtonLocator).click();

        // 3. Verify form not submitted successfully
        WebElement successAlert = driver.findElement(successMessageLocator);
        assertFalse(successAlert.isDisplayed(), "Success message must not be displayed when Email is invalid.");

        // 4. Verify email validation error is displayed with proper guidance
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(emailErrorLocator));
        assertTrue(emailError.isDisplayed(), "Invalid email error must be displayed.");
        assertEquals("Please enter a valid email address.", emailError.getText().trim());

        // 5. Verify name and message errors are not displayed
        assertFalse(driver.findElement(nameErrorLocator).isDisplayed(), "Name error should not be shown.");
        assertFalse(driver.findElement(messageErrorLocator).isDisplayed(), "Message error should not be shown.");
    }
}
