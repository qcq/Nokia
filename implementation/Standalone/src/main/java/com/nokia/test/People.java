package com.nokia.test;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class People {
  public String name;
  private Integer age;
  public void setAge(@Nonnull Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
          .add("name", name)
          .add("age", age)
          .toString();
  }
}
