package de.mkammerer.sq2p.util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble extends Number {
  /**
   * We can convert via {@link Double#doubleToLongBits(double)} from double to long and with
   * {@link Double#longBitsToDouble(long)} from long to double.
   * <p>
   * We use all the fancy atomicity from the atomic long and just do bit conversions.
   */
  private final AtomicLong bits;

  public AtomicDouble(double value) {
    this.bits = new AtomicLong(Double.doubleToLongBits(value));
  }

  public void set(double value) {
    bits.set(Double.doubleToLongBits(value));
  }

  public double get() {
    return Double.longBitsToDouble(bits.get());
  }

  @Override
  public int intValue() {
    return (int) get();
  }

  @Override
  public long longValue() {
    return (long) get();
  }

  @Override
  public float floatValue() {
    return (float) get();
  }

  @Override
  public double doubleValue() {
    return get();
  }
}
