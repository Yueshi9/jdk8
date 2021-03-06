/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.util.stream;

import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/**
 * A sequence of primitive long elements supporting sequential and parallel
 * bulk operations. Streams support lazy intermediate operations (transforming
 * a stream to another stream) such as {@code filter} and {@code map}, and terminal
 * operations (consuming the contents of a stream to produce a result or
 * side-effect), such as {@code forEach}, {@code findFirst}, and {@code
 * iterator}.  Once an operation has been performed on a stream, it
 * is considered <em>consumed</em> and no longer usable for other operations.
 *
 * <p>For sequential stream pipelines, all operations are performed in the
 * <a href="package-summary.html#Ordering">encounter order</a> of the pipeline
 * source, if the pipeline source has a defined encounter order.
 *
 * <p>For parallel stream pipelines, unless otherwise specified, intermediate
 * stream operations preserve the <a href="package-summary.html#Ordering">
 * encounter order</a> of their source, and terminal operations
 * respect the encounter order of their source, if the source
 * has an encounter order.  Provided that and parameters to stream operations
 * satisfy the <a href="package-summary.html#NonInterference">non-interference
 * requirements</a>, and excepting differences arising from the absence of
 * a defined encounter order, the result of a stream pipeline should be the
 * stable across multiple executions of the same operations on the same source.
 * However, the timing and thread in which side-effects occur (for those
 * operations which are allowed to produce side-effects, such as
 * {@link #forEach(LongConsumer)}), are explicitly nondeterministic for parallel
 * execution of stream pipelines.
 *
 * <p>Unless otherwise noted, passing a {@code null} argument to any stream
 * method may result in a {@link NullPointerException}.
 *
 * @apiNote
 * Streams are not data structures; they do not manage the storage for their
 * elements, nor do they support access to individual elements.  However,
 * you can use the {@link #iterator()} or {@link #spliterator()} operations to
 * perform a controlled traversal.
 *
 * @since 1.8
 * @see <a href="package-summary.html">java.util.stream</a>
 */
public interface LongStream extends BaseStream<Long, LongStream> {

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">
     *                  non-interfering, stateless</a> predicate to apply to
     *                  each element to determine if it should be included
     * @return the new stream
     */
    LongStream filter(LongPredicate predicate);

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">
     *               non-interfering, stateless</a> function to apply to each
     *               element
     * @return the new stream
     */
    LongStream map(LongUnaryOperator mapper);

    /**
     * Returns an object-valued {@code Stream} consisting of the results of
     * applying the given function to the elements of this stream.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">
     *     intermediate operation</a>.
     *
     * @param <U> the element type of the new stream
     * @param mapper a <a href="package-summary.html#NonInterference">
     *               non-interfering, stateless</a> function to apply to each
     *               element
     * @return the new stream
     */
    <U> Stream<U> mapToObj(LongFunction<? extends U> mapper);

    /**
     * Returns an {@code IntStream} consisting of the results of applying the
     * given function to the elements of this stream.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">
     *               non-interfering, stateless</a> function to apply to each
     *               element
     * @return the new stream
     */
    IntStream mapToInt(LongToIntFunction mapper);

    /**
     * Returns a {@code DoubleStream} consisting of the results of applying the
     * given function to the elements of this stream.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @param mapper a <a href="package-summary.html#NonInterference">
     *               non-interfering, stateless</a> function to apply to each
     *               element
     * @return the new stream
     */
    DoubleStream mapToDouble(LongToDoubleFunction mapper);

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of the stream produced by applying the
     * provided mapping function to each element.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * @apiNote
     * The {@code flatMap()} operation has the effect of applying a one-to-many
     * tranformation to the elements of the stream, and then flattening the
     * resulting elements into a new stream. For example, if {@code orders}
     * is a stream of purchase orders, and each purchase order contains a
     * collection of line items, then the following produces a stream of line
     * items:
     * <pre>{@code
     *     orderStream.flatMap(order -> order.getLineItems().stream())...
     * }</pre>
     *
     * @param mapper a <a href="package-summary.html#NonInterference">
     *               non-interfering, stateless</a> function to apply to
     *               each element which produces an {@code LongStream} of new
     *               values
     * @return the new stream
     * @see Stream#flatMap(Function)
     */
    LongStream flatMap(LongFunction<? extends LongStream> mapper);

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @return the new stream
     */
    LongStream distinct();

