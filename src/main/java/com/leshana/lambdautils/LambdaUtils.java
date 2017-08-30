package com.leshana.lambdautils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of utility functions that the Java 8 JDK seems to have forgotten.
 *
 * Some of these methods are simply wrappers to prettify code. For example the syntax sugar methods are just a cleaner
 * way of casting method references or inline lambda expressions to the standard functional interfaces. Casting is
 * useful in case you want to use the default methods on the standard interfaces. Consider;
 *
 * <code>stream.filter(((Prediate<MyClass>)MyClass::isOrange).and(MyClass::isRound))</code> vs.
 * <code>stream.filter(predicate(MyClass::isOrange).and(MyClass::isRound))</code>
 *
 * @author Leshana
 */
public class LambdaUtils {

    /**
     * Returns a function that always returns null.
     *
     * @param <P> the type of the input. Can be anything.
     * @param <R> the type of the output. Can be anything.
     * @return a function that always returns null.
     */
    public static <P, R> Function<P, R> alwaysNull() {
        return (Function<P, R> & Serializable) (p -> null);
    }

    /**
     * Syntax sugar for casting a lambda to {@link Function}.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @param function The {@code Function}
     * @return {@code function}
     */
    public static <T, R> Function<T, R> function(Function<T, R> function) {
        return function;
    }

    /**
     * Syntax sugar for casting a lambda to {@link Predicate}.
     *
     * @param <T> the type of the input to the predicate
     * @param predicate The {@code Predicate}
     * @return {@code predicate}
     */
    public static <T> Predicate<T> predicate(Predicate<T> predicate) {
        return predicate;
    }

    /**
     * Syntax sugar for casting a lambda to {@link Consumer}.
     *
     * @param <T> the type of the input to the consumer.
     * @param consumer The {@code Consumer}
     * @return {@code consumer}
     */
    public static <T> Consumer<T> consumer(Consumer<T> consumer) {
        return consumer;
    }

    /**
     * Accepts a function that extracts a collection from a type {@code T}, and returns a function that extracts a
     * stream of that collection from the type. Except it is also null-safe - if {@code collectionExtrator} returns
     * null, the function will return an empty stream. This is intended as a convenience for use with
     * {@link Stream#flatMap(Function)}.
     *
     * @param <T> the type of element to extract collection from (for flatMap() this is the type of the stream)
     * @param <R> the type of the collection contents.
     * @param collectionExtractor
     * @return
     */
    public static <T, R> Function<T, Stream<R>> streamOf(Function<? super T, ? extends Collection<R>> collectionExtractor) {
        return (T input) -> {
            Collection<R> collection = collectionExtractor.apply(input);
            return collection == null ? Stream.empty() : collection.stream();
        };
    }

    /**
     * Accepts a function that extracts a comparison key from a type {@code T}, and returns a {@code Predicate<T>} that
     * filters by that comparison key using the specified {@link Predicate}.
     *
     * The returned predicate is serializable if the specified function and predicate are both serializable.
     *
     * @param <T> the type of element to be tested
     * @param <U> the type of the sort key
     * @param keyExtractor the function used to extract the key
     * @param predicate the {@code Predicate} used to compare the extracted key.
     * @return a predicate that tests by an extracted key using the specified {@code Predicate}
     * @throws NullPointerException if either argument is null
     */
    public static <T, U> Predicate<T> testing(Function<? super T, ? extends U> keyExtractor,
            Predicate<? super U> predicate) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(predicate);
        return (Predicate<T> & Serializable) (t) -> predicate.test(keyExtractor.apply(t));
    }

    /**
     * The the {@code double}-consuming primitive type specialization of {@link #testing(Function, Predicate).
     *
     * @param <T> the type of element to be tested
     * @param keyExtractor the function used to extract the key
     * @param predicate the {@code Predicate} used to compare the extracted key.
     * @return a predicate that tests by an extracted key using the specified {@code DoublePredicate}
     * @throws NullPointerException if either argument is null
     */
    public static <T> Predicate<T> testing(ToDoubleFunction<? super T> keyExtractor, DoublePredicate predicate) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(predicate);
        return (Predicate<T> & Serializable) (t) -> predicate.test(keyExtractor.applyAsDouble(t));
    }

    /**
     * The the {@code int}-consuming primitive type specialization of {@link #testing(Function, Predicate).
     *
     * @param <T> the type of element to be tested
     * @param keyExtractor the function used to extract the key
     * @param predicate the {@code Predicate} used to compare the extracted key.
     * @return a predicate that tests by an extracted key using the specified {@code IntPredicate}
     * @throws NullPointerException if either argument is null
     */
    public static <T> Predicate<T> testing(ToIntFunction<? super T> keyExtractor, IntPredicate predicate) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(predicate);
        return (Predicate<T> & Serializable) (t) -> predicate.test(keyExtractor.applyAsInt(t));
    }

    /**
     * The the {@code long}-consuming primitive type specialization of {@link #testing(Function, Predicate).
     *
     * @param <T> the type of element to be tested
     * @param keyExtractor the function used to extract the key
     * @param predicate the {@code Predicate} used to compare the extracted key.
     * @return a predicate that tests by an extracted key using the specified {@code LongPredicate}
     * @throws NullPointerException if either argument is null
     */
    public static <T> Predicate<T> testing(ToLongFunction<? super T> keyExtractor, LongPredicate predicate) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(predicate);
        return (Predicate<T> & Serializable) (t) -> predicate.test(keyExtractor.applyAsLong(t));
    }

    /**
     * Returns a merge function, suitable for use in {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
     * {@link Collectors#toMap(Function, Function, BinaryOperator) toMap()}, which always throws
     * {@code IllegalStateException}. This can be used to enforce the assumption that the elements being collected are
     * distinct.
     *
     * @param <T> the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     * @see Copied from {@link Collectors} because it is private.
     */
    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    /**
     * Convenience method which calls {@link Collectors#toMap(Function, Function, BinaryOperator, Supplier)} but with
     * the default merge behavior as {@link Collectors#toMap(Function, Function)}.
     *
     * @apiNote This is useful for collecting in sorted maps with a custom comparator.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <V> the output type of the value mapping function
     * @param <M> the type of the resulting {@code Map}
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between values associated with the same key, as
     * supplied to {@link Map#merge(Object, Object, BiFunction)}
     * @param mapSupplier a function which returns a new, empty {@code Map} into which the results will be inserted
     * @return Return value of Collectors.ToMap()
     */
    public static <T, K, V, M extends Map<K, V>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper,
            Supplier<M> mapSupplier) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
    }

}
