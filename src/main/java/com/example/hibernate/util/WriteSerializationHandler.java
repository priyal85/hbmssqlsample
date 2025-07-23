package com.example.hibernate.util;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;


public final class WriteSerializationHandler {
  private final ObjectOutput objectOutput;
  private final Map<String, Object> propertyMap;

  private WriteSerializationHandler(ObjectOutput objectOutput) {
    this.objectOutput = objectOutput;
    this.propertyMap = new HashMap();
  }

  public static WriteSerializationHandler createWithObjectOutput(ObjectOutput objectOutput) {
    return new WriteSerializationHandler(objectOutput);
  }

  public void put(String fieldName, Object fieldValue) {
    this.propertyMap.put(fieldName, fieldValue);
  }

//  public void put(String fieldName, Element fieldValue) throws IOException {
//    this.propertyMap.put(fieldName, IOUtil.getDocumentElementAsString(fieldValue));
//  }

  public void write() throws IOException {
    this.objectOutput.writeObject(this.propertyMap);
  }
}

