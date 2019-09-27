package com.lfm.rpc.core.exception;

import java.io.Serializable;

/**
 * @author lifengming
 * @since 2019.09.17
 */
public class LfmException extends RuntimeException  {

    private static final long serialVersionUID = -3594448263939459024L;

    public LfmException(String message) { super(message); }

    public LfmException(String message, Throwable cause) { super(message, cause); }

    public LfmException(Throwable cause) { super(cause); }

}
