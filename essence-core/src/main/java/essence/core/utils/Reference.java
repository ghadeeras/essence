package essence.core.utils;

public class Reference<T> {

    private T value = null;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

}
