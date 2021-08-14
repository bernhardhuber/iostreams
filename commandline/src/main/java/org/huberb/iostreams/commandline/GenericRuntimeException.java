/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.iostreams.commandline;

/**
 * Generic {@link RuntimeException} for wrapping {@link Exception}.
 * <p>
 * Usually used in lambda expressions.
 */
class GenericRuntimeException extends RuntimeException {

    public GenericRuntimeException() {
    }

    public GenericRuntimeException(String message) {
        super(message);
    }

    public GenericRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericRuntimeException(Throwable cause) {
        super(cause);
    }
    
}
