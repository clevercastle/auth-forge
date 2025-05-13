package org.clevercastle.authforge.examples.springboot.springbootexample;

public class VerifyOneTimeRequest {
    private String email;
    private String oneTimePassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }
}
