package com.metova.android.test.service.user;

import com.metova.android.service.user.UserService;
import com.metova.android.test.MainActivityTest;
import com.metova.android.util.http.response.Response;

public class UserServiceTest extends MainActivityTest {

    public void testAuthenticationWithValidCredentials() {

        Response response = UserService.authenticate( "xyz@123.com", "thisIsATest" );
        assertTrue( response.isSuccessful() );
    }
}
