package springboot_cntt2.it211_rikkeibank.exception;
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
