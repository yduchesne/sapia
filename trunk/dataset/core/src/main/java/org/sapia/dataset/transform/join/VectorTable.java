package org.sapia.dataset.transform.join;

class VectorTable {
  
  enum VectorType {
    LEFT,
    RIGHT;
  }

  private int[]        joinToVectorIndices;
  private VectorType[] vectorTypes;
  
  VectorTable(int[] joinToVectorIndices, VectorType[] vectorTypes) {
    this.joinToVectorIndices = joinToVectorIndices;
    this.vectorTypes         = vectorTypes;
  }
 
  int resolveVectorIndex(int index) {
    return joinToVectorIndices[index];
  }
  
  VectorType resolveVectorType(int index) {
    return vectorTypes[index];
  }
 
}
