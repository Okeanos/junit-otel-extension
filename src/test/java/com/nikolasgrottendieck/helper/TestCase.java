package com.nikolasgrottendieck.helper;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denote TestCases that are to be evaluated by the EngineTestKit only. Other test detection mechanisms should not execute them.
 * This includes for example IntelliJ and Maven Surefire.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(com.nikolasgrottendieck.helper.TestCaseCondition.class)
public @interface TestCase {
}
