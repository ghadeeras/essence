package essence.jdbc.mapping;

import essence.core.basic.UniqueMember;
import essence.core.utils.MemberNaming;
import essence.jdbc.View.TableAlias;
import essence.jdbc.mapping.columns.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Table<T> {

    final TableAlias<T> defaultAlias;
    final MemberNaming<Table<T>, Column> columnNaming = new MemberNaming<>(this, Column.class);

    private final String name;
    private final Supplier<T> entityConstructor;
    private final Collection<Column<T, ?>> columns = new ArrayList<>();

    public Table(String name, Supplier<T> entityConstructor) {
        this.name = name;
        this.defaultAlias = as(name.substring(0, 1));
        this.entityConstructor = entityConstructor;
    }

    protected <V, C extends Column<T, V>> AccessSpecifier<T, V, C> mandatory(ColumnConstructor<T, V, C> constructor) {
        return column(constructor, true);
    }

    protected <V, C extends Column<T, V>> AccessSpecifier<T, V, C> optional(ColumnConstructor<T, V, C> constructor) {
        return column(constructor, false);
    }

    protected ColumnConstructor<T, String, ColVarChar<T>> varchar(int maxLength) {
        return (table, mandatory, getter, setter) -> new ColVarChar<>(table, mandatory, getter, setter, maxLength);
    }

    protected ColumnConstructor<T, BigDecimal, ColDecimal<T>> decimal(int precision, int scale) {
        return (table, mandatory, getter, setter) -> new ColDecimal<>(table, mandatory, getter, setter, precision, scale);
    }

    protected ColumnConstructor<T, Integer, ColInteger<T>> integer() {
        return ColInteger::new;
    }

    protected ColumnConstructor<T, Long, ColBigInteger<T>> bigInteger() {
        return ColBigInteger::new;
    }

    protected ColumnConstructor<T, Boolean, ColFlag<T>> flag() {
        return ColFlag::new;
    }

    private <V, C extends Column<T, V>> AccessSpecifier<T, V, C> column(ColumnConstructor<T, V, C> constructor, boolean mandatory) {
        return (getter, setter) -> addColumn(constructor.construct(this, mandatory, getter, setter));
    }

    private <V, C extends Column<T, V>> C addColumn(C column) {
        columns.add(column);
        return column;
    }

    public Collection<Column<T, ?>> getColumns() {
        return columns;
    }

    public String getName() {
        return name;
    }

    public Supplier<T> getEntityConstructor() {
        return entityConstructor;
    }

    public TableAlias<T> as(String alias) {
        return  new TableAlias<>(this, alias);
    }

    public interface AccessSpecifier<T, V, C extends Column<T, V>> {

        C accessedBy(Function<T, V> getter, BiFunction<T, V, T> setter);

        default C mapsTo(UniqueMember<T, V> member) {
            return accessedBy(member.getGetter(), member.getSetter());
        }

    }

    interface ColumnConstructor<T, V, C extends Column<T, V>> {

        C construct(Table<T> table, boolean mandatory, Function<T, V> getter, BiFunction<T, V, T> setter);

    }

}
