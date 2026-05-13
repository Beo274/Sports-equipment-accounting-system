import MeasureUnit from "./measureUnit";

export interface Category {
  id: number;
  name: string;
  shortName: string;
  baseClassId?: number;
  munitId?: number;
}

export interface CategoryWithMeasure extends Category {
  measure?: MeasureUnit;
}
