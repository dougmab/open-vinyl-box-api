package com.github.dougmab.openvinylboxapi.controller.exception;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class StandardError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;

    public StandardError() {}


    /**
     * Sets the timestamp, message, and path of the error to default.
     * @param e The exception that was thrown.
     * @param request The request that was made.
     */
    public StandardError(RuntimeException e, HttpServletRequest request) {
        this.timestamp = Instant.now();
        this.error = e.getMessage();
        this.path = request.getRequestURI();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
