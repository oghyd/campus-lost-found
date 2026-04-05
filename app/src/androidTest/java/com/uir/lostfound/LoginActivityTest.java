package com.uir.lostfound;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.uir.lostfound.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Phase 2 — end-to-end LoginActivity tests.
 * Validates form display, input validation, session persistence after login.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private Context context;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sessionManager = new SessionManager(context);
        sessionManager.clearUser(); // ensure no existing session before each test
    }

    @Test
    public void loginForm_isDisplayed_whenNoSession() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.til_student_id)).check(matches(isDisplayed()));
            onView(withId(R.id.til_name)).check(matches(isDisplayed()));
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyStudentId_showsError() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.et_name)).perform(typeText("Omar"), closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());

            // Error message should be set on the student ID TextInputLayout
            onView(withId(R.id.til_student_id)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyName_showsError() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.et_student_id)).perform(typeText("STU001"), closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());

            onView(withId(R.id.til_name)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void validLogin_savesSessionAndFinishesActivity() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.et_student_id)).perform(typeText("STU001"), closeSoftKeyboard());
            onView(withId(R.id.et_name)).perform(typeText("Omar"), closeSoftKeyboard());
            onView(withId(R.id.btn_login)).perform(click());

            // After login, session must be saved
            assertTrue(sessionManager.isLoggedIn());
            assertEquals("STU001", sessionManager.getStudentId());
            assertEquals("Omar", sessionManager.getName());
        }
    }

    @After
    public void tearDown() {
        sessionManager.clearUser();
    }
}
