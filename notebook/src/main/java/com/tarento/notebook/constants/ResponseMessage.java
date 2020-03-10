package com.tarento.notebook.constants;

public enum ResponseMessage {

	USER_REGISTRATION_SUCCESS("User has been registered successfully"),
    USER_ALREADY_EXISTS("Email address is in use"),
    EMAIL_INVALID("Email is not valid. Please use another email address."),
    PASSWORD_INVALID("Password must be 8 or more characters"),
    INVALID_CREDENTIALS("Invalid Credentials!"),
    NOT_LOGGED_IN("You are not logged in!"),
    INVALID_BOOKNAME("Book name already exists"),
	ERROR("Error!"),
    SUCCESSFUL("Successful!"),
    BOOK_DOESNOT_BELONG_TO_THE_USER("Book does not belong to the user!");

    private String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
