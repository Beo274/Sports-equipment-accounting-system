export interface CreateCategoryDto {
  name: string;
  shortName: string;
  baseClassId: number | null;
  measureUnitId: number | null;
}
