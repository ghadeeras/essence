package essence.core.utils;

import essence.core.basic.Member;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public interface Query<T, K> {

    Member<T, ?, K> key();

    List<T> find(Collection<K> keys);

    @SuppressWarnings("unused")
    default List<T> find(K... keys) {
        return find(Stream.of(keys).collect(toList()));
    }

    default <TChild, KChild> Query<T, K> leftJoin(Query<TChild, KChild> childQuery, Member<T, ?, TChild> childRelation, Member<T, ?, KChild> childKey) {
        return defineFindBy(key(), keys -> {
            List<T> results = find(keys);
            Set<KChild> childKeys = results.stream().flatMap(childKey::valuesFrom).collect(toSet());
            List<TChild> childs = childQuery.find(childKeys);
            Map<KChild, List<TChild>> indexedChilds = childQuery.key().index(childs);
            Function<Stream<KChild>, Stream<TChild>> lookupFunction = ks -> ks.flatMap(k -> indexedChilds.get(k).stream());
            UnaryOperator<T> joinFunction = entity -> childRelation.update(entity, lookupFunction.apply(childKey.valuesFrom(entity)));
            return results.stream().map(joinFunction).collect(toList());
        });
    }

    static <T, K> Query<T, K> defineFindBy(final Member<T, ?, K> key, final Function<Collection<K>, List<T>> findFunction) {
        return new Query<T, K>() {

            @Override
            public Member<T, ?, K> key() {
                return key;
            }

            @Override
            public List<T> find(Collection<K> keys) {
                return findFunction.apply(keys);
            }

        };
    }

    static <T> Query<T, ?> query(final Supplier<List<T>> findFunction) {
        return defineFindBy(null, keys -> findFunction.get());
    }

}