    /**
     * Returns a stream consisting of the elements of this stream in sorted
     * order.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @return the new stream
     */
    LongStream sorted();

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream.
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     *
     * <p>For parallel stream pipelines, the action may be called at
     * whatever time and in whatever thread the element is made available by the
     * upstream operation.  If the action modifies shared state,
     * it is responsible for providing the required synchronization.
     *
     * @apiNote This method exists mainly to support debugging, where you want
     * to see the elements as they flow past a certain point in a pipeline:
     * <pre>{@code
     *     list.stream()
     *         .filter(filteringFunction)
     *         .peek(e -> {System.out.println("Filtered value: " + e); });
     *         .map(mappingFunction)
     *         .peek(e -> {System.out.println("Mapped value: " + e); });
     *         .collect(Collectors.toLongSummaryStastistics());
     * }</pre>
     *
     * @param consumer a <a href="package-summary.html#NonInterference">
     *                 non-interfering</a> action to perform on the elements as
     *                 they are consumed from the stream
     * @return the new stream
     */
    LongStream peek(LongConsumer consumer);

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * stateful intermediate operation</a>.
     *
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    LongStream limit(long maxSize);

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after indexing {@code startInclusive} elements into the stream. If the
     * {@code startInclusive} index lies past the end of this stream then an
     * empty stream will be returned.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     *
     * @param startInclusive the number of leading elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code startInclusive} is negative
     */
    LongStream substream(long startInclusive);

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after indexing {@code startInclusive} elements into the stream and
     * truncated to contain no more than {@code endExclusive - startInclusive}
     * elements. If the {@code startInclusive} index lies past the end
     * of this stream then an empty stream will be returned.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * stateful intermediate operation</a>.
     *
     * @param startInclusive the starting position of the substream, inclusive
     * @param endExclusive the ending position of the substream, exclusive
     * @return the new stream
     * @throws IllegalArgumentException if {@code startInclusive} or
     * {@code endExclusive} is negative or {@code startInclusive} is greater
     * than {@code endExclusive}
     */
    LongStream substream(long startInclusive, long endExclusive);

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * <p>For parallel stream pipelines, this operation does <em>not</em>
     * guarantee to respect the encounter order of the stream, as doing so
     * would sacrifice the benefit of parallelism.  For any given element, the
     * action may be performed at whatever time and in whatever thread the
     * library chooses.  If the action accesses shared state, it is
     * responsible for providing the required synchronization.
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     */
    void forEach(LongConsumer action);

    /**
     * Performs an action for each element of this stream, guaranteeing that
     * each element is processed in encounter order for streams that have a
     * defined encounter order.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     * @see #forEach(LongConsumer)
     */
    void forEachOrdered(LongConsumer action);

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return an array containing the elements of this stream
     */
    long[] toArray();

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using the provided identity value and an
     * <a href="package-summary.html#Associativity">associative</a>
     * accumulation function, and returns the reduced value.  This is equivalent
     * to:
     * <pre>{@code
     *     long result = identity;
     *     for (long element : this stream)
     *         result = accumulator.apply(result, element)
     *     return result;
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @apiNote Sum, min, max, and average are all special cases of reduction.
     * Summing a stream of numbers can be expressed as:
     *
     * <pre>{@code
     *     long sum = integers.reduce(0, (a, b) -> a+b);
     * }</pre>
     *
     * or more compactly:
     *
     * <pre>{@code
     *     long sum = integers.reduce(0, Long::sum);
     * }</pre>
     *
     * <p>While this may seem a more roundabout way to perform an aggregation
     * compared to simply mutating a running total in a loop, reduction
     * operations parallelize more gracefully, without needing additional
     * synchronization and with greatly reduced risk of data races.
     *
     * @param identity the identity value for the accumulating function
     * @param op an <a href="package-summary.html#Associativity">associative</a>
     *                    <a href="package-summary.html#NonInterference">non-interfering,
     *                    stateless</a> function for combining two values
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     * @see #average()
     */
    long reduce(long identity, LongBinaryOperator op);

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using an
     * <a href="package-summary.html#Associativity">associative</a> accumulation
     * function, and returns an {@code OptionalLong} describing the reduced value,
     * if any. This is equivalent to:
     * <pre>{@code
     *     boolean foundAny = false;
     *     long result = null;
     *     for (long element : this stream) {
     *         if (!foundAny) {
     *             foundAny = true;
     *             result = element;
     *         }
     *         else
     *             result = accumulator.apply(result, element);
     *     }
     *     return foundAny ? OptionalLong.of(result) : OptionalLong.empty();
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     *
     * <p>The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param op an <a href="package-summary.html#Associativity">associative</a>
     *           <a href="package-summary.html#NonInterference">non-interfering,
     *           stateless</a> function for combining two values
     * @return the result of the reduction
     * @see #reduce(long, LongBinaryOperator)
     */
    OptionalLong reduce(LongBinaryOperator op);

    /**
     * Performs a <a href="package-summary.html#MutableReduction">mutable
     * reduction</a> operation on the elements of this stream.  A mutable
     * reduction is one in which the reduced value is a mutable value holder,
     * such as an {@code ArrayList}, and elements are incorporated by updating
     * the state of the result, rather than by replacing the result.  This
     * produces a result equivalent to:
     * <pre>{@code
     *     R result = resultFactory.get();
     *     for (long element : this stream)
     *         accumulator.accept(result, element);
     *     return result;
     * }</pre>
     *
     * <p>Like {@link #reduce(long, LongBinaryOperator)}, {@code collect} operations
     * can be parallelized without requiring additional synchronization.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @param <R> type of the result
     * @param resultFactory a function that creates a new result container.
     *                      For a parallel execution, this function may be
     *                      called multiple times and must return a fresh value
     *                      each time.
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>
     *                    <a href="package-summary.html#NonInterference">non-interfering,
     *                    stateless</a> function for incorporating an additional
     *                    element into a result
     * @param combiner an <a href="package-summary.html#Associativity">associative</a>
     *                 <a href="package-summary.html#NonInterference">non-interfering,
     *                 stateless</a> function for combining two values, which
     *                 must be compatible with the accumulator function
     * @return the result of the reduction
     * @see Stream#collect(Supplier, BiConsumer, BiConsumer)
     */
    <R> R collect(Supplier<R> resultFactory,
                  ObjLongConsumer<R> accumulator,
                  BiConsumer<R, R> combiner);

    /**
     * Returns the sum of elements in this stream.  This is a special case
     * of a <a href="package-summary.html#MutableReduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(0, Long::sum);
     * }</pre>
     *
     * @return the sum of elements in this stream
     */
    long sum();

    /**
     * Returns an {@code OptionalLong} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.  This is a special
     * case of a <a href="package-summary.html#MutableReduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(Long::min);
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     *

     * @return an {@code OptionalLong} containing the minimum element of this
     * stream, or an empty {@code OptionalLong} if the stream is empty
     */
    OptionalLong min();

    /**
     * Returns an {@code OptionalLong} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.  This is a special
     * case of a <a href="package-summary.html#MutableReduction">reduction</a>
     * and is equivalent to:
     * <pre>{@code
     *     return reduce(Long::max);
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return an {@code OptionalLong} containing the maximum element of this
     * stream, or an empty {@code OptionalLong} if the stream is empty
     */
    OptionalLong max();

    /**
     * Returns the count of elements in this stream.  This is a special case of
     * a <a href="package-summary.html#MutableReduction">reduction</a> and is
     * equivalent to:
     * <pre>{@code
     *     return map(e -> 1L).sum();
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     *
     * @return the count of elements in this stream
     */
    long count();

    /**
     * Returns an {@code OptionalDouble} describing the average of elements of
     * this stream, or an empty optional if this stream is empty.  This is a
     * special case of a
     * <a href="package-summary.html#MutableReduction">reduction</a>.
     *
     * @return an {@code OptionalDouble} containing the average element of this
     * stream, or an empty optional if the stream is empty
     */
    OptionalDouble average();

    /**
     * Returns a {@code LongSummaryStatistics} describing various summary data
     * about the elements of this stream.  This is a special case of a
     * <a href="package-summary.html#MutableReduction">reduction</a>.
     *
     * @return a {@code LongSummaryStatistics} describing various summary data
     * about the elements of this stream
     */
    LongSummaryStatistics summaryStatistics();

    /**
     * Returns whether any elements of this stream match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering,
     *                  stateless</a> predicate to apply to elements of this
     *                  stream
     * @return {@code true} if any elements of the stream match the provided
     * predicate otherwise {@code false}
     */
    boolean anyMatch(LongPredicate predicate);

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering,
     *                  stateless</a> predicate to apply to elements of this
     *                  stream
     * @return {@code true} if all elements of the stream match the provided
     * predicate otherwise {@code false}
     */
    boolean allMatch(LongPredicate predicate);

    /**
     * Returns whether no elements of this stream match the provided  predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering,
     *                  stateless</a> predicate to apply to elements of this
     *                  stream
     * @return {@code true} if no elements of the stream match the provided
     * predicate otherwise {@code false}
     */
    boolean noneMatch(LongPredicate predicate);

    /**
     * Returns an {@link OptionalLong} describing the first element of this
     * stream (in the encounter order), or an empty {@code OptionalLong} if the
     * stream is empty.  If the stream has no encounter order, then any element
     * may be returned.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @return an {@code OptionalLong} describing the first element of this
     * stream, or an empty {@code OptionalLong} if the stream is empty
     */
    OptionalLong findFirst();

    /**
     * Returns an {@link OptionalLong} describing some element of the stream, or
     * an empty {@code OptionalLong} if the stream is empty.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * <p>The behavior of this operation is explicitly nondeterministic; it is
     * free to select any element in the stream.  This is to allow for maximal
     * performance in parallel operations; the cost is that multiple invocations
     * on the same source may not return the same result.  (If the first element
     * in the encounter order is desired, use {@link #findFirst()} instead.)
     *
     * @return an {@code OptionalLong} describing some element of this stream,
     * or an empty {@code OptionalLong} if the stream is empty
     * @see #findFirst()
     */
    OptionalLong findAny();

    /**
     * Returns a {@code DoubleStream} consisting of the elements of this stream,
     * converted to {@code double}.
     *
     * @return a {@code DoubleStream} consisting of the elements of this stream,
     * converted to {@code double}
     */
    DoubleStream asDoubleStream();

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to a {@code Long}.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     * each boxed to {@code Long}
     */
    Stream<Long> boxed();

    @Override
    LongStream sequential();

    @Override
    LongStream parallel();

    @Override
    PrimitiveIterator.OfLong iterator();

    @Override
    Spliterator.OfLong spliterator();

    // Static factories

    /**
     * Returns a builder for a {@code LongStream}.
     *
     * @return a stream builder
     */
    public static Builder builder() {
        return new Streams.LongStreamBuilderImpl();
    }

    /**
     * Returns an empty sequential {@code LongStream}.
     *
     * @return an empty sequential stream
     */
    public static LongStream empty() {
        return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
    }

    /**
     * Returns a sequential {@code LongStream} containing a single element.
     *
     * @param t the single element
     * @return a singleton sequential stream
     */
    public static LongStream of(long t) {
        return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(t), false);
    }

    /**
     * Returns a sequential stream whose elements are the specified values.
     *
     * @param values the elements of the new stream
     * @return the new stream
     */
    public static LongStream of(long... values) {
        return Arrays.stream(values);
    }

    /**
     * Returns an infinite sequential {@code LongStream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * <p>The first element (position {@code 0}) in the {@code LongStream} will
     * be the provided {@code seed}.  For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * @param seed the initial element
     * @param f a function to be applied to to the previous element to produce
     *          a new element
     * @return a new sequential {@code LongStream}
     */
    public static LongStream iterate(final long seed, final LongUnaryOperator f) {
        Objects.requireNonNull(f);
        final PrimitiveIterator.OfLong iterator = new PrimitiveIterator.OfLong() {
            long t = seed;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public long nextLong() {
                long v = t;
                t = f.applyAsLong(t);
                return v;
            }
        };
        return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    /**
     * Returns a sequential {@code LongStream} where each element is generated
     * by a {@code LongSupplier}.  This is suitable for generating constant
     * streams, streams of random elements, etc.
     *
     * @param s the {@code LongSupplier} for generated elements
     * @return a new sequential {@code LongStream}
     */
    public static LongStream generate(LongSupplier s) {
        Objects.requireNonNull(s);
        return StreamSupport.longStream(
                new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, s), false);
    }

    /**
     * Returns a sequential {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (long i = startInclusive; i < endExclusive ; i++) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    public static LongStream range(long startInclusive, final long endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        } else if (endExclusive - startInclusive < 0) {
            // Size of range > Long.MAX_VALUE
            // Split the range in two and concatenate
            // Note: if the range is [Long.MIN_VALUE, Long.MAX_VALUE) then
            // the lower range, [Long.MIN_VALUE, 0) will be further split in two
            long m = startInclusive + Long.divideUnsigned(endExclusive - startInclusive, 2) + 1;
            return concat(range(startInclusive, m), range(m, endExclusive));
        } else {
            return StreamSupport.longStream(
                    new Streams.RangeLongSpliterator(startInclusive, endExclusive, false), false);
        }
    }

    /**
     * Returns a sequential {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (long i = startInclusive; i <= endInclusive ; i++) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    public static LongStream rangeClosed(long startInclusive, final long endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else if (endInclusive - startInclusive + 1 <= 0) {
            // Size of range > Long.MAX_VALUE
            // Split the range in two and concatenate
            // Note: if the range is [Long.MIN_VALUE, Long.MAX_VALUE] then
            // the lower range, [Long.MIN_VALUE, 0), and upper range,
            // [0, Long.MAX_VALUE], will both be further split in two
            long m = startInclusive + Long.divideUnsigned(endInclusive - startInclusive, 2) + 1;
            return concat(range(startInclusive, m), rangeClosed(m, endInclusive));
        } else {
            return StreamSupport.longStream(
                    new Streams.RangeLongSpliterator(startInclusive, endInclusive, true), false);
        }
    }

    /**
     * Creates a lazy concatenated {@code LongStream} whose elements are all the
     * elements of a first {@code LongStream} succeeded by all the elements of the
     * second {@code LongStream}. The resulting stream is ordered if both
     * of the input streams are ordered, and parallel if either of the input
     * streams is parallel.
     *
     * @param a the first stream
     * @param b the second stream to concatenate on to end of the first stream
     * @return the concatenation of the two streams
     */
    public static LongStream concat(LongStream a, LongStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        Spliterator.OfLong split = new Streams.ConcatSpliterator.OfLong(
                a.spliterator(), b.spliterator());
        return StreamSupport.longStream(split, a.isParallel() || b.isParallel());
    }

    /**
     * A mutable builder for a {@code LongStream}.
     *
     * <p>A stream builder has a lifecycle, where it starts in a building
     * phase, during which elements can be added, and then transitions to a
     * built phase, after which elements may not be added.  The built phase
     * begins when the {@link #build()} method is called, which creates an
     * ordered stream whose elements are the elements that were added to the
     * stream builder, in the order they were added.
     *
     * @see LongStream#builder()
     * @since 1.8
     */
    public interface Builder extends LongConsumer {

        /**
         * Adds an element to the stream being built.
         *
         * @throws IllegalStateException if the builder has already transitioned
         * to the built state
         */
        @Override
        void accept(long t);

        /**
         * Adds an element to the stream being built.
         *
         * @implSpec
         * The default implementation behaves as if:
         * <pre>{@code
         *     accept(t)
         *     return this;
         * }</pre>
         *
         * @param t the element to add
         * @return {@code this} builder
         * @throws IllegalStateException if the builder has already transitioned
         * to the built state
         */
        default Builder add(long t) {
            accept(t);
            return this;
        }

        /**
         * Builds the stream, transitioning this builder to the built state.
         * An {@code IllegalStateException} is thrown if there are further
         * attempts to operate on the builder after it has entered the built
         * state.
         *
         * @return the built stream
         * @throws IllegalStateException if the builder has already transitioned
         * to the built state
         */
        LongStream build();
    }
}
