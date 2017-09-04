package app.bsmuniversal.com.utils;

/**
 * Users utility
 */
public class Users {

    public static User getUserOne() {
        return new User("mera2", "pass789", "mera");
    }

    public static User getUserTwo() {
        return new User("devin", "1234", "sfm");
    }

    public static User getUserWrongUsername() {
        return new User("wrongusername", "pass789", "mera");
    }

    public static User getUserWrongPassword() {
        return new User("mera2", "wrongpassword", "mera");
    }

    public static User getUserWrongCompany() {
        return new User("mera2", "pass789", "wrongcompany");
    }

    public static User getUserEmptyPassword() {
        return new User("mera2", "", "mera");
    }

    public static User getUserEmptyCompany() {
        return new User("mera2", "pass789", "");
    }

    public static User getUserEmptyUsername() {
        return new User("", "pass789", "mera");
    }

    public static User getEmptyUser() {
        return new User("", "", "");
    }

    public static class User {

        private String username;
        private String password;
        private String domain;

        private User(String username, String password, String domain) {
            this.username = username;
            this.password = password;
            this.domain = domain;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDomain() {
            return domain;
        }
    }
}
