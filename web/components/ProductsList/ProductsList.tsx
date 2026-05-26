"use client";

import { useStore } from "@/lib/store/store";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemGroup,
  ItemHeader,
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
import { ScrollArea } from "../ui/scroll-area";

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
      clearItems,
    },
  } = useStore();
  const [isAddParamOpen, setAddParamOpen] = useState(false);
  const [isListParamsOpen, setListParamOpen] = useState(false);

  useEffect(() => {
    fetchProducts();

    return () => {
      clearItems();
    };
  }, []);

  return (
    <div className="h-full flex flex-col min-h-0">
      <h3 className="p-1 text-xl">Список изделий</h3>
      <div className="flex-1 min-h-0">
        {errors.fetchingError ? (
          <div className="h-full flex justify-center items-center">
            <ErrorLabel
              message={errors.fetchingError.message}
              onClearError={() => {
                clearError("fetchingError");
                fetchProducts();
              }}
            />
          </div>
        ) : isFetchLoading ? (
          <div className="h-full w-full flex justify-center items-center">
            <Loader />
          </div>
        ) : (
          <ScrollArea className="h-full max-h-115">
            {products.length ? (
              <ItemGroup className="pr-4 pb-3">
                {products.map((p) => (
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
                ))}
              </ItemGroup>
            ) : (
              <div className="flex justify-center items-center h-full">
                <p>Пусто...</p>
              </div>
            )}
          </ScrollArea>
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
    <Item className="max-w-sm border border-accent hover:shadow-lg hover:shadow-foreground transition-all p-0">
      <ItemHeader className="p-0 border border-accent rounded-t-md bg-accent">
        <ItemTitle className="p-1 w-full font-bold">{`${product.id}: ${product.name} (${product.shortName})`}</ItemTitle>
      </ItemHeader>
      <ItemContent className="flex-col p-1">
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
            className="cursor-pointer hover:bg-gray-50 rounded px-2 py-1"
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
            title="Удалить"
          >
            <Trash />
          </Button>
        </ItemActions>
      </ItemContent>
    </Item>
  );
}
