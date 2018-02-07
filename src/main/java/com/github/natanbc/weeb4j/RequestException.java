package com.github.natanbc.weeb4j;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "WeakerAccess"})
public class RequestException extends RuntimeException {
    private final StackTraceElement[] callSite;

    protected RequestException(String message, StackTraceElement[] callSite) {
        super(message);
        this.callSite = callSite;
    }

    protected RequestException(String message) {
        this(message, null);
    }

    protected RequestException(Throwable cause, StackTraceElement[] callSite) {
        super(cause);
        this.callSite = callSite;
    }

    protected RequestException(Throwable cause) {
        this(cause, null);
    }


    /**
     * Returns the stack trace of the location this request was made. For blocking requests or async requests
     * with call site tracking disabled, this method returns null.
     *
     * @return null if the request was blocking or call site tracking was disabled, the call site where this request was generated otherwise.
     */
    @Nullable
    @CheckReturnValue
    public StackTraceElement[] getCallSite() {
        return callSite == null ? null : callSite.clone();
    }
}
