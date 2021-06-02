package org.hmcore;

public enum MessageType {

    INVALID (-1),
    BLOGPOST(0),
    TWITTER(1),
    JOB_LISTING(2),
    WEBSITE_CHANGED(3);

    MessageType(int i) {
    }

    int i;
}
