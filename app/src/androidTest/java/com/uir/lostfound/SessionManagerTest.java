package com.uir.lostfound;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.uir.lostfound.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Phase 2 — confirms that SessionManager SharedPreferences persist across
 * Activity restarts (simulated by constructing a second SessionManager instance
 * reading the same SharedPreferences file).
 */
@RunWith(AndroidJUnit4.class)
public class SessionManagerTest {

    private Context context;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sessionManager = new SessionManager(context);
        sessionManager.clearUser(); // start each test from a clean slate
    }

    @Test
    public void notLoggedIn_initially() {
        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getStudentId());
        assertNull(sessionManager.getName());
    }

    @Test
    public void saveUser_persistsAcrossNewInstances() {
        sessionManager.saveUser("STU001", "Omar");

        // Simulate an Activity restart: a brand-new SessionManager reads the same prefs file
        SessionManager fresh = new SessionManager(context);

        assertEquals("STU001", fresh.getStudentId());
        assertEquals("Omar", fresh.getName());
        assertTrue(fresh.isLoggedIn());
    }

    @Test
    public void clearUser_removesSessionFromPrefs() {
        sessionManager.saveUser("STU001", "Omar");
        sessionManager.clearUser();

        SessionManager fresh = new SessionManager(context);

        assertFalse(fresh.isLoggedIn());
        assertNull(fresh.getStudentId());
        assertNull(fresh.getName());
    }

    @Test
    public void saveUser_overwritesPreviousValues() {
        sessionManager.saveUser("STU001", "Omar");
        sessionManager.saveUser("STU002", "Fatima");

        SessionManager fresh = new SessionManager(context);

        assertEquals("STU002", fresh.getStudentId());
        assertEquals("Fatima", fresh.getName());
    }

    @After
    public void tearDown() {
        sessionManager.clearUser(); // leave prefs clean for other tests
    }
}
