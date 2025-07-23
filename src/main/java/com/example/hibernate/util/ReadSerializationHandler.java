package com.example.hibernate.util;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Map;


public final class ReadSerializationHandler {
  private final Map<String, Object> propertyMap;

  private ReadSerializationHandler(ObjectInput oi) throws IOException, ClassNotFoundException {
    this.propertyMap = (Map)oi.readObject();
  }

  public static ReadSerializationHandler createWithObjectInput(ObjectInput oi) throws IOException, ClassNotFoundException {
    return new ReadSerializationHandler(oi);
  }

  private <T> T getValue(String fieldName) {
    return (T)this.getValue(fieldName, (Object)null);
  }

  private <T> T getValue(String fieldName, T def) {
    return (T)(this.propertyMap != null && this.propertyMap.size() > 0 ? this.propertyMap.getOrDefault(fieldName, def) : def);
  }

  public boolean getBooleanPrimitive(String fieldName) {
    return this.getBooleanPrimitive(fieldName, false);
  }

  public boolean getBooleanPrimitive(String fieldName, boolean def) {
    return (Boolean)this.getValue(fieldName, def);
  }

  public Boolean getBoolean(String fieldName) {
    return (Boolean)this.getValue(fieldName);
  }

  public byte getBytePrimitive(String fieldName) {
    return this.getBytePrimitive(fieldName, (byte)0);
  }

  public byte getBytePrimitive(String fieldName, byte def) {
    return (Byte)this.getValue(fieldName, def);
  }

  public Byte getByte(String fieldName) {
    return (Byte)this.getValue(fieldName);
  }

  public char getCharPrimitive(String fieldName) {
    return this.getCharPrimitive(fieldName, '\u0000');
  }

  public char getCharPrimitive(String fieldName, char def) {
    return (Character)this.getValue(fieldName, def);
  }

  public Character getChar(String fieldName) {
    return (Character)this.getValue(fieldName);
  }

  public double getDoublePrimitive(String fieldName) {
    return this.getDoublePrimitive(fieldName, (double)0.0F);
  }

  public double getDoublePrimitive(String fieldName, double def) {
    return (Double)this.getValue(fieldName, def);
  }

  public Double getDouble(String fieldName) {
    return (Double)this.getValue(fieldName);
  }

  public float getFloatPrimitive(String fieldName) {
    return this.getFloatPrimitive(fieldName, 0.0F);
  }

  public float getFloatPrimitive(String fieldName, float def) {
    return (Float)this.getValue(fieldName, def);
  }

  public Float getFloat(String fieldName) {
    return (Float)this.getValue(fieldName);
  }

  public int getIntPrimitive(String fieldName) {
    return this.getIntPrimitive(fieldName, 0);
  }

  public int getIntPrimitive(String fieldName, int def) {
    return (Integer)this.getValue(fieldName, def);
  }

  public Integer getInt(String fieldName) {
    return (Integer)this.getValue(fieldName);
  }

  public long getLongPrimitive(String fieldName) {
    return this.getLongPrimitive(fieldName, 0L);
  }

  public long getLongPrimitive(String fieldName, long def) {
    return (Long)this.getValue(fieldName, def);
  }

  public Long getLong(String fieldName) {
    return (Long)this.getValue(fieldName);
  }

  public short getShortPrimitive(String fieldName) {
    return this.getShortPrimitive(fieldName, (short)0);
  }

  public short getShortPrimitive(String fieldName, short def) {
    return (Short)this.getValue(fieldName, def);
  }

  public Short getShort(String fieldName) {
    return (Short)this.getValue(fieldName);
  }

  public String getString(String fieldName) {
    return (String)this.getValue(fieldName);
  }

//  public Element getXML(String fieldName) throws IOException {
//    return IOUtil.readDocumentElementString((String)this.getValue(fieldName));
//  }

  public Object getObject(String fieldName) {
    return this.getValue(fieldName);
  }
}

