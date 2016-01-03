package ims.fhj.at.klettertracker;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by doktordos on 20/12/15.
 */
public class LoginActivityTest {

    LoginActivity login;

    @Before
    public void setUp() throws Exception {
        login = new LoginActivity();
    }

    @org.junit.Test
    public void testIsValidUser() throws Exception {
        Assert.assertEquals(true, login.postCredentials("hoesed", "123456789"));
    }

    @org.junit.Test
    public void testIsNotValidUser() throws Exception {
        Assert.assertEquals(false, login.postCredentials("root", "password"));
    }

    @Test
    public void testButtonClicked() throws Exception {
        //Espresso net yet available on marshmellow
        //onView(withId(R.id.etInput)).perform(typeText("Hello")).check(matches(withText("Hello")));
    }

    @Test
    public void testDialog() throws Exception {
        //AlertDialog dialog = login.createDialog("testTitle", "testMessage");
        //Assert.assertEquals(dialog.);

    }

    @After
    public void tearDown() throws Exception {
        login = null;
    }
}