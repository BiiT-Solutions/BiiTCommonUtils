package com.biit.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Suppress FindBugs warnings on the annotated element. FindBugs will recognize
 * any annotation that has class retention and whose name ends with
 * "SuppressWarnings".
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.CLASS)
public @interface FindBugsSuppressWarnings {
    /**
     * The <a href="http://findbugs.sourceforge.net/bugDescriptions.html">FindBugs
     * Patterns</a> to suppress, such as {@code SE_TRANSIENT_FIELD_NOT_RESTORED}
     * or {@code Se}. Full, upper case names are preferred.
     */
    String[] value();
}
