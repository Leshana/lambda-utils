package com.leshna.lambdautils;

import com.leshana.lambdautils.LambdaUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.Test;

import static com.leshana.lambdautils.LambdaUtils.streamOf;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test the non-trivial methods of LambdaUtils.
 *
 * @author Leshana
 */
public class LambdaUtilsTest {

    @Test
    public void testAlwaysNull() {
        assertNull(LambdaUtils.alwaysNull().apply(null));
        assertNull(LambdaUtils.alwaysNull().apply(new Object()));
    }

    @Test
    public void testStreamOf() {
        Widget a = new Widget("A");
        Widget b = new Widget("B");
        Widget c = new Widget("C");
        Warehouse w1 = new Warehouse(Arrays.asList(a, b));
        Warehouse w2 = new Warehouse(Arrays.asList(c));
        Warehouse w3 = new Warehouse(null);
        List<Widget> widgets = Stream.of(w1, w2, w3).flatMap(streamOf(Warehouse::getWidgets)).collect(toList());
        assertThat(widgets, contains(a, b, c));
    }

    @Test
    public void testTesting() {
        Widget a = new Widget("A");
        Widget b = new Widget("B");
        Widget n = new Widget(null);
        Predicate<Widget> target = LambdaUtils.testing(Widget::getName, isEqual("A"));
        assertTrue(target.test(a));
        assertFalse(target.test(b));
        assertFalse(target.test(n));
    }

    @Test(expected = NullPointerException.class)
    public void testTesting_NullExtractor() {
        LambdaUtils.testing(null, isEqual("A"));
    }

    @Test(expected = NullPointerException.class)
    public void testTesting_NullPredicate() {
        LambdaUtils.testing(Widget::getName, null);
    }

    @Test
    public void testToMap() {
        Widget lowerA = new Widget("a");
        Widget capB = new Widget("B");
        Map<String, Widget> actual = Stream.of(capB, lowerA).collect(LambdaUtils.toMap(Widget::getName, Function.identity(), () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
        assertSame(lowerA, actual.get("A"));
        assertSame(lowerA, actual.get("a"));
        assertSame(capB, actual.get("b"));
    }

    @Test(expected = IllegalStateException.class)
    public void testToMap_DuplicateKey() {
        Widget lowerA = new Widget("a");
        Widget capA = new Widget("A");
        Widget capB = new Widget("B");
        Stream.of(capB, lowerA, capA).collect(LambdaUtils.toMap(Widget::getName, Function.identity(), () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
    }

    /**
     * Simple POJO for testing extractor functions.
     */
    public static class Widget {

        private final String name;

        public Widget(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Widget{" + name + '}';
        }

    }

    /**
     * Simple POJO with a List for testing flatMap.
     */
    public static class Warehouse {

        private final List<Widget> widgets;

        public Warehouse(List<Widget> widgets) {
            this.widgets = widgets;
        }

        public List<Widget> getWidgets() {
            return widgets;
        }

    }

}
