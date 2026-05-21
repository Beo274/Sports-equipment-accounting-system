interface CreateEntityParameterDto {
  paramId: number;
  enumValueId: number | null;
  maxVal: number | null;
  minVal: number | null;
}

export interface CreateClassParameterDto extends CreateEntityParameterDto {
  classId: number;
}

export interface CreateProductParameterDto extends CreateEntityParameterDto {
  productId: number;
}
