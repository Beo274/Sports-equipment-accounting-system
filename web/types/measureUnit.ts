export default interface MeasureUnit {
  id: number;
  name: string;
  shortName: string;
}

export const NullMeasureUnit = "Без е. и." as const;
