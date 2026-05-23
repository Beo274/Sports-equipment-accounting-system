import MeasureUnit from "./measureUnit";

export default interface Parameter {
  id: number;
  name: string;
  shortName: string;
  measure: MeasureUnit | null;
}

export const NullParameterId = "Не выбрано" as const;
