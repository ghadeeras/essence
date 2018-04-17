package essence.core.basic;

public class MemberData<T> {

    private final String name;
    private final DataType<T> type;
    private final int minMultiplicity;
    private final int maxMultiplicity;

    public MemberData(String name, DataType<T> type, int minMultiplicity, int maxMultiplicity) {
        this.name = name;
        this.type = type;
        this.minMultiplicity = minMultiplicity;
        this.maxMultiplicity = maxMultiplicity;
    }

    public String getName() {
        return name;
    }

    public DataType<T> getType() {
        return type;
    }

    public int getMinMultiplicity() {
        return minMultiplicity;
    }

    public int getMaxMultiplicity() {
        return maxMultiplicity;
    }

}
