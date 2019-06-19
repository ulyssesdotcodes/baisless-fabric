public class Optional<T> {
    public readonly T value;
    public readonly bool isNothing;

    private Optional(T value) {
        this.value = value;
        this.isNothing = false;
    }

    private Optional() {
        this.isNothing = true;
    }

    public static Optional<T> of(T instance) {
        return new Optional<T>(instance);
    }

    public static Optional<T> none() {
        return new Optional<T>();
    }
}