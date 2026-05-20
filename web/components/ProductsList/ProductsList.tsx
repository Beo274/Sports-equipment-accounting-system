"use client";

import { useStore } from "@/lib/store/store";
import {
  Item,
  ItemActions,
  ItemDescription,
  ItemGroup,
  ItemTitle,
} from "../ui/item";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import Loader from "../Loader/Loader";
import Product from "@/types/product";
import { useEffect } from "react";

export default function ProductsList() {
  const {
    products: { isFetchLoading, errors, clearError, products, fetchProducts },
  } = useStore();

  useEffect(() => {
    fetchProducts();
  }, []);

  return (
    <div className="h-full">
      <h3 className="p-1 text-xl">Список изделийП</h3>
      <div className="flex justify-center items-center p-1 overflow-y-auto">
        {errors.fetchingError ? (
          <ErrorLabel
            message={errors.fetchingError.message}
            onClearError={() => {
              clearError("fetchingError");
              fetchProducts();
            }}
          />
        ) : isFetchLoading ? (
          <Item className="h-full w-full justify-center items-center">
            <Loader />
          </Item>
        ) : (
          <div className="w-full max-h-125">
            <ItemGroup>
              {products.map((p) => (
                <ProductItem key={p.id} product={p} />
              ))}
            </ItemGroup>
          </div>
        )}
      </div>
    </div>
  );
}

interface ProductItemProps {
  product: Product;
}

function ProductItem({ product }: ProductItemProps) {
  return (
    <Item className="border-2 border-foreground">
      <ItemTitle>{`${product.id}: ${product.name} (${product.shortName})`}</ItemTitle>
      <ItemDescription>{`ID базовой категории: ${product.classId}`}</ItemDescription>
      <ItemActions></ItemActions>
    </Item>
  );
}
