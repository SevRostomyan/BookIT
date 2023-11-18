package com.bookit.bookit.enums;

//Arbetsstatus från städarens perspektiv
public enum CleaningReportStatus {
    NOT_ASSIGNED,
    NOT_STARTED,        // The cleaner has not started the cleaning
    IN_PROGRESS,        // The cleaner is currently performing the cleaning
    REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW, // The cleaner reports that the cleaning is done, awaiting customer approval
    REVIEW_APPROVED,    // The cleaning has been reviewed by the customer and it is approved
    REVIEW_FAILED     // The cleaning has been reviewed by the customer and it is failed

}
