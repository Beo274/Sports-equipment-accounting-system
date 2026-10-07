export default interface CreateEnumerationValueDto {
  enumerationId: number;
  intValue: number | null;
  stringValue: string | null;
  imageValue: string | null;
  measureId: number | null;
}
