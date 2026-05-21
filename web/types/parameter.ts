export default interface Parameter {
  id: number;
  name: string;
  shortName: string;
  measureUnitId: number | null;
}

export const NullParameterId = "Не выбрано" as const;
