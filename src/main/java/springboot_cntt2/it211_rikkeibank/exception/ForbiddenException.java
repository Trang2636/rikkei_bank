package springboot_cntt2.it211_rikkeibank.exception;
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
