package com.dhenry.lendingclubscraper.app.exceptions;

public class LendingClubException extends Exception {

    public LendingClubException() {
    }

    public LendingClubException(String detailMessage) {
        super(detailMessage);
    }

    public LendingClubException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LendingClubException(Throwable throwable) {
        super(throwable);
    }
}
