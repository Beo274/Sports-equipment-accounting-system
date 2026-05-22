"use client";

import { useStore } from "@/lib/store/store";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemGroup,
  ItemTitle,
} from "../ui/item";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import Loader from "../Loader/Loader";
import Product from "@/types/product";
import { KeyboardEvent, useEffect, useState } from "react";
import { Button } from "../ui/button";
import { Grid2x2Plus, TableProperties, Trash } from "lucide-react";
import AddParameterDialog from "../dialog/AddParameterDialog";
import ListParametersDialog from "../dialog/ListParametersDialog";

export default function ProductsList() {
  const {
    products: {
      isFetchLoading,
      errors,
      clearError,
      products,
      fetchProducts,
      deleteProduct,
      updateBaseClass,
      editingProduct,
      setEditingProduct,
    },
  } = useStore();
  const [isAddParamOpen, setAddParamOpen] = useState(false);
  const [isListParamsOpen, setListParamOpen] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, []);

  return (
    <div className="h-full">
      <div className="max-h-100 flex justify-center items-center p-1 overflow-y-auto">
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
              {products.length ? (
                products.map((p) => (
                  <ProductItem
                    key={p.id}
                    product={p}
                    handleDelete={async () => {
                      await deleteProduct(p.id);
                      fetchProducts();
                    }}
                    handleUpdateBaseClass={async (classId: number) => {
                      await updateBaseClass(p.id, classId);
                      fetchProducts();
                    }}
                    openAddParam={() => {
                      setAddParamOpen(true);
                      setEditingProduct(p);
                    }}
                    openListParam={() => {
                      setListParamOpen(true);
                      setEditingProduct(p);
                    }}
                  />
                ))
              ) : (
                <Item className="w-full h-full">
                  <ItemContent>Пусто...</ItemContent>
                </Item>
              )}
            </ItemGroup>
          </div>
        )}
      </div>
      <AddParameterDialog
        editingEntity={{ paramFor: "product", entity: editingProduct }}
        open={isAddParamOpen}
        onOpenChange={setAddParamOpen}
      />
      <ListParametersDialog
        dialogEntity={{ paramFor: "product", entity: editingProduct }}
        open={isListParamsOpen}
        onOpenChange={setListParamOpen}
      />
    </div>
  );
}

interface ProductItemProps {
  product: Product;

  handleDelete: () => Promise<void>;
  handleUpdateBaseClass: (classId: number) => Promise<void>;

  openAddParam: () => void;
  openListParam: () => void;
}

function ProductItem({
  product,
  handleDelete,
  handleUpdateBaseClass,
  openAddParam,
  openListParam,
}: ProductItemProps) {
  const {
    products: { isModifyLoading },
  } = useStore();
  const [isEditing, setEditing] = useState(false);
  const [editValue, setEditValue] = useState("");

  const startEditing = () => {
    setEditValue(String(product.classId));
    setEditing(true);
  };

  const saveEdit = async () => {
    const parsedValue = Number(editValue);
    if (
      !isNaN(parsedValue) &&
      parsedValue > 0 &&
      parsedValue !== product.classId
    ) {
      await handleUpdateBaseClass(parsedValue);
    }
    setEditing(false);
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      saveEdit();
    } else if (e.key === "Escape") {
      setEditing(false);
    }
  };

  return (
    <Item className="border-2 border-foreground">
      <ItemContent className="flex-col">
        <ItemTitle>{`${product.id}: ${product.name} (${product.shortName})`}</ItemTitle>
        {isEditing ? (
          <input
            type={"number"}
            value={editValue ?? ""}
            onChange={(e) => setEditValue(e.target.value)}
            onBlur={saveEdit}
            onKeyDown={handleKeyDown}
            className="border border-gray-300 rounded px-2 py-1 w-full focus:outline-none focus:border-accent"
            min={1}
            autoFocus
          />
        ) : (
          <div
            onClick={startEditing}
            className="cursor-pointer hover:bg-gray-50 rounded px-2 py-1 -mx-2 -my-1"
            title="Нажмите для редактирования"
          >
            <ItemDescription>
              {product.classId
                ? `ID базовой категории: ${product.classId}`
                : "Базовый класс не указан"}{" "}
              {}
            </ItemDescription>
          </div>
        )}
      </ItemContent>
      <ItemActions>
        <Button
          variant="ghost"
          className="hover:bg-accent"
          disabled={isModifyLoading}
          title="Добавить параметр"
          onClick={() => openAddParam()}
        >
          <Grid2x2Plus />
        </Button>
        <Button
          variant="ghost"
          className="hover:bg-accent"
          disabled={isModifyLoading}
          title="Посмотреть параметры"
          onClick={() => openListParam()}
        >
          <TableProperties />
        </Button>
        <Button
          variant="ghost"
          className="hover:bg-accent"
          disabled={isModifyLoading}
          onClick={handleDelete}
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}
