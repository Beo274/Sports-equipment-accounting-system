export interface Category {
  id: number;
  name: string;
  shortName: string;
  baseClassId: number | null;
  baseClass: Category | null;
  mUnitId: number | null;
}

export interface CategoryWithLevel extends Category {
  level: number;
}
