import MeasureUnit from "./measureUnit";

export interface Category {
  id: number;
  name: string;
  shortName: string;
  baseClassId: number | null;
  baseClass: Category | null;
  munitId?: number | null;
}

export interface CategoryWithMeasure extends Category {
  measure?: MeasureUnit;
}

export interface CategoryWithLevel extends Category {
  level: number;
}
