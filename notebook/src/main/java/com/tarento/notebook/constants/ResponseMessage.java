package com.tarento.notebook.constants;

public enum ResponseMessage {

	USER_REGISTRATION_SUCCESS("User has been registered successfully"),
    USER_ALREADY_EXISTS("Email address is in use"),
    INVALID_CREDENTIALS("Invalid Credentials!"),
    NOT_LOGGED_IN("You are not logged in!"),
    INVALID_BOOKNAME("Book name already exists"),
	ERROR("Error!"),
    INTERNAL_SERVER_ERROR("Internal Server Error!"),
    SUCCESSFUL("Successful!"),
    TAG_EXISTS_IN_NOTES("Note already has the tag!"),
    BOOK_DOESNOT_BELONG_TO_THE_USER("Book does not belong to the user!"),
    INVALID_REQUEST("Invalid Request!!");

    private String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
