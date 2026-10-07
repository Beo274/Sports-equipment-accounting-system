import MeasureUnit from "./measureUnit";

export interface Enumeration {
  id: number;
  name: string;
  shortName: string;
}

export interface EnumerationValue {
  id: number;
  enumeration: Enumeration;
  intValue: number | null;
  stringValue: string | null;
  imageValue: string | null;
  measure: MeasureUnit | null;
  position: number | null;
}

export const NullEnumerationId = "Не выбрано" as const;
