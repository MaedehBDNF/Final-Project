package Shared;

import Shared.Enums.Error;
import Shared.Enums.Status;
import Shared.Enums.Title;

public class Response {
    private Status status = Status.failed;
    private Title title;
    private Object data;
    private Error error = Error.none;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void successful() {
        this.status = Status.successful;
    }
}
