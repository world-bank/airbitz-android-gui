/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.airbitz.api;

public class tABC_Currency {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected tABC_Currency(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(tABC_Currency obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        coreJNI.delete_tABC_Currency(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setSzCode(String value) {
    coreJNI.tABC_Currency_szCode_set(swigCPtr, this, value);
  }

  public String getSzCode() {
    return coreJNI.tABC_Currency_szCode_get(swigCPtr, this);
  }

  public void setNum(int value) {
    coreJNI.tABC_Currency_num_set(swigCPtr, this, value);
  }

  public int getNum() {
    return coreJNI.tABC_Currency_num_get(swigCPtr, this);
  }

  public void setSzDescription(String value) {
    coreJNI.tABC_Currency_szDescription_set(swigCPtr, this, value);
  }

  public String getSzDescription() {
    return coreJNI.tABC_Currency_szDescription_get(swigCPtr, this);
  }

  public void setSzCountries(String value) {
    coreJNI.tABC_Currency_szCountries_set(swigCPtr, this, value);
  }

  public String getSzCountries() {
    return coreJNI.tABC_Currency_szCountries_get(swigCPtr, this);
  }

  public tABC_Currency() {
    this(coreJNI.new_tABC_Currency(), true);
  }

}
