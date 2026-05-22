import { EnumerationValue } from "./enumeration";
import Parameter from "./parameter";

interface EntityParameter {
  id: number;
  param: Parameter;
  enumValue: EnumerationValue | null;
  maxVal: number | null;
  minVal: number | null;
  intVal: number | null;
}

export interface ClassParameter extends EntityParameter {
  classId: number;
}

export interface ProductParameter extends EntityParameter {
  productId: number;
}
