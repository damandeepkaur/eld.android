package com.bsmwireless.common.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;


/**
 * Dagger scope for per-activity dependencies.
 */
@Scope
@Retention(RetentionPolicy.CLASS)
public @interface ActivityScope {
}
