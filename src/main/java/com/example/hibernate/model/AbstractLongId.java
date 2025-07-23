package com.example.hibernate.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.example.hibernate.util.ReadSerializationHandler;
import com.example.hibernate.util.WriteSerializationHandler;

public abstract class AbstractLongId implements Externalizable, Comparable {
  private static final long serialVersionUID = 1L;
  private long value;

  public AbstractLongId() {
  }

  public AbstractLongId(long value) {
    this.value = value;
  }

  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + (int)(this.value ^ this.value >>> 32);
    return result;
  }

  public int compareTo(Object toCompareWith) {
    return Long.compare(this.value, ((AbstractLongId)toCompareWith).value);
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      AbstractLongId other = (AbstractLongId)obj;
      return this.value == other.value;
    }
  }

  public final String toString() {
    return Long.toString(this.getValue());
  }



  public long getValue() {
    return this.value;
  }

  public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
    ReadSerializationHandler readSerializationHandler = ReadSerializationHandler.createWithObjectInput(oi);
    this.value = readSerializationHandler.getLongPrimitive("value");
  }

  public void writeExternal(ObjectOutput oo) throws IOException {
    WriteSerializationHandler writeSerializationHandler = WriteSerializationHandler.createWithObjectOutput(oo);
    writeSerializationHandler.put("value", this.value);
    writeSerializationHandler.write();
  }
}