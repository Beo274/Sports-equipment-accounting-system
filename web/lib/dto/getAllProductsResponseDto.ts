import Product from "@/types/product";

export default interface GetAllProductsResponseDto {
  total: number;
  limit: number;
  offset: number;
  items: ReadonlyArray<Product>;
}
